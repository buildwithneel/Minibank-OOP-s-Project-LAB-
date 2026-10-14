package service;
import exception.InsufficientFundsException;
import model.Account;
import model.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BankServiceTest {
    @Test
    void depositIncreasesBalance() throws Exception {
        Account account = new SavingsAccount("Test", 1000, 0);
        account.deposit(500);
        assertEquals(1500, account.getBalance());
    }
    @Test
    void excessiveWithdrawalThrows() {
        Account account = new SavingsAccount("Test", 1000, 0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(2000));
    }
    @Test
    void concurrentDepositsKeepBalanceCorrect() throws Exception {
        Account account = new SavingsAccount("Test", 0, 0);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    try {
                        account.deposit(1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) thread.join();
        assertEquals(10000, account.getBalance());
    }
}

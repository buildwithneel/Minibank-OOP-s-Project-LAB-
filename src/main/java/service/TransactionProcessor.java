package service;
import model.Account;
import exception.BankException;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;
public class TransactionProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();
    public void submit(Runnable task) {
        executor.execute(task);
    }
    public void stop() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }
    public void addAccount(Account account) {
        sharedAccounts.put(account.getAccountNumber(), account);
    }
    public void submitDeposit(Account account, long amount) {
        submit(() -> {
            try {
                account.deposit(amount);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        });
    }
    public void submitWithdrawal(Account account, long amount) {
        submit(() -> {
            try {
                account.withdraw(amount);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        });
    }
    public static void producerConsumerDemo() throws InterruptedException {
        TransactionBuffer buffer = new TransactionBuffer(3);
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) buffer.put("TRANSACTION-" + i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) System.out.println("Processed " + buffer.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
    public static void oppositeTransferDemo() throws InterruptedException {
        Account first = new model.CurrentAccount("Transfer A", 10000, 10000);
        Account second = new model.CurrentAccount("Transfer B", 10000, 10000);
        Runnable forward = () -> {
            try {
                for (int i = 0; i < 100; i++) first.transfer(second, 1);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        };
        Runnable reverse = () -> {
            try {
                for (int i = 0; i < 100; i++) second.transfer(first, 1);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        };
        Thread one = new Thread(forward, "Transfer-A-to-B");
        Thread two = new Thread(reverse, "Transfer-B-to-A");
        one.start();
        two.start();
        one.join();
        two.join();
        System.out.println("Opposite transfers completed without deadlock");
    }
    public static class TransactionBuffer {
        private final String[] buffer;
        private int count;
        public TransactionBuffer(int size) {
            buffer = new String[size];
        }
        public synchronized void put(String value) throws InterruptedException {
            while (count == buffer.length) wait();
            buffer[count++] = value;
            notifyAll();
        }
        public synchronized String take() throws InterruptedException {
            while (count == 0) wait();
            String value = buffer[0];
            System.arraycopy(buffer, 1, buffer, 0, --count);
            notifyAll();
            return value;
        }
    }
}

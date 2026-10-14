package model;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;
import java.io.Serializable;
import java.util.Objects;
public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;
    private static long accountCounter = 1;
    @Id
    @MaxLength(10)
    private final String accountNumber;
    private final String ownerName;
    @Positive
    private long balance;
    private boolean active = true;
    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", accountCounter++);
        this.ownerName = Objects.requireNonNull(ownerName);
        this.balance = openingBalance;
    }
    public abstract double interestRate();
    public abstract boolean canWithdraw(long amount);
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be greater than zero");
        if (!active) throw new InvalidAmountException("Account is inactive");
        balance += amount;
    }
    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        if (!active) throw new InvalidAmountException("Account is inactive");
        if (!canWithdraw(amount)) {
            long shortfall = Math.max(0, amount - balance);
            throw new InsufficientFundsException("Withdrawal is not allowed or funds are insufficient", shortfall);
        }
        balance -= amount;
        return true;
    }
    public void transfer(Account to, long amount) throws BankException {
        if (to == null) throw new BankException("Destination account is required");
        if (to == this) throw new BankException("Cannot transfer to the same account");
        Account first = compareTo(to) <= 0 ? this : to;
        Account second = first == this ? to : this;
        synchronized (first) {
            synchronized (second) {
                try {
                    withdraw(amount);
                    to.deposit(amount);
                } catch (BankException e) {
                    throw e;
                } finally {
                    Thread.yield();
                }
            }
        }
    }
    public static synchronized void syncAccountCounter(Account[] accounts) {
        long next = 1;
        for (Account account : accounts) {
            if (account != null && account.accountNumber.matches("AC[0-9]+")) {
                next = Math.max(next, Long.parseLong(account.accountNumber.substring(2)) + 1);
            }
        }
        accountCounter = Math.max(accountCounter, next);
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public String getOwnerName() {
        return ownerName;
    }
    @Override
    public synchronized long getBalance() {
        return balance;
    }
    public synchronized boolean isActive() {
        return active;
    }
    public synchronized void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }
    @Override
    public String toString() {
        return "Account{accountNumber='" + accountNumber + "', ownerName='" + ownerName + "', balance=" + getBalance() + "}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return accountNumber.equals(account.accountNumber);
    }
    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}

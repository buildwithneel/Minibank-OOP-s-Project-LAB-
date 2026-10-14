package model;
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long minBalance;
    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }
    public SavingsAccount(String ownerName, long minBalance) {
        this(ownerName, 0, minBalance);
    }
    @Override
    public double interestRate() {
        return 4.0;
    }
    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && getBalance() - amount >= minBalance;
    }
    public long getMinBalance() {
        return minBalance;
    }
}

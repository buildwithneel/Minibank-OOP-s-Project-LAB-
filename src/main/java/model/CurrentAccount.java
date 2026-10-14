package model;
public class CurrentAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;
    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }
    public CurrentAccount(String ownerName, long overdraftLimit) {
        this(ownerName, 0, overdraftLimit);
    }
    @Override
    public double interestRate() {
        return 0;
    }
    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && getBalance() - amount >= -overdraftLimit;
    }
    public long getOverdraftLimit() {
        return overdraftLimit;
    }
}

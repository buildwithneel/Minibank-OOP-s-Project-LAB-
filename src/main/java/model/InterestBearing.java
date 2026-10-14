package model;
public interface InterestBearing {
    default double yearlyInterest() {
        return getBalance() * interestRate() / 100.0;
    }
    double interestRate();
    long getBalance();
}

package model;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;
public interface Transactable {
    void deposit(long amount) throws InvalidAmountException;
    boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException;
}

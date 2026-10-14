package exception;
public class InvalidAmountException extends BankException {
    private static final long serialVersionUID = 1L;
    public InvalidAmountException(String message) {
        super(message);
    }
}

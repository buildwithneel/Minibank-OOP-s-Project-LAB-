package exception;
public class AccountNotFoundException extends BankException {
    private static final long serialVersionUID = 1L;
    public AccountNotFoundException(String message) {
        super(message);
    }
}

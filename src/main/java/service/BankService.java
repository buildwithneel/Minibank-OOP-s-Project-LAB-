package service;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.AnnotationValidator;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
public class BankService {
    private final Repository<Account> repository = new Repository<>();
    private final AccountStore store = new AccountStore();
    private final Path logFile = Path.of("logs", "transactions.log");
    public Account openAccount(String name, String type, long openingBalance) throws BankException {
        if (name == null || name.isBlank()) throw new BankException("Owner name is required");
        if (openingBalance < 0) throw new InvalidAmountException("Opening balance cannot be negative");
        Account account;
        switch (type.toUpperCase()) {
            case "SAVINGS" -> account = new SavingsAccount(name, openingBalance, 500);
            case "CURRENT" -> account = new CurrentAccount(name, openingBalance, 10000);
            case "FIXED" -> account = new FixedDepositAccount(name, openingBalance);
            default -> throw new BankException("Unknown account type");
        }
        String[] errors = AnnotationValidator.validate(account);
        if (errors.length > 0) throw new BankException(String.join(", ", errors));
        repository.save(account);
        store.add(account);
        return account;
    }
    public Account findAccount(String id) throws AccountNotFoundException {
        Account account = repository.findById(id);
        if (account == null) account = store.get(id);
        if (account == null) throw new AccountNotFoundException("Account not found: " + id);
        return account;
    }
    public void deposit(String id, long amount) throws BankException {
        Account account = findAccount(id);
        account.deposit(amount);
        record("DEPOSIT", amount, id);
    }
    public void withdraw(String id, long amount) throws BankException {
        Account account = findAccount(id);
        account.withdraw(amount);
        record("WITHDRAW", amount, id);
    }
    public void transfer(String from, String to, long amount) throws BankException {
        Account source = findAccount(from);
        Account destination = findAccount(to);
        source.transfer(destination, amount);
        record("TRANSFER", amount, from + " to " + to);
    }
    public String statement(String id) throws AccountNotFoundException {
        return StatementFormatter.buildStatement(findAccount(id));
    }
    public ArrayList<Account> listAccounts() {
        return store.sortedByBalance();
    }
    public ArrayList<Account> allAccounts() {
        return repository.findAll();
    }
    public void addLoaded(Account account) {
        repository.save(account);
        store.add(account);
    }
    public void save(Path path) throws IOException {
        StatePersister.save(store.getAll().toArray(new Account[0]), path);
    }
    public void load(Path path) throws IOException, ClassNotFoundException {
        for (Account account : StatePersister.load(path)) addLoaded(account);
    }
    public AccountStore getStore() {
        return store;
    }
    private void record(String type, long amount, String detail) throws BankException {
        String line = type + " " + amount + " " + detail;
        store.addHistory(line);
        try {
            TransactionLog.append(logFile, line);
        } catch (IOException e) {
            throw new BankException("Transaction completed but log could not be written: " + e.getMessage());
        }
    }
    public boolean validContact(String email, String mobile) {
        return Validator.isValidEmail(email) && Validator.isValidMobile(mobile);
    }
}

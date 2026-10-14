package service;
import model.Account;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
public class AccountStore {
    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();
    public synchronized void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }
    public synchronized Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }
    public synchronized ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }
    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = getAll();
        list.sort(Comparator.comparingLong(Account::getBalance));
        return list;
    }
    public synchronized void addHistory(String line) {
        transactionHistory.add(line);
    }
    public synchronized ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
}

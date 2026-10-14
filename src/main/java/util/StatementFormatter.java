package util;
import model.Account;
public final class StatementFormatter {
    private StatementFormatter() {}
    public static String buildStatement(Account account) {
        StringBuilder s = new StringBuilder();
        s.append("MINIBANK ACCOUNT STATEMENT\n");
        s.append("Account: ").append(account.getAccountNumber()).append('\n');
        s.append("Owner: ").append(account.getOwnerName()).append('\n');
        s.append("Balance: ").append(account.getBalance()).append(" rupees\n");
        s.append("Interest rate: ").append(account.interestRate()).append("%\n");
        s.append("Yearly interest estimate: ").append(account.yearlyInterest()).append(" rupees\n");
        s.append("Active: ").append(account.isActive());
        return s.toString();
    }
}

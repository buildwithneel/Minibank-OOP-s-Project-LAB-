package app;
import exception.BankException;
import model.Account;
import model.BankInfo;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.MenuOption;
import model.SavingsAccount;
import model.WithdrawRule;
import service.AccountWorker;
import service.BankService;
import service.Repository;
import service.TransactionProcessor;
import util.AnnotationValidator;
import util.ClosableResource;
import util.CommandParser;
import util.ReportGenerator;
import util.Validator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import static java.lang.System.out;
public class MiniBank {
    private static final Path STATE_FILE = Path.of("data", "accounts.dat");
    private static final Path LOG_FOLDER = Path.of("logs");
    private static final Path REPORT_FILE = Path.of("reports", "end-of-day.txt");
    public static void main(String[] args) {
        BankService bank = new BankService();
        try {
            bank.load(STATE_FILE);
        } catch (IOException | ClassNotFoundException e) {
            out.println("Could not load saved accounts: " + e.getMessage());
        }
        out.println(new BankInfo("MiniBank", "Main Branch"));
        demonstrateConcepts();
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMenu();
                out.print("Choose option: ");
                String input = scanner.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    out.println("Enter a valid menu number");
                    continue;
                }
                MenuOption option = switch (choice) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.STATEMENT;
                    case 6 -> MenuOption.LIST_ACCOUNTS;
                    case 7 -> MenuOption.BATCH_PROCESS;
                    case 8 -> MenuOption.REPORT;
                    case 9 -> MenuOption.EXIT;
                    default -> null;
                };
                if (option == null) {
                    out.println("Invalid option");
                    continue;
                }
                try {
                    switch (option) {
                        case OPEN_ACCOUNT -> openAccount(scanner, bank);
                        case DEPOSIT -> {
                            out.print("Account number: ");
                            String id = scanner.nextLine().trim();
                            out.print("Amount: ");
                            long amount = Long.parseLong(scanner.nextLine().trim());
                            bank.deposit(id, amount);
                            out.println("Deposit successful");
                        }
                        case WITHDRAW -> {
                            out.print("Account number: ");
                            String id = scanner.nextLine().trim();
                            out.print("Amount: ");
                            long amount = Long.parseLong(scanner.nextLine().trim());
                            bank.withdraw(id, amount);
                            out.println("Withdrawal successful");
                        }
                        case TRANSFER -> {
                            out.print("Source account: ");
                            String from = scanner.nextLine().trim();
                            out.print("Destination account: ");
                            String to = scanner.nextLine().trim();
                            out.print("Amount: ");
                            long amount = Long.parseLong(scanner.nextLine().trim());
                            bank.transfer(from, to, amount);
                            out.println("Transfer successful");
                        }
                        case STATEMENT -> {
                            out.print("Account number: ");
                            out.println(bank.statement(scanner.nextLine().trim()));
                        }
                        case LIST_ACCOUNTS -> bank.listAccounts().forEach(out::println);
                        case BATCH_PROCESS -> batchProcess(scanner, bank);
                        case REPORT -> out.println(ReportGenerator.generate(LOG_FOLDER, REPORT_FILE));
                        case EXIT -> {
                            bank.save(STATE_FILE);
                            out.println("Goodbye from MiniBank");
                            running = false;
                        }
                    }
                } catch (BankException | IOException e) {
                    out.println("Operation failed: " + e.getMessage());
                } catch (NumberFormatException e) {
                    out.println("Enter a valid number");
                } finally {
                    out.println("Operation finished");
                }
            }
        } catch (Exception e) {
            out.println("MiniBank stopped: " + e.getMessage());
        }
    }
    private static void showMenu() {
        out.println("\n1. Open account");
        out.println("2. Deposit");
        out.println("3. Withdraw");
        out.println("4. Transfer");
        out.println("5. Statement");
        out.println("6. List accounts sorted by balance");
        out.println("7. Batch processing");
        out.println("8. Generate report");
        out.println("9. Exit");
    }
    private static void openAccount(Scanner scanner, BankService bank) throws BankException {
        out.print("Owner name: ");
        String name = scanner.nextLine().trim();
        out.print("Account type (SAVINGS/CURRENT/FIXED): ");
        String type = scanner.nextLine().trim();
        out.print("Opening balance: ");
        long balance = Long.parseLong(scanner.nextLine().trim());
        Account account = bank.openAccount(name, type, balance);
        out.println("Created: " + account);
    }
    private static void batchProcess(Scanner scanner, BankService bank) throws BankException {
        out.print("Account number for batch: ");
        Account account = bank.findAccount(scanner.nextLine().trim());
        out.print("Number of deposits: ");
        int count = Integer.parseInt(scanner.nextLine().trim());
        out.print("Deposit amount each time: ");
        long amount = Long.parseLong(scanner.nextLine().trim());
        TransactionProcessor processor = new TransactionProcessor();
        processor.addAccount(account);
        for (int i = 0; i < count; i++) processor.submitDeposit(account, amount);
        try {
            processor.stop();
            out.println("Batch complete. Balance: " + account.getBalance());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BankException("Batch processing interrupted");
        }
    }
    private static void demonstrateConcepts() {
        Account[] types = {
            new SavingsAccount("Demo Savings", 2000, 500),
            new CurrentAccount("Demo Current", 1000, 5000),
            new FixedDepositAccount("Demo Fixed", 3000)
        };
        for (Account account : types) out.println(account.getClass().getSimpleName() + " interest=" + account.interestRate());
        Account savings = types[0];
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };
        WithdrawRule lambdaRule = (account, amount) -> account.canWithdraw(amount);
        out.println("Anonymous rule: " + anonymousRule.allow(savings, 100));
        out.println("Lambda rule: " + lambdaRule.allow(savings, 100));
        out.println("Mobile valid: " + Validator.isValidMobile("9876543210"));
        out.println("Mobile invalid: " + Validator.isValidMobile("123"));
        out.println("Email valid: " + Validator.isValidEmail("user@example.com"));
        out.println("Email invalid: " + Validator.isValidEmail("bad-email"));
        out.println("PAN valid: " + Validator.isValidPan("ABCDE1234F"));
        out.println("PAN invalid: " + Validator.isValidPan("123"));
        out.println("IFSC valid: " + Validator.isValidIfsc("SBIN0001234"));
        out.println("IFSC invalid: " + Validator.isValidIfsc("BAD"));
        Command command = CommandParser.parse("DEPOSIT AC0001 500");
        out.println(command.type() + " " + command.accountNumber() + " " + command.amount());
        try (ClosableResource resource = new ClosableResource()) {
            out.println("Try-with-resources example");
        }
        Account invalid = new SavingsAccount("Invalid Demo", -1, 0);
        for (String error : AnnotationValidator.validate(invalid)) out.println(error);
        Repository<Account> repository = new Repository<>();
        repository.saveAll(types);
        out.println("Repository example: " + repository.findAll().size() + " accounts");
        try {
            TransactionProcessor.producerConsumerDemo();
            TransactionProcessor.oppositeTransferDemo();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Thread demo = new Thread(new AccountWorker(new SavingsAccount("Thread Demo", 0, 0), 1, 1), "Demo-Worker");
        demo.start();
        try {
            demo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

import java.util.*;

class Transaction {
    String type;
    double amount;
    String status;
    long time;

    Transaction(String type, double amount, String status) {
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return type + " | Amount: " + amount + " | Status: " + status;
    }
}

public class DigitalWallet {

    private String accountId;
    private String pin;
    private double balance;

    private final double DAILY_LIMIT = 50000;
    private double dailyTransactionAmount = 0;

    private int failedPinAttempts = 0;

    private List<Transaction> transactions = new ArrayList<>();

    public DigitalWallet(String accountId, String pin, double initialBalance) {
        this.accountId = accountId;
        this.pin = pin;
        this.balance = initialBalance;
    }

    // Account verification
    private boolean verifyPin(String enteredPin) {
        if (pin.equals(enteredPin)) {
            failedPinAttempts = 0;
            return true;
        }

        failedPinAttempts++;

        System.out.println("Invalid PIN attempt: " + failedPinAttempts);

        return false;
    }

    // Fraud detection
    private boolean fraudDetection(double amount) {

        // More than 5 transactions
        if (transactions.size() >= 5) {
            System.out.println("FRAUD ALERT: More than 5 transactions detected.");
            return true;
        }

        // Large transaction
        if (amount > 25000) {
            System.out.println("FRAUD ALERT: Large transaction detected.");
            return true;
        }

        // Multiple failed PIN attempts
        if (failedPinAttempts >= 3) {
            System.out.println("FRAUD ALERT: Multiple failed PIN attempts.");
            return true;
        }

        // Unusual transaction amount
        if (amount > 20000) {
            System.out.println("FRAUD ALERT: Unusual transaction amount.");
            return true;
        }

        return false;
    }

    // Deposit
    public void deposit(double amount) {

        if (amount <= 0) {
            System.out.println("Deposit failed: Negative or zero amount.");
            transactions.add(new Transaction("DEPOSIT", amount, "FAILED"));
            return;
        }

        balance += amount;

        transactions.add(new Transaction("DEPOSIT", amount, "SUCCESS"));

        System.out.println("Deposit successful: " + amount);
    }

    // Withdrawal
    public void withdraw(double amount, String enteredPin) {

        if (!verifyPin(enteredPin)) {
            transactions.add(new Transaction("WITHDRAW", amount, "FAILED - INVALID PIN"));
            return;
        }

        if (amount <= 0) {
            System.out.println("Withdrawal failed: Invalid amount.");
            transactions.add(new Transaction("WITHDRAW", amount, "FAILED"));
            return;
        }

        if (amount > balance) {
            System.out.println("Withdrawal failed: Insufficient balance.");
            transactions.add(new Transaction("WITHDRAW", amount, "FAILED"));
            return;
        }

        if (dailyTransactionAmount + amount > DAILY_LIMIT) {
            System.out.println("Withdrawal failed: Daily transaction limit exceeded.");
            transactions.add(new Transaction("WITHDRAW", amount, "FAILED"));
            return;
        }

        if (fraudDetection(amount)) {
            System.out.println("Withdrawal flagged as suspicious.");
            transactions.add(new Transaction("WITHDRAW", amount, "SUSPICIOUS"));
            return;
        }

        balance -= amount;
        dailyTransactionAmount += amount;

        transactions.add(new Transaction("WITHDRAW", amount, "SUCCESS"));

        System.out.println("Withdrawal successful: " + amount);
    }

    // Money transfer
    public void transfer(
            DigitalWallet receiver,
            double amount,
            String enteredPin) {

        if (!verifyPin(enteredPin)) {
            transactions.add(new Transaction("TRANSFER", amount, "FAILED - INVALID PIN"));
            return;
        }

        if (amount <= 0) {
            System.out.println("Transfer failed: Invalid amount.");
            transactions.add(new Transaction("TRANSFER", amount, "FAILED"));
            return;
        }

        if (amount > balance) {
            System.out.println("Transfer failed: Insufficient balance.");
            transactions.add(new Transaction("TRANSFER", amount, "FAILED"));
            return;
        }

        if (dailyTransactionAmount + amount > DAILY_LIMIT) {
            System.out.println("Transfer failed: Daily transaction limit exceeded.");
            transactions.add(new Transaction("TRANSFER", amount, "FAILED"));
            return;
        }

        if (fraudDetection(amount)) {
            System.out.println("Transfer flagged as suspicious.");
            transactions.add(new Transaction("TRANSFER", amount, "SUSPICIOUS"));
            return;
        }

        balance -= amount;
        receiver.balance += amount;

        dailyTransactionAmount += amount;

        transactions.add(new Transaction("TRANSFER", amount, "SUCCESS"));

        System.out.println(
                "Transfer successful: " + amount +
                " to " + receiver.accountId);
    }

    // Balance verification
    public double getBalance() {
        return balance;
    }

    // Transaction history
    public void showTransactionHistory() {

        System.out.println("\nTransaction History");

        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // Used by QA
    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    public double getDailyTransactionAmount() {
        return dailyTransactionAmount;
    }

    public static void main(String[] args) {

        System.out.println("===== DIGITAL WALLET SYSTEM =====");

        DigitalWallet wallet1 =
                new DigitalWallet("W001", "1234", 50000);

        DigitalWallet wallet2 =
                new DigitalWallet("W002", "5678", 10000);

        System.out.println("\nInitial Balance: "
                + wallet1.getBalance());

        // Account / deposit
        wallet1.deposit(5000);

        // Normal withdrawal
        wallet1.withdraw(2000, "1234");

        // Money transfer
        wallet1.transfer(wallet2, 5000, "1234");

        // Invalid PIN
        wallet1.withdraw(1000, "1111");

        // Negative transaction
        wallet1.deposit(-500);

        // Large suspicious transaction
        wallet1.withdraw(30000, "1234");

        // Balance
        System.out.println(
                "\nFinal Wallet 1 Balance: "
                        + wallet1.getBalance());

        System.out.println(
                "Final Wallet 2 Balance: "
                        + wallet2.getBalance());

        // History
        wallet1.showTransactionHistory();

        System.out.println("\n===== PROGRAM COMPLETED =====");
    }
}

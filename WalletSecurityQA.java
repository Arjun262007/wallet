public class WalletSecurityQA {

    static int passed = 0;
    static int failed = 0;

    static void check(String testName, boolean condition) {

        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.out.println("[FAIL] " + testName);
            failed++;
        }
    }

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println(" DIGITAL WALLET QA TEST");
        System.out.println("================================\n");

        // 1. Normal transaction
        DigitalWallet wallet1 =
                new DigitalWallet("QA001", "1234", 10000);

        double beforeDeposit = wallet1.getBalance();

        wallet1.deposit(2000);

        check(
                "Normal transaction",
                wallet1.getBalance() == beforeDeposit + 2000
        );

        // 2. Insufficient balance
        DigitalWallet wallet2 =
                new DigitalWallet("QA002", "1234", 1000);

        double beforeWithdrawal = wallet2.getBalance();

        wallet2.withdraw(5000, "1234");

        check(
                "Insufficient balance",
                wallet2.getBalance() == beforeWithdrawal
        );

        // 3. Daily transaction limit
        DigitalWallet wallet3 =
                new DigitalWallet("QA003", "1234", 100000);

        wallet3.withdraw(30000, "1234");

        check(
                "Daily transaction limit",
                wallet3.getDailyTransactionAmount() <= 50000
        );

        // 4. Multiple failed PIN attempts
        DigitalWallet wallet4 =
                new DigitalWallet("QA004", "1234", 10000);

        wallet4.withdraw(100, "0000");
        wallet4.withdraw(100, "0000");
        wallet4.withdraw(100, "0000");

        check(
                "Multiple failed PINs",
                wallet4.getFailedPinAttempts() >= 3
        );

        // 5. Suspicious transaction
        DigitalWallet wallet5 =
                new DigitalWallet("QA005", "1234", 100000);

        double beforeSuspicious = wallet5.getBalance();

        wallet5.withdraw(30000, "1234");

        check(
                "Suspicious transaction",
                wallet5.getBalance() == beforeSuspicious
        );

        // 6. Duplicate transaction
        DigitalWallet wallet6 =
                new DigitalWallet("QA006", "1234", 10000);

        wallet6.withdraw(1000, "1234");
        wallet6.withdraw(1000, "1234");

        check(
                "Duplicate transaction handling",
                wallet6.getBalance() == 8000
        );

        // 7. Negative amount
        DigitalWallet wallet7 =
                new DigitalWallet("QA007", "1234", 10000);

        double beforeNegative = wallet7.getBalance();

        wallet7.deposit(-500);

        check(
                "Negative amount",
                wallet7.getBalance() == beforeNegative
        );

        // 8. Concurrent transactions
        DigitalWallet wallet8 =
                new DigitalWallet("QA008", "1234", 10000);

        Thread t1 = new Thread(() ->
                wallet8.withdraw(1000, "1234"));

        Thread t2 = new Thread(() ->
                wallet8.withdraw(1000, "1234"));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        check(
                "Concurrent transactions",
                wallet8.getBalance() >= 8000
        );

        System.out.println("\n================================");
        System.out.println(" TEST SUMMARY");
        System.out.println("================================");

        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            System.out.println("\nQA RESULT: FAILED");
            System.exit(1);
        } else {
            System.out.println("\nQA RESULT: PASSED");
        }
    }
}

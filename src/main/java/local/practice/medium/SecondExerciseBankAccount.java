package local.practice.medium;

import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a BankAccount class with a balance and methods deposit(int amount) and
 withdraw(int amount), both thread-safe using the synchronized keyword. Create three threads: two that each deposit
 100 amounts of $10 with a random delay (50-200ms) between deposits, and one that withdraws 100 amounts of $20 with a
 random delay (100-300ms) between withdrawals. Use java.util.Random for delays. The withdrawal method should block if the
 balance is insufficient, using wait() and notifyAll(). The main thread should wait for all threads to complete and
 print the final balance, which should be 0 if implemented correctly.
 */
public class SecondExerciseBankAccount {

    private static class BankAccount {

        private final Object dedicatedLock = new Object();
        private int balance = 0;


        void deposit(int amount) {
            synchronized (dedicatedLock) {
                balance += amount;
                dedicatedLock.notifyAll();
            }
        }

        void withdraw(int amount) {
            synchronized (dedicatedLock) {
                while (balance < amount) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Failed to wait for balance to be more than requested amount.");
                        throw new RuntimeException(e);
                    }
                }
                balance -= amount;
                dedicatedLock.notifyAll();
            }
        }

        synchronized int getBalance() {
            return balance;
        }
    }

    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount();

        Runnable depositTask = () -> {
            Random randomDelayGenerator = new Random();
            IntStream.range(0, 100).forEach(i -> {
                try {
                    bankAccount.deposit(10);
                    Thread.sleep(randomDelayGenerator.nextInt(151) + 50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Unexpected exception while depositing amount from thread " +
                            Thread.currentThread().getName() + ". Exception: " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            });
        };

        Runnable withdrawalTask = () -> {
            Random randomDelayGenerator = new Random();
            IntStream.range(0, 100).forEach(i -> {
                try {
                    Thread.sleep(randomDelayGenerator.nextInt(201) + 100);
                    bankAccount.withdraw(20);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Unexpected exception while withdrawing amount from thread " +
                            Thread.currentThread().getName() + ". Exception: " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            });
        };

        Thread firstDepositThread = new Thread(depositTask);
        firstDepositThread.setName("First Thread to Deposit");
        Thread secondDepositThread = new Thread(depositTask);
        secondDepositThread.setName("Second Thread to Deposit");

        Thread withdrawalThread = new Thread(withdrawalTask);
        withdrawalThread.setName("Thread to Withdraw");

        firstDepositThread.start();
        secondDepositThread.start();
        withdrawalThread.start();

        try {
            firstDepositThread.join();
            secondDepositThread.join();
            withdrawalThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Failed to join thread: " + Thread.currentThread().getName() + "\n Exception: " + ex.getMessage());
        }

        System.out.printf("Final balance is: %d%n ", bankAccount.getBalance());

    }
}

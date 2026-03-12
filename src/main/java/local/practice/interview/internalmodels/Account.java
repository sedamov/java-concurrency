package local.practice.interview.internalmodels;

import java.util.UUID;

public class Account {

    private final String accountId;
    private double balance;

    public Account(double balance) {
        this.accountId = UUID.randomUUID().toString();
        this.balance = balance;
    }

    public Account(String accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() { return this.accountId; }

    public double getBalance() {return this.balance; }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

package local.practice.interview.ledgersystem;

import local.practice.interview.internalmodels.Account;
import local.practice.interview.internalmodels.AccountTransaction;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LedgerSystem {

    private final ReentrantReadWriteLock explicitLock = new ReentrantReadWriteLock();

    private final LinkedList<AccountTransaction> transactionsHistory = new LinkedList<>();
    private final Map<String, Account> accountsCache = new HashMap<>();

    public void deposit(String accountId, double amount) {
        explicitLock.writeLock().lock();
        try {
            if (!LedgerSystemHelper.isValidAmount(amount))
                throw new IllegalArgumentException("Provided amount is invalid");
            Account existingAccount = accountsCache.get(accountId);
            if (existingAccount == null) {
                existingAccount = new Account(accountId, 0);
                transactionsHistory.add(
                        new AccountTransaction(
                                null,
                                existingAccount.getAccountId(),
                                0.0,
                                AccountTransaction.Operation.CREATE,
                                List.of("Creating new account " + accountId),
                                Collections.emptyList()
                        ));
            }
            existingAccount.setBalance(existingAccount.getBalance() + amount);
            accountsCache.put(existingAccount.getAccountId(), existingAccount);
            transactionsHistory.add(
                    new AccountTransaction(
                            null,
                            existingAccount.getAccountId(),
                            amount,
                            AccountTransaction.Operation.DEPOSIT,
                            List.of(String.format("Depositing amount %f to account %s", amount, existingAccount.getAccountId())),
                            Collections.emptyList()
                    )
            );
        } finally {
            explicitLock.writeLock().unlock();
        }
    }

    public void withdraw(String accountId, double amount) {
        explicitLock.writeLock().lock();
        try {
            if (!LedgerSystemHelper.isValidAmount(amount))
                throw new IllegalArgumentException("Provided amount is invalid");
            Account existingAccount = accountsCache.get(accountId);
            if (existingAccount == null) {
                throw new IllegalStateException(String.format("Requested account with ID %s does not exits", accountId));
            } else {
                if (!LedgerSystemHelper.isEnoughBalance(existingAccount.getBalance(), amount)) {
                    throw new IllegalStateException(String.format("Requested account with ID %s does not have enough balance", accountId));
                }
                existingAccount.setBalance(existingAccount.getBalance() - amount);
                accountsCache.put(accountId, existingAccount);
                transactionsHistory.add(
                        new AccountTransaction(
                                accountId,
                                null,
                                amount,
                                AccountTransaction.Operation.WITHDRAW,
                                List.of(String.format("Withdrawn amount %f from account %s", amount, accountId)),
                                Collections.emptyList()
                        )
                );
            }
        } finally {
            explicitLock.writeLock().unlock();
        }
    }

    public void transfer(String fromAccountId, String toAccountId, double amount) {
        explicitLock.writeLock().lock();
        try {
            if (!LedgerSystemHelper.isValidAmount(amount))
                throw new IllegalArgumentException("Provided amount is invalid");
            Account existingFromAccount = accountsCache.get(fromAccountId);
            if (existingFromAccount == null) {
                throw new IllegalStateException(String.format("Requested account with ID %s does not exits", fromAccountId));
            } else {
                if (!LedgerSystemHelper.isEnoughBalance(existingFromAccount.getBalance(), amount)) {
                    throw new IllegalStateException(String.format("Requested account with ID %s does not have enough balance", fromAccountId));
                }
                Account existingToAccount = accountsCache.get(toAccountId);
                if (existingToAccount == null) {
                    existingToAccount = new Account(toAccountId, 0);
                    transactionsHistory.add(
                            new AccountTransaction(
                                    null,
                                    existingToAccount.getAccountId(),
                                    0.0,
                                    AccountTransaction.Operation.CREATE,
                                    List.of("Creating new account " + toAccountId),
                                    Collections.emptyList()
                            ));
                }
                existingFromAccount.setBalance(existingFromAccount.getBalance() - amount);
                accountsCache.put(fromAccountId, existingFromAccount);
                transactionsHistory.add(
                        new AccountTransaction(
                                fromAccountId,
                                null,
                                amount,
                                AccountTransaction.Operation.WITHDRAW,
                                List.of(String.format("Withdrawn amount %f from account %s", amount, fromAccountId)),
                                Collections.emptyList()
                        )
                );
                existingToAccount.setBalance(existingToAccount.getBalance() + amount);
                accountsCache.put(toAccountId, existingToAccount);
                transactionsHistory.add(
                        new AccountTransaction(
                                null,
                                toAccountId,
                                amount,
                                AccountTransaction.Operation.DEPOSIT,
                                List.of(String.format("Depositing amount %f to account %s", amount, toAccountId)),
                                Collections.emptyList()
                        )
                );
                transactionsHistory.add(
                        new AccountTransaction(
                                fromAccountId,
                                toAccountId,
                                amount,
                                AccountTransaction.Operation.TRANSFER,
                                List.of(String.format("Transferring amount %f from account %s to account %s", amount, fromAccountId, toAccountId)),
                                Collections.emptyList()
                        )
                );
            }
        } finally {
            explicitLock.writeLock().unlock();
        }
    }

    public double getBalance(String accountId) {
        explicitLock.readLock().lock();
        try {
            Account requestedAccount = accountsCache.get(accountId);
            if (requestedAccount == null) throw new IllegalStateException("Requested account does not exist");
            return requestedAccount.getBalance();
        } finally {
            explicitLock.readLock().unlock();
        }
    }
}

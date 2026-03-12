package local.practice.interview.internalmodels;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountTransaction {

    private final String fromAccountId;
    private final String toAccountId;
    private final double amount;
    private final Operation operation;
    private final List<String> details = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final LocalDateTime executionTime;

    public AccountTransaction(String fromAccountId,
                              String toAccountId,
                              double amount,
                              Operation operation,
                              List<String> details,
                              List<String> errors) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.operation = operation;
        this.details.addAll(details);
        this.errors.addAll(errors);
        this.executionTime = LocalDateTime.now();
    }

    public static enum Operation {
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        CREATE,
    }
}



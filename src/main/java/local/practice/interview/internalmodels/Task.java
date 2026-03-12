package local.practice.interview.internalmodels;

import java.util.UUID;
import java.util.concurrent.Callable;

public class Task {

    private final UUID id;
    private final Callable<String> task;

    public Task(Callable<String> task) {
        this.id = UUID.randomUUID();
        this.task = task;
    }

    public UUID getId() {
        return id;
    }

    public Callable<String> getTask() {
        return task;
    }
}

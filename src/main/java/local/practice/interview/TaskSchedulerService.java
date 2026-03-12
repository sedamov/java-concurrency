package local.practice.interview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: Implement a TaskSchedulerService that schedules and executes tasks concurrently with a fixed thread pool.
 * Each task is represented by a unique ID and a Runnable that simulates some work (e.g., sleeping for a random duration).
 * The service should support submitting tasks, tracking their status (pending, running, completed), and retrieving
 * results. Ensure thread-safety for task state management and handle task cancellation gracefully.
 * Requirements:

 * 1. Use a fixed-size thread pool (ExecutorService) to execute tasks.
 * 2. Maintain a thread-safe map to track task statuses and results.
 * 3. Provide methods: submitTask(String taskId, Runnable task), getTaskStatus(String taskId), cancelTask(String taskId).
 * 4. Write unit tests to verify task execution, status updates, cancellation, and thread-safety under concurrent submissions.
 * 5. Handle edge cases: duplicate task IDs, cancellation of non-existent tasks, and thread pool shutdown.

 * Constraints:

 * 1. No external libraries beyond core Java and JUnit/TestNG.
 * 2. Task execution time should be simulated (e.g., Thread.sleep).
 * 3. Tests must cover concurrent task submissions and edge cases.
 */
public class TaskSchedulerService {

    private final ExecutorService internalExecutor;

    public TaskSchedulerService(int threadCount) {
        this.internalExecutor = Executors.newFixedThreadPool(threadCount);
    }

}

package local.practice.medium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a TaskQueue class with a List<String> to store tasks and methods addTask(String task)
 and getTask() to add and remove tasks, both thread-safe using the synchronized keyword. The getTask method should block
 if the queue is empty, using wait() and notifyAll(). Create two threads that each add 50 tasks
 (e.g., "Task1-0", "Task1-1", ..., "Task2-49") with a random delay (100-300ms) between additions. Create two threads
 that each remove and print 50 tasks with a random delay (150-350ms) between removals. Use java.util.Random for delays.
 The main thread should wait for all threads to complete and print the final queue size, which should be 0
 */
public class ThirdExerciseTaskQueue {

    private static class TaskQueue {
        private final Object dedicatedLock = new Object();
        private final List<String> tasks = new ArrayList<>();

        void addTask(String task) {
            synchronized (dedicatedLock) {
                tasks.add(task);
                dedicatedLock.notifyAll();
            }
        }

        String getTask() {
            synchronized (dedicatedLock) {
                while (tasks.isEmpty()) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.println("Failed to wait for tasks to fill before getting. Exception in thread: "
                                + Thread.currentThread().getName());
                        throw new RuntimeException(ex);
                    }
                }
                String task = tasks.removeLast();
                dedicatedLock.notifyAll();
                return task;
            }
        }

        synchronized int getSize() {
            return tasks.size();
        }
    }

    public static void main(String[] args) {
        TaskQueue taskQueue = new TaskQueue();

        Thread firstThreadToAddTasks = new Thread(createRunnableToAddTasks(taskQueue));
        firstThreadToAddTasks.setName("1");
        Thread secondThreadToAddTasks = new Thread(createRunnableToAddTasks(taskQueue));
        secondThreadToAddTasks.setName("2");
        Thread firstThreadToGetTasks = new Thread(createRunnableToGetTasks(taskQueue));
        firstThreadToGetTasks.setName("First getting thread");
        Thread secondThreadToGetTasks = new Thread(createRunnableToGetTasks(taskQueue));
        secondThreadToGetTasks.setName("Second getting thread");

        firstThreadToAddTasks.start();
        secondThreadToAddTasks.start();
        firstThreadToGetTasks.start();
        secondThreadToGetTasks.start();

        joinThread(firstThreadToAddTasks);
        joinThread(secondThreadToAddTasks);
        joinThread(firstThreadToGetTasks);
        joinThread(secondThreadToGetTasks);

        System.out.println("Tasks left: " + taskQueue.getSize());

    }

    private static Runnable createRunnableToAddTasks(TaskQueue taskQueue) {
        return  () -> {
            Random randomDelayGenerator = new Random();
            IntStream.range(0, 50).forEach(i -> {
                try {
                    taskQueue.addTask(String.format("Task%d-%d", Integer.parseInt(Thread.currentThread().getName()), i));
                    Thread.sleep(randomDelayGenerator.nextInt(201) + 100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Failed to delay between adding tasks");
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static Runnable createRunnableToGetTasks(TaskQueue taskQueue) {
        return  () -> {
            Random randomDelayGenerator = new Random();
            IntStream.range(0, 50).forEach(i -> {
                try {
                    System.out.printf("Got task %s\n", taskQueue.getTask());
                    Thread.sleep(randomDelayGenerator.nextInt(201) + 150);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Failed to delay between getting tasks");
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static void joinThread(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Failed to join thread [%s]", Thread.currentThread().getName());
            throw new RuntimeException(ex);
        }
    }
}

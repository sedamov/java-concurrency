package local.practice.hard;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a PriorityTaskQueue class with a PriorityQueue<Task> to store tasks with priorities
 (integers 1 to 10, lower numbers = higher priority). The class should have thread-safe methods addTask(Task task) and
 getTask() to add and remove tasks, using the synchronized keyword. The getTask method should block if the queue is
 empty, using wait() and notifyAll(), and return the highest-priority task (lowest number). Create two threads that
 each add 50 tasks with random priorities (1-10) and task names (e.g., "Task1-0", "Task2-49") with a random delay
 (100-300ms). Create two threads that each remove and print 50 tasks (name and priority) with a random delay (150-350ms).
 Use java.util.Random for priorities and delays. The main thread should wait for all threads to complete and print the
 final queue size, which should be 0. Define a Task class with name (String) and priority (int).
 Requirements:
  1. Define a Task class with name (String) and priority (int), comparable by priority (lower number = higher priority).
  2. PriorityTaskQueue uses a PriorityQueue<Task> with thread-safe addTask(Task) and getTask().
  3. getTask() blocks if the queue is empty using wait()/notifyAll() and returns the highest-priority task.
  4. Two threads add 50 tasks each (100 total) with random priorities (1-10) and delays (100-300ms).
  5. Two threads remove and print 50 tasks each (name and priority) with delays (150-350ms).
  6. Main thread waits for all threads and prints the final queue size (0).
 */
public class FirstExercisePriorityTaskQueue {

    private static final class Task implements Comparable<Task> {
        private final String name;
        private final int priority;

        Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public int compareTo(Task o) {
            if (o == null) {
                throw new IllegalArgumentException("Can not compare Task to null");
            }
            return this.priority - o.priority;
        }

        @Override
        public String toString() {
            return String.format("Task[name = %s, priority = %d]", this.name, this.priority);
        }
    }

    private static class PriorityTaskQueue {
        private final Object dedicatedLock = new Object();
        private final PriorityQueue<Task> tasksQueue = new PriorityQueue<>();

        void addTask(Task task) {
            synchronized (dedicatedLock) {
                tasksQueue.add(task);
                dedicatedLock.notifyAll();
            }
        }

        Task getTask() {
            synchronized (dedicatedLock) {
                while (tasksQueue.isEmpty()) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.printf("Failed to wait for priority task queue to fill with tasks. Failure in thread %s",
                                Thread.currentThread().getName());
                        throw new RuntimeException(ex);
                    }
                }
                Task taskToReturn = tasksQueue.peek();
                tasksQueue.remove(taskToReturn);
                dedicatedLock.notifyAll();
                return taskToReturn;
            }
        }

        int getNumberOfTasksLeft() {
            return this.tasksQueue.size();
        }
    }

    private static void joinThread(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException ex) {
            thread.interrupt();
            System.out.printf("Failed to join thread [%s]", thread.getName());
            throw new RuntimeException(ex);
        }
    }

    private static Runnable getRunnableToAddTasks(int threadNumber, PriorityTaskQueue priorityTaskQueue) {
        return () -> {
            Random randomGenerator = new Random();
            IntStream.range(0, 50).forEach(i -> {
                Task taskToAdd = new Task(String.format("Task%d-%d", threadNumber, i), randomGenerator.nextInt(10) + 1);
                System.out.printf("Adding task: %s\n", taskToAdd);
                priorityTaskQueue.addTask(taskToAdd);
                try {
                    Thread.sleep(randomGenerator.nextInt(201) + 100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to wait for priority task queue to fill with tasks. Failure in thread %s",
                            Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static Runnable getRunnableForRetrievingTask(PriorityTaskQueue priorityTaskQueue) {
        return () -> {
            Random randomGenerator = new Random();
            IntStream.range(0, 50).forEach(i -> {
                System.out.printf("Removing task: %s\n", priorityTaskQueue.getTask().toString());
                try {
                    Thread.sleep(randomGenerator.nextInt(251) + 100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to wait before retrieving another tasks. Failure in thread %s",
                            Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    public static void main(String[] args) {
        PriorityTaskQueue priorityTaskQueue = new PriorityTaskQueue();
        Thread firstThreadToAdd = new Thread(getRunnableToAddTasks(1, priorityTaskQueue));
        firstThreadToAdd.setName("First Thread That Adds");
        Thread secondThreadToAdd = new Thread(getRunnableToAddTasks(2, priorityTaskQueue));
        secondThreadToAdd.setName("Second Thread That Adds");

        Thread firstThreadToRead = new Thread(getRunnableForRetrievingTask(priorityTaskQueue));
        firstThreadToRead.setName("First Thread That Reads");
        Thread secondThreadToRead = new Thread(getRunnableForRetrievingTask(priorityTaskQueue));
        secondThreadToRead.setName("Second Thread That Reads");

        firstThreadToAdd.start();
        secondThreadToAdd.start();
        firstThreadToRead.start();
        secondThreadToRead.start();

        joinThread(firstThreadToAdd);
        joinThread(secondThreadToAdd);
        joinThread(firstThreadToRead);
        joinThread(secondThreadToRead);

        System.out.printf("After completing threads there are [%d] tasks left", priorityTaskQueue.getNumberOfTasksLeft());
    }
}

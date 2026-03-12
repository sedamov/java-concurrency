package local.practice.medium;

import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a SharedCounter class with an integer counter and thread-safe methods increment(),
 decrement(), and reset() using the synchronized keyword. The increment and decrement methods should block if the
 counter reaches 100 or 0, respectively, using wait() and notifyAll(). Create two threads that each call increment()
 100 times with a random delay (50-150ms) between calls, and one thread that calls decrement() 150 times with a random
 delay (100-200ms). Create one thread that calls reset() (sets counter to 0) twice, with a random delay (200-500ms)
 between calls. Use java.util.Random for delays. The main thread should wait for all threads to complete and print the
 final counter value, which should be 50 (2 * 100 increments - 150 decrements).
 */
public class FourthExerciseCounterWithReset {

    private static class SharedCounter {
        private final Object dedicatedLock = new Object();
        private int counter = 0;

        void increment() {
            synchronized (dedicatedLock) {
                while (counter == 100) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.printf("Failed to wait for counter to decrease from 100 in thread [%s]\n",
                                Thread.currentThread().getName());
                        throw new RuntimeException(ex);
                    }
                }
                counter++;
                System.out.printf("Incremented counter from thread %s, counter is %d\n", Thread.currentThread().getName(), counter);
                dedicatedLock.notifyAll();
            }
        }

        void decrement() {
            synchronized (dedicatedLock) {
                while (counter == 0) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.printf("Failed to wait for counter to increase from 0 in thread [%s]\n",
                                Thread.currentThread().getName());
                        throw new RuntimeException(ex);
                    }
                }
                counter--;
                System.out.printf("Decremented counter from thread %s, counter is %d\n", Thread.currentThread().getName(), counter);
                dedicatedLock.notifyAll();
            }
        }

        void reset() {
            synchronized (dedicatedLock) {
                System.out.printf("Reset counter from thread %s\n, counter is %d\n", Thread.currentThread().getName(), counter);
                counter = 0;
                dedicatedLock.notifyAll();
            }
        }

        synchronized int getCounter() {
            return counter;
        }
    }

    public static void main(String[] args) {
        SharedCounter sharedCounter = new SharedCounter();
        Thread firstIncrementThread = new Thread(createIncrementTask(sharedCounter));
        firstIncrementThread.setName("First Increment Thread");

        Thread secondIncrementThread = new Thread(createIncrementTask(sharedCounter));
        secondIncrementThread.setName("Second Increment Thread");

        Thread decrementThread = new Thread(createDecrementTask(sharedCounter));
        decrementThread.setName("Decrement Thread");

        Thread resetThread = new Thread(createResetTask(sharedCounter));
        resetThread.setName("Reset Thread");

        firstIncrementThread.start();
        secondIncrementThread.start();
        decrementThread.start();
        resetThread.start();

        joinThread(firstIncrementThread);
        joinThread(secondIncrementThread);
        joinThread(decrementThread);
        joinThread(resetThread);

        System.out.printf("After all threads completed, the counter is: %d\n", sharedCounter.getCounter());
    }

    private static void joinThread(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Failed to join thread [%s]. Exception: %s\n", Thread.currentThread().getName(), ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static Runnable createIncrementTask(SharedCounter sharedCounter) {
        return () -> {
            Random delayGenerator = new Random();
            IntStream.range(0, 100).forEach(i -> {
                try {
                    Thread.sleep(delayGenerator.nextInt(101) + 50);
                    sharedCounter.increment();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to delay increment from thread [%s]\n", Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static Runnable createDecrementTask(SharedCounter sharedCounter) {
        return () -> {
            Random delayGenerator = new Random();
            IntStream.range(0, 150).forEach(i -> {
                try {
                    Thread.sleep(delayGenerator.nextInt(101) + 100);
                    sharedCounter.decrement();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to delay decrement from thread [%s]\n", Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static Runnable createResetTask(SharedCounter sharedCounter) {
        return () -> {
            Random delayGenerator = new Random();
            IntStream.range(0, 2).forEach(i -> {
                try {
                    sharedCounter.reset();
                    Thread.sleep(delayGenerator.nextInt(51) + 50);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to delay reset from thread [%s]\n", Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

}

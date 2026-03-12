package local.practice.easy;

import java.util.stream.IntStream;

/*
 Write a Java program that creates a shared counter class with an increment() method and a getCount() method.
 Create two threads that each call increment() 1000 times. Ensure the counter is thread-safe using the synchronized
 keyword. The main thread should wait for both threads to complete and then print the final counter value,
 which should be 2000 if implemented correctly.
 */
public class SecondExerciseWithCounter {

    private static class Counter {
        private int counter = 0;

        synchronized void increment() {
            counter++;
        }

        int getCount() {
            return counter;
        }
    }

    public static void main(String[] args) {
        Counter counter = new Counter();

        Runnable callCounterTask = () -> {
            IntStream.range(0, 1000).forEach(i -> counter.increment());
        };

        Thread firstThread = new Thread(callCounterTask);
        firstThread.setName("First Thread");

        Thread secondThread = new Thread(callCounterTask);
        secondThread.setName("Second Thread");

        firstThread.start();
        secondThread.start();

        try {
            firstThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Unexpected interrupted exception in thread: " + Thread.currentThread().getName());
        }

        try {
            secondThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Unexpected interrupted exception in thread: " + Thread.currentThread().getName());
        }

        System.out.println("Counter result after running 2 threads: " + counter.getCount());
    }
}

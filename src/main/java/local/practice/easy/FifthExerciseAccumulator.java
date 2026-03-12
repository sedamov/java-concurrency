package local.practice.easy;

import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates an Accumulator class with an add(int value) method that adds a given value to
 an internal sum and a getSum() method that returns the current sum. The add method should be thread-safe using
 the synchronized keyword. Create three threads that each add a random integer between 1 and 10 (inclusive)
 to the sum 100 times. Use java.util.Random for generating random numbers. The main thread should wait for all threads
 to complete and then print the final sum, which should be the total of all 300 additions.
 */
public class FifthExerciseAccumulator {

    private static class Accumulator {
        private int sum = 0;

        synchronized void add(int value) {
            System.out.printf("Adding %d to sum %d\n", value, sum);
            sum += value;
        }

        synchronized int getSum() {
            return sum;
        }
    }

    public static void main(String[] args) {
        Accumulator accumulator = new Accumulator();

        Runnable addValueTask = () -> {
            Random randomValueGenerator = new Random();
            IntStream.range(0, 100).forEach(i -> accumulator.add(randomValueGenerator.nextInt(10) + 1));
        };

        Thread firstThread = new Thread(addValueTask);
        firstThread.setName("FirstThread");

        Thread secondThread = new Thread(addValueTask);
        secondThread.setName("SecondThread");

        Thread thirdThread = new Thread(addValueTask);
        thirdThread.setName("ThirdThread");

        firstThread.start();
        secondThread.start();
        thirdThread.start();

        try {
            firstThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception when joining thread '%s' thread. Message: %s", Thread.currentThread().getName(), ex.getMessage());
        }

        try {
            secondThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception when joining thread '%s' thread. Message: %s", Thread.currentThread().getName(), ex.getMessage());
        }

        try {
            thirdThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception when joining thread '%s' thread. Message: %s", Thread.currentThread().getName(), ex.getMessage());
        }

        System.out.printf("Final sum is: %d", accumulator.getSum());
    }
}

package local.practice.medium;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

/*
 Write a Java program that implements a producer-consumer pattern using a BlockingQueue. Create a SharedBuffer class
 with a BlockingQueue<Integer> of capacity 10. The producer thread generates 20 random integers (1 to 100, inclusive)
 and adds them to the queue, sleeping for a random duration (100-500ms) between additions. The consumer thread removes
 items from the queue and prints them, sleeping for a random duration (200-600ms) between removals.
 Use java.util.Random for random numbers and delays. The main thread should start both threads and wait for them to
 complete (e.g., by joining or ensuring the producer adds all 20 items and the consumer processes them).
 Ensure thread-safety using the BlockingQueue’s built-in synchronization.
 */
public class FirstExerciseProducerConsumer {
    private static class SharedBuffer {
        private final BlockingQueue<Integer> sharedBuffer = new ArrayBlockingQueue<>(10);

        void addValue(Integer value) {
            try {
                sharedBuffer.put(value);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.printf("Unexpected exception when adding a value by thread '%s'. Message: '%s'",
                        Thread.currentThread().getName(), ex.getMessage());
            }
        }

        Integer removeValue() {
            try {
                return sharedBuffer.take();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.printf("Unexpected exception when taking a value by thread '%s'. Message: '%s'",
                        Thread.currentThread().getName(), ex.getMessage());
                throw new RuntimeException(ex);
            }
        }

        int getSharedBufferSize() {
            return sharedBuffer.size();
        }
    }

    public static void main(String[] args) {
        SharedBuffer sharedBuffer = new SharedBuffer();
        Runnable producerTask = () -> {
            IntStream.range(0, 20).forEach(i -> {
                final Random randomNumberGenerator = new Random();
                int value = randomNumberGenerator.nextInt(100) + 1;
                sharedBuffer.addValue(value);
                try {
                    Thread.sleep(randomNumberGenerator.nextInt(401) + 100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Unexpected exception when adding a value '%d' by thread '%s'. Message: '%s'",
                            value, Thread.currentThread().getName(), e.getMessage());
                }
            });
        };

        Thread producerThread = new Thread(producerTask);
        producerThread.setName("ProducerThread");

        Runnable consumerTask = () -> {
            try {
                while (true) {
                    final Random randomNumberGenerator = new Random();
                    Thread.sleep(randomNumberGenerator.nextInt(401) + 200);
                    Integer head = sharedBuffer.removeValue();
                    if (head == -1) {
                        break;
                    }
                    System.out.println("Removed head of shared queue: " + head);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("Unexpected exception when consuming the head by thread '%s'. Message: '%s'",
                        Thread.currentThread().getName(), e.getMessage());
                throw new RuntimeException(e);
            }
        };

        Thread consumerThread = new Thread(consumerTask);
        consumerThread.setName("ConsumerThread");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception when joining thread '%s'. Message: '%s'",
                    Thread.currentThread().getName(), ex.getMessage());
        }

        //adding the poison pill
        sharedBuffer.addValue(-1);

        try {
            consumerThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception when joining thread '%s'. Message: '%s'",
                    Thread.currentThread().getName(), ex.getMessage());
        }

        System.out.println("In the end the shared buffer's size is: " + sharedBuffer.getSharedBufferSize());
    }
}

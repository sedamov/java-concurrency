package local.practice.medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a ResourcePool class with a fixed pool of 5 resources
 (represented as integers 1 to 5) stored in a List<Integer>. Implement thread-safe methods acquireResource() and
 releaseResource(int resource) using the synchronized keyword. The acquireResource method should block if no resources
 are available, using wait() and notifyAll(), and return an available resource. The releaseResource method returns a
 resource to the pool. Create three threads that each acquire a resource, hold it for a random duration (100-300ms),
 and release it, repeating this 50 times. Create one thread that acquires two resources, holds them for a random
 duration (200-400ms), and releases them, repeating this 25 times. Use java.util.Random for delays. The main thread
 should wait for all threads to complete and print the final number of available resources, which should be 5.
 */
public class FifthExerciseResourcePool {

    private static class ResourcePool {
        private final Object dedicatedLock = new Object();

        private final List<Integer> resources = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        int acquireResource() {
            synchronized (dedicatedLock) {
                while (resources.isEmpty()) {
                    try {
                        dedicatedLock.wait();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        System.out.printf("Failed to wait for available resource to be acquired by thread [%s]",
                                Thread.currentThread().getName());
                        throw new RuntimeException(ex);
                    }
                }
                Integer availableResource = resources.removeLast();
                dedicatedLock.notifyAll();
                return availableResource;
            }
        }

        void releaseResource(int acquiredResource) {
            synchronized (dedicatedLock) {
                resources.add(acquiredResource);
                dedicatedLock.notifyAll();
            }
        }

        synchronized int getAvailableResourceCount() {
            return resources.size();
        }
    }

    private static Runnable createSingleResourceAcquisitionTask(ResourcePool resourcePool) {
        return () -> {
            Random delayGenerator = new Random();
            IntStream.range(0, 50).forEach(i -> {
                int acquiredResource = resourcePool.acquireResource();
                try {
                    Thread.sleep(delayGenerator.nextInt(201) + 100);
                    resourcePool.releaseResource(acquiredResource);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to hold the resource [%d] acquired by thread [%s]",
                            acquiredResource, Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
    }

    private static Runnable createDoubleResourceAcquisitionTask(ResourcePool resourcePool) {
        return () -> {
            Random delayGenerator = new Random();
            IntStream.range(0, 25).forEach(i -> {
                int firstAcquiredResource = resourcePool.acquireResource();
                int secondAcquiredResource = resourcePool.acquireResource();
                try {
                    Thread.sleep(delayGenerator.nextInt(201) + 200);
                    resourcePool.releaseResource(firstAcquiredResource);
                    resourcePool.releaseResource(secondAcquiredResource);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Failed to hold the resources [%d] and [%d] acquired by thread [%s]",
                            firstAcquiredResource, secondAcquiredResource, Thread.currentThread().getName());
                    throw new RuntimeException(ex);
                }
            });
        };
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

    public static void main(String[] args) {
        ResourcePool resourcePool = new ResourcePool();

        Thread firstSingleAcquisitionThread = new Thread(createSingleResourceAcquisitionTask(resourcePool));
        Thread secondSingleAcquisitionThread = new Thread(createSingleResourceAcquisitionTask(resourcePool));
        Thread thirdSingleAcquisitionThread = new Thread(createSingleResourceAcquisitionTask(resourcePool));

        Thread doubleAcquisitionThread = new Thread(createDoubleResourceAcquisitionTask(resourcePool));

        firstSingleAcquisitionThread.start();
        secondSingleAcquisitionThread.start();
        thirdSingleAcquisitionThread.start();

        doubleAcquisitionThread.start();

        joinThread(firstSingleAcquisitionThread);
        joinThread(secondSingleAcquisitionThread);
        joinThread(thirdSingleAcquisitionThread);

        joinThread(doubleAcquisitionThread);

        System.out.printf("After all threads have completed, the number of available resources is: %d", resourcePool.getAvailableResourceCount());
    }
}

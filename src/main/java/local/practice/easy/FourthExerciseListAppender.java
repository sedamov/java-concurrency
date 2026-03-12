package local.practice.easy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/*
 Write a Java program that creates a SharedList class with an add(String item) method that adds a string to an
 internal ArrayList. The method should be thread-safe using the synchronized keyword to ensure safe concurrent additions.
 Create two threads that each add 100 unique strings (e.g., "Thread1-Item0", "Thread1-Item1", ..., "Thread2-Item99")
 to the list. The main thread should wait for both threads to complete and then print the total number of items in the
 list, which should be 200.
 */
public class FourthExerciseListAppender {

    private static class SharedList {
        private final ArrayList<String> sharedList = new ArrayList<>();

        synchronized void add(String item) {
            sharedList.add(item);
        }

        List<String> getSharedList() {
            return Collections.unmodifiableList(sharedList);
        }
    }

    public static void main(String[] args) {
        final SharedList sharedList = new SharedList();

        final String firstThreadName = "Thread0";
        final String secondThreadName = "Thread1";

        Thread firstThread = createSharedListIncrementThread(sharedList, firstThreadName);
        Thread secondThread = createSharedListIncrementThread(sharedList, secondThreadName);

        firstThread.start();
        secondThread.start();

        try {
            firstThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception in thread: '%s', message: %s", Thread.currentThread().getName(), ex.getMessage());
        }

        try {
            secondThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception in thread: '%s', message: %s", Thread.currentThread().getName(), ex.getMessage());
        }

        System.out.printf("Total number of items in shared list is %d", sharedList.getSharedList().size());
    }


    private static Thread createSharedListIncrementThread(final SharedList sharedList, final String threadName) {
        Runnable addToSharedListTask = () -> {
            IntStream.range(0, 100).forEach(i -> sharedList.add(threadName + "-Item"+i));
        };
        Thread addToSharedListThread = new Thread(addToSharedListTask);
        addToSharedListThread.setName(threadName);
        return addToSharedListThread;
    }
}

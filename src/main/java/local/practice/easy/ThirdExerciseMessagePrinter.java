package local.practice.easy;

import java.util.stream.IntStream;

/*
 Write a Java program that creates a MessagePrinter class with a printMessage(String message) method that prints a
 given message along with the current thread's name. The method should be thread-safe using the synchronized keyword
 to ensure messages are printed without interleaving (i.e., each message appears as a complete line in the output).
 Create three threads that each call printMessage 5 times with a unique message for each thread.
 The main thread should wait for all threads to complete before exiting.
 */
public class ThirdExerciseMessagePrinter {

    private static class MessagePrinter {
        synchronized void printMessage(String message) {
            System.out.printf("Printing message '%s' from thread '%s'%n", message, Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        final String messageFromFirstThread = "Knock-Knock-Knocking on Heaven's door";
        String firstThreadName = "GunsN'Roses";
        final String messageFromSecondThread = "Those were the days, my friend. We thought they'd never end";
        String secondThreadName = "Mary Hopkin";
        final String messageFromThirdThread = "and Nothing else matters";
        String thirdThreadName = "Metallica";

        final MessagePrinter messagePrinter = new MessagePrinter();

        Thread firstThread = createMessagePrinterThread(messagePrinter, messageFromFirstThread, firstThreadName);
        Thread secondThread = createMessagePrinterThread(messagePrinter, messageFromSecondThread, secondThreadName);
        Thread thirdThread = createMessagePrinterThread(messagePrinter, messageFromThirdThread, thirdThreadName);

        firstThread.start();
        secondThread.start();
        thirdThread.start();

        try {
            firstThread.join();
            secondThread.join();
            thirdThread.join();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.printf("Unexpected exception from thread %s: message: %s", Thread.currentThread().getName(), exception.getMessage());
        }

    }

    private static Thread createMessagePrinterThread(final MessagePrinter messagePrinter, final String message, String threadName) {
        Runnable firstThreadTask = () -> {
            IntStream.range(0, 5).forEach(i -> messagePrinter.printMessage(message));
        };

        Thread firstThread = new Thread(firstThreadTask);
        firstThread.setName(threadName);

        return firstThread;
    }
}

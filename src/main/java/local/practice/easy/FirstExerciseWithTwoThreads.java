package local.practice.easy;

/*
 Write a Java program that creates two threads using the Thread class.
 Each thread should print a message "Thread [name] is running" 5 times, with a 500ms delay between each print.
 Ensure the main thread waits for both threads to complete before the program exits.
 Use proper thread naming to identify each thread in the output.
 */
public class FirstExerciseWithTwoThreads {

    public static void main(String[] args) {
        Runnable runnableTask = () -> {
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Thread " + Thread.currentThread().getName() + " is running");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Unexpected exception while waiting for timeout to pass");
                System.out.println(e.getMessage());
            }
        };

        Thread firstThread = new Thread(runnableTask);
        firstThread.setName("First-ever-thread");
        Thread secondThread = new Thread(runnableTask);
        secondThread.setName("Second-hand thread");

        firstThread.start();
        secondThread.start();

        try {
            firstThread.join();
            secondThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Unexpected exception while joining threads: " + ex.getMessage());
        }
    }

}

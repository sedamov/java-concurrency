package local.practice.hard;

/*
 Write a Java program that creates a BoundedBuffer class with a fixed-size array (size 10) to store integers.
 Implement thread-safe methods put(int value) and take() using synchronized, where put blocks if the buffer is full
 and take blocks if the buffer is empty, using wait() and notifyAll(). Create three producer threads that each add 50
 random integers (1-100) with a random delay (50-200ms). Create two consumer threads that each remove 75 integers and
 print them with a random delay (100-250ms). Use java.util.Random for values and delays. Producers add a poison pill
 (-1) after their 50 values to signal consumers to stop after each consumes 75 values. The main thread waits for all
 threads to complete and prints the final buffer size, which should be 0.
 */
public class SecondExerciseBufferWithSeveralProducersAndConsumers {
}

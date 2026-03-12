package local.practice.hard;

/*
 Write a Java program that creates a TaskScheduler class with a PriorityQueue<ScheduledTask> to store tasks with
 execution times (long, milliseconds since epoch). Implement thread-safe methods scheduleTask(ScheduledTask task) and
 executeNextTask() using synchronized. The executeNextTask method blocks until a task’s execution time is reached,
 using wait() with a timeout, then removes and returns the task. Create two threads that each schedule 25 tasks with
 random execution times (current time + 100-500ms) and task names (e.g., "Task1-0", "Task2-24") with a random delay
 (50-200ms). Create two threads that each execute 25 tasks, printing the task name and execution time.
 Use java.util.Random for times and delays. The main thread waits for all threads to complete and prints the final
 queue size (0). Define a ScheduledTask class with name (String) and executionTime (long), comparable by execution time.
 */
public class FifthExerciseTaskScheduler {
}

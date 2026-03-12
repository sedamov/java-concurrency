package local.practice.hard;

/*
 Write a Java program that creates a ResourceAllocator class managing 3 resources (IDs 1 to 3).
 Implement thread-safe methods allocate(int priority) and release(int resourceId) using synchronized.
 The allocate method takes a priority (1-5, lower = higher priority) and blocks if no resources are available,
 prioritizing higher-priority requests using a PriorityQueue and wait()/notifyAll(). Create three threads that each
 request 20 resources with random priorities (1-5) and hold them for a random duration (100-300ms) before releasing.
 Create one thread that requests 30 resources with fixed priority 1 (highest) and holds them for 200-400ms.
 Use java.util.Random for priorities and delays. The main thread waits for all threads to complete and prints the
 final number of available resources (3).
 */
public class ThirdExercisePriorityResourceAllocator {
}

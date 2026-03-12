package local.practice.hard;

/*
 Write a Java program that creates a VotingSystem class to track votes for 5 candidates (IDs 1 to 5).
 Implement thread-safe methods vote(int candidateId) and getResults() using synchronized. The vote method increments a
 candidate’s count and blocks if the total votes reach 100, using wait()/notifyAll(). The getResults method returns a
 map of candidate IDs to vote counts. Create four threads that each cast 25 votes for random candidates (1-5) with a
 random delay (50-150ms). Create one thread that calls getResults every 500ms, prints the current results, and stops
 after all votes are cast (100 total). Use java.util.Random for candidate selection and delays. The main thread waits
 for all threads to complete and prints the final vote counts, summing to 100.
 */
public class FourthExerciseVotingSystem {
}

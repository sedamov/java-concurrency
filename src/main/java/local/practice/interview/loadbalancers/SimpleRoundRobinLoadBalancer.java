package local.practice.interview.loadbalancers;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem Statement:
 * <p>
 * Design and implement a simple load balancer in Java that distributes incoming requests across a pool of servers
 * using a round-robin algorithm. The load balancer should allow adding and removing servers dynamically, and it
 * should handle requests by returning the next available server in a cyclical manner.
 * Requirements:
 * <p>
 * Create a class LoadBalancer with the following methods:
 * <p>
 * 1. void addServer(String serverId): Add a server to the pool (serverId is a unique string identifier).
 * 2. void removeServer(String serverId): Remove a server from the pool. If the server doesn't exist, throw an
 * appropriate exception.
 * 3. String getNextServer(): Return the next server in round-robin order. If no servers are available, throw an exception.
 * <p>
 * <p>
 * Ensure the implementation is thread-safe (e.g., using synchronization or concurrent collections) to handle concurrent
 * additions, removals, and requests.
 * Use an appropriate data structure (e.g., a list or queue) to maintain the server pool.
 * Handle edge cases like empty pool, duplicate server additions, or removing the last server.
 * <p>
 * Unit Tests:
 * <p>
 * 1. Write at least 5 JUnit tests covering: adding/removing servers, round-robin distribution, thread safety
 * (e.g., concurrent gets), and error scenarios.
 * 2. Example test ideas: Verify round-robin order after multiple gets; ensure removal interrupts the cycle correctly;
 * test concurrent access with multiple threads.
 */
public class SimpleRoundRobinLoadBalancer {
    private final Object dedicatedLock = new Object();

    private final List<String> servers;
    private int nextServerIndex = 0;

    public SimpleRoundRobinLoadBalancer() {
        this.servers = new ArrayList<>();
    }

    public void addServer(String serverId) {
        synchronized (dedicatedLock) {
            if (serverId == null || this.servers.contains(serverId)) {
                throw new IllegalArgumentException(String.format("Server with ID [%s] either already exists or is null", serverId));
            }
            servers.add(serverId);
        }
    }

    public void removeServer(String serverId) {
        synchronized (dedicatedLock) {
            if (serverId == null || !servers.contains(serverId)) {
                throw new IllegalStateException(
                        String.format(
                                "The server with ID [%s] you are trying to remove either does not exist or is null",
                                serverId
                        )
                );
            }
            servers.remove(serverId);
            if (!servers.isEmpty()) nextServerIndex %= servers.size();
        }
    }

    public String getNextServer() {
        synchronized (dedicatedLock) {
            if (this.servers.isEmpty() || nextServerIndex >= this.servers.size()) {
                throw new IllegalStateException("No available servers in the server pool or the next index is invalid");
            }
            return servers.get(nextServerIndex);
        }
    }

    public void clearServers() {
        synchronized (dedicatedLock) {
            this.servers.clear();
            nextServerIndex = 0;
        }
    }

    public List<String> getServers() {
        return this.servers;
    }
}


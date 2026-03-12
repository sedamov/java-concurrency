package local.practice.interview.loadbalancers;

import local.practice.interview.internalmodels.WeightedServer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Problem Statement:
 * <p>
 * Design and implement a weighted load balancer in Java that distributes incoming requests across a pool of servers
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
public class WeightedRoundRobinLoadBalancer {

    private final Comparator<WeightedServer> serverComparator = Comparator.comparingInt(WeightedServer::getCurrentWeight).reversed();

    private final ReentrantReadWriteLock explicitLock = new ReentrantReadWriteLock();
    private final Set<WeightedServer> weightedServersPool = new LinkedHashSet<>();
    private final AtomicInteger totalWeightOfServerPool = new AtomicInteger(0);

    public void addServer(WeightedServer server) {
        explicitLock.writeLock().lock();
        if (server == null) {
            throw new IllegalArgumentException("Can not add null server to server pool");
        }
        weightedServersPool.add(server);
        totalWeightOfServerPool.set(totalWeightOfServerPool.get() + server.getInitialWeight());
        explicitLock.writeLock().unlock();

    }

    public void removeServer(WeightedServer server) {
        explicitLock.writeLock().lock();
        if (server == null) {
            explicitLock.writeLock().unlock();
            throw new IllegalArgumentException("Can not remove null server");
        } else if (weightedServersPool.isEmpty()) {
            explicitLock.writeLock().unlock();
            throw new IllegalStateException("Server pool is empty");
        }
        weightedServersPool.remove(server);
        totalWeightOfServerPool.set(totalWeightOfServerPool.get() - server.getInitialWeight());
        explicitLock.writeLock().unlock();

    }

    public WeightedServer getNextServer() {
        explicitLock.writeLock().lock();
        if (weightedServersPool.isEmpty()) {
            explicitLock.writeLock().unlock();
            throw new IllegalStateException("Server pool is empty");
        }
        final WeightedServer[] selectedServer = {null};
        final int[] currentMaxWeight = {0};
        weightedServersPool.forEach(server -> {
            server.increaseCurrentWeight();
            if (server.getCurrentWeight() >= currentMaxWeight[0]) {
                currentMaxWeight[0] = server.getCurrentWeight();
                selectedServer[0] = server;
            }
        });
        selectedServer[0].setCurrentWeight(selectedServer[0].getCurrentWeight() - totalWeightOfServerPool.get());
        explicitLock.writeLock().unlock();
        return selectedServer[0];
    }

    public void clearServers() {
        explicitLock.writeLock().lock();
        this.weightedServersPool.clear();
        this.totalWeightOfServerPool.set(0);
        explicitLock.writeLock().unlock();
    }

    public List<WeightedServer> getListOfServers() {
        explicitLock.readLock().lock();
        try {
            return new ArrayList<>(this.weightedServersPool);
        } finally {
            explicitLock.readLock().unlock();
        }
    }
}

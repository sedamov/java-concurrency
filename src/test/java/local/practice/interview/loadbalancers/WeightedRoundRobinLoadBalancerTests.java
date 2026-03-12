package local.practice.interview.loadbalancers;

import local.practice.interview.internalmodels.WeightedServer;
import local.practice.interview.util.WeightedRoundRobinTaskDefinitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class WeightedRoundRobinLoadBalancerTests {

    WeightedRoundRobinLoadBalancer balancer = new WeightedRoundRobinLoadBalancer();

    @BeforeEach
    public void clearServers() {
        balancer.clearServers();
    }


    @Test
    public void addOneServer() {
        WeightedServer server = new WeightedServer("Server_1", 3);
        balancer.addServer(server);
        List<WeightedServer> weightedServers = balancer.getListOfServers();
        assertFalse(weightedServers.isEmpty(), "Balancer should have non-empty pool of servers");
        assertEquals(1, weightedServers.size(), "Balancer should have only 1 server");
        assertTrue(weightedServers.contains(server), String.format("Balancer should contain server [%s]", server));
    }

    @Test
    public void addMultipleServers() {
        Thread addSeveralServers = new Thread(WeightedRoundRobinTaskDefinitions.addServers(balancer));
        addSeveralServers.start();
        try {
            addSeveralServers.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            fail("Failed to wait for servers to be added");
        }

        List<WeightedServer> weightedServers = balancer.getListOfServers();
        assertFalse(weightedServers.isEmpty(), "Balancer should have non-empty pool of servers");
        assertEquals(10, weightedServers.size(), "Balancer should have 10 servers");
    }

    @Test
    public void addDuplicateServers() {
        WeightedServer firstServer = new WeightedServer("Server_1", 1);
        WeightedServer duplicateServer = new WeightedServer("Server_1", 1);

        balancer.addServer(firstServer);
        balancer.addServer(duplicateServer);

        List<WeightedServer> serverPool = balancer.getListOfServers();
        assertNotNull(serverPool, "Server Pool can not be null");
        assertFalse(serverPool.isEmpty(), "Server pool can not be empty");
        assertEquals(1, serverPool.size(), "Server pool should contain only 1 element");
    }

    @Test
    public void addNullServer() {
        assertThrows(IllegalArgumentException.class, () -> balancer.addServer(null));
    }

    @Test
    public void removeOneServer() {
        WeightedServer serverToRemove = new WeightedServer("Server_2", 8);
        balancer.addServer(new WeightedServer("Server_1", 3));
        balancer.addServer(new WeightedServer("Server_3", 10));
        balancer.addServer(serverToRemove);
        List<WeightedServer> serversBeforeRemoval = balancer.getListOfServers();

        assertFalse(serversBeforeRemoval.isEmpty(), "Balancer should contain non-empty server pool");
        assertEquals(3, serversBeforeRemoval.size(), "Balancer should contain only 3 servers");

        balancer.removeServer(serverToRemove);

        List<WeightedServer> serversAfterRemoval = balancer.getListOfServers();

        assertFalse(serversAfterRemoval.isEmpty(), "Balancer should contain non-empty server pool");
        assertEquals(2, serversAfterRemoval.size(), "Balancer should contain only 2 servers");

    }

    @Test
    public void removeNonExistentServer() {
        balancer.addServer(new WeightedServer("Server_1", 1));
        balancer.removeServer(new WeightedServer("Server_3", 1));

        List<WeightedServer> serverPool = balancer.getListOfServers();
        assertFalse(serverPool.isEmpty(), "Balancer should contain non-empty server pool");
        assertEquals(1, serverPool.size(), "Balancer should contain only 1 server");
    }

    @Test
    public void removeServerFromEmptyServerPool() {
        assertThrows(IllegalStateException.class, () -> balancer.removeServer(new WeightedServer("Server_3", 1)));
    }

    @Test
    public void removeNullServer() {
        assertThrows(IllegalArgumentException.class, () -> balancer.removeServer(null));
    }

    @Test
    public void requestNextAvailableServer() throws IllegalAccessException {
        //Ensure the server with the highest weight is returned and removed from the server pool
        balancer.addServer(new WeightedServer("Server_2", 4));
        balancer.addServer(new WeightedServer("Server_1", 10));
        balancer.addServer(new WeightedServer("Server_3", 8));

        WeightedServer nextAvailableServer = balancer.getNextServer();
        assertNotNull(nextAvailableServer, "Requested server should not be null");
        assertEquals("Server_1", nextAvailableServer.getName(), "Server_1 should be the next available server");
    }

    @Test
    public void requestNextAvailableServerOnEmptyServerPool() {
        assertThrows(IllegalStateException.class, balancer::getNextServer);
    }

    @Test
    public void concurrentAddAndRemoveServers() {
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(WeightedRoundRobinTaskDefinitions.addServersWithLatch(balancer, latch));
        executorService.submit(WeightedRoundRobinTaskDefinitions.removeServersWithLatch(balancer, latch));

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Failed to await for latch");
        }

        executorService.shutdown();

        List<WeightedServer> weightedServers = balancer.getListOfServers();

        assertFalse(weightedServers.isEmpty(), "Balancer should contain non-empty server pool");
        assertEquals(5, weightedServers.size(), "Balancer should contain 5 servers");

    }

    @Test
    public void concurrentAddAndRequestNextAvailableServers() {
        List<WeightedServer> requestedServers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(WeightedRoundRobinTaskDefinitions.addServersWithLatch(balancer, latch));
        executorService.submit(WeightedRoundRobinTaskDefinitions.requestNextAvailableServerWithLatch(balancer, requestedServers, latch));

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Failed to await for latch");
        }
        executorService.shutdown();
        List<WeightedServer> weightedServers = balancer.getListOfServers();

        assertFalse(weightedServers.isEmpty(), "Balancer should contain non-empty server pool");
        assertEquals(10, weightedServers.size(), "Balancer should contain 10 servers");

        assertFalse(requestedServers.isEmpty(), "Requested servers should not be empty");
        assertEquals(5, requestedServers.size(), "5 servers should have been successfully requested");

    }
}

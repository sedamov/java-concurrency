package local.practice.interview.loadbalancers;


import local.practice.interview.util.SimpleRoundRobinTaskDefinitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleRoundRobinLoadBalancerTest {

    private final SimpleRoundRobinLoadBalancer simpleRoundRobinLoadBalancer = new SimpleRoundRobinLoadBalancer();

    @BeforeEach
    public void init() {
        simpleRoundRobinLoadBalancer.clearServers();
    }

    @Test
    public void simpleRoundRobin_add_one_server() {
        String randomUUIDForServer = UUID.randomUUID().toString();
        simpleRoundRobinLoadBalancer.addServer(randomUUIDForServer);
        String nextServer = simpleRoundRobinLoadBalancer.getNextServer();
        assertEquals(randomUUIDForServer, nextServer, "Returned server ID should match with added server ID");
    }

    @Test
    public void simpleRoundRobin_add_null_server_id() {
        assertThrows(IllegalArgumentException.class, () -> simpleRoundRobinLoadBalancer.addServer(null));
    }

    @Test
    public void simpleRoundRobin_add_duplicate_servers() {
        String randomUUIDForServer = UUID.randomUUID().toString();
        simpleRoundRobinLoadBalancer.addServer(randomUUIDForServer);
        assertThrows(IllegalArgumentException.class, () -> simpleRoundRobinLoadBalancer.addServer(randomUUIDForServer));
    }

    @Test
    public void simpleRoundRobin_remove_existing_server() {
        String randomUUIDForServer = UUID.randomUUID().toString();
        simpleRoundRobinLoadBalancer.addServer(randomUUIDForServer);
        simpleRoundRobinLoadBalancer.removeServer(randomUUIDForServer);
        assertThrows(IllegalStateException.class,
                simpleRoundRobinLoadBalancer::getNextServer);
    }

    @Test
    public void simpleRoundRobin_remove_from_empty_server_list() {
        assertThrows(IllegalStateException.class, () -> simpleRoundRobinLoadBalancer.removeServer("random-server-id"));
    }

    @Test
    public void simpleRoundRobin_remove_non_existent_server_from_non_empty_list() {
        String randomUUIDForServer = UUID.randomUUID().toString();
        simpleRoundRobinLoadBalancer.addServer(randomUUIDForServer);
        assertThrows(IllegalStateException.class, () -> simpleRoundRobinLoadBalancer.removeServer("random-server-id"));
    }

    @Test
    public void simpleRoundRobin_remove_server_with_null_id() {
        assertThrows(IllegalStateException.class, () -> simpleRoundRobinLoadBalancer.removeServer(null));
    }

    @Test
    public void simpleRoundRobin_get_next_server_from_empty_server_pool() {
        assertThrows(IllegalStateException.class, simpleRoundRobinLoadBalancer::getNextServer);
    }

    @Test
    public void simpleRoundRobin_concurrent_add_remove() {

        Thread threadToAdd = new Thread(SimpleRoundRobinTaskDefinitions.getRunnableForAddingServers(simpleRoundRobinLoadBalancer));
        threadToAdd.start();

        Thread threadToRemove = new Thread(SimpleRoundRobinTaskDefinitions.getRunnableForRemovingServers(simpleRoundRobinLoadBalancer));
        threadToRemove.start();

        try {
            threadToAdd.join();
            threadToRemove.join();
        } catch (InterruptedException e) {
            fail("Failed to join threads");
        }

        assertThrows(
                IllegalStateException.class, simpleRoundRobinLoadBalancer::getNextServer
        );
    }

    @Test
    public void simpleRoundRobin_concurrent_add_remove_get_next() {
        Thread threadToAdd = new Thread(SimpleRoundRobinTaskDefinitions.getRunnableForAddingServers(simpleRoundRobinLoadBalancer));
        threadToAdd.start();

        Thread threadToRemove = new Thread(SimpleRoundRobinTaskDefinitions.getRunnableForRemovingServers(simpleRoundRobinLoadBalancer));
        threadToRemove.start();

        List<String> results = new ArrayList<>();
        Thread threadToRequestNextAvailable = new Thread(
                SimpleRoundRobinTaskDefinitions
                        .getRunnableForRequestingNextServer(simpleRoundRobinLoadBalancer, results)
        );
        threadToRequestNextAvailable.start();

        try {
            threadToAdd.join();
            threadToRemove.join();
            threadToRequestNextAvailable.join();
        } catch (InterruptedException e) {
            fail("Failed to join threads");
        }

        assertEquals(0, simpleRoundRobinLoadBalancer.getServers().size(), "No available servers should be left");
        assertFalse(results.isEmpty(), "Available servers list from calling getNextServer should not be empty");
        assertEquals(10, results.size(), "Available servers list from calling getNextServer should have 10 elements");
    }

    @Test
    public void simpleRoundRobin_get_next_available_server_round() {
        simpleRoundRobinLoadBalancer.addServer("server_1");
        simpleRoundRobinLoadBalancer.addServer("server_2");
        simpleRoundRobinLoadBalancer.addServer("server_3");

        List<String> requestedAvailableServers = IntStream.range(0, 9)
                .mapToObj(i -> simpleRoundRobinLoadBalancer.getNextServer())
                .toList();

        assertAll(
                IntStream.range(0, requestedAvailableServers.size() - 3)
                        .mapToObj(i -> () ->
                                assertEquals(requestedAvailableServers.get(i), requestedAvailableServers.get(i + 3),
                                        "Mismatch at index " + i + " and " + (i + 3))
                        )
        );

    }
}

package local.practice.interview.util;

import local.practice.interview.loadbalancers.SimpleRoundRobinLoadBalancer;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;

public class SimpleRoundRobinTaskDefinitions {

    public static Runnable getRunnableForAddingServers(SimpleRoundRobinLoadBalancer balancer) {
        return () -> IntStream.range(0, 10).forEach(i -> {
            try {
                balancer.addServer(String.format("server_%d", i));
                System.out.println("Added server: server_" + i);
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                fail("Failed to delay adding servers");
            } catch (IllegalArgumentException ex) {
                fail("Duplicate server has been added to the server pool: iteration " + i);
            }
        });
    }

    public static Runnable getRunnableForRemovingServers(SimpleRoundRobinLoadBalancer balancer) {
        return () -> IntStream.range(0, 10).forEach(i -> {
            try {
                Thread.sleep(20);
                balancer.removeServer(String.format("server_%d", i));
                System.out.println("Removed server: server_" + i);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                fail("Failed to delay adding servers");
            } catch (IllegalStateException ex) {
                fail("Failed to remove server from the server pool: iteration " + i + ", exception: " + ex.getMessage());
            }
        });
    }

    public static Runnable getRunnableForRequestingNextServer(SimpleRoundRobinLoadBalancer balancer, List<String> results) {
        return () -> IntStream.range(0, 10).forEach(i -> {
            try {
                Thread.sleep(15);
                results.add(balancer.getNextServer());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                fail("Failed to delay adding servers");
            } catch (IllegalStateException ex) {
                fail("Failed to get next available server: iteration " + i);
            }
        });
    }
}

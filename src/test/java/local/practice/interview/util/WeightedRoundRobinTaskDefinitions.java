package local.practice.interview.util;

import local.practice.interview.internalmodels.WeightedServer;
import local.practice.interview.loadbalancers.WeightedRoundRobinLoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;

public class WeightedRoundRobinTaskDefinitions {

    public static Runnable addServers(WeightedRoundRobinLoadBalancer balancer) {
        return () -> {
            Random randomWeightGenerator = new Random();
            IntStream.range(0, 5).forEach(i ->
                    {
                        balancer.addServer(new WeightedServer("Server_" + i, i));
                        balancer.addServer(new WeightedServer("Server_" + i, randomWeightGenerator.nextInt(6) + 10));
                    }
            );
        };
    }

    public static Runnable addServersWithLatch(WeightedRoundRobinLoadBalancer balancer, CountDownLatch latch) {
        return () -> {
            Random randomWeightGenerator = new Random();
            IntStream.range(0, 5).forEach(i ->
                    {
                        WeightedServer firstServer = new WeightedServer("Server_" + i, i);
                        WeightedServer secondServer = new WeightedServer("Server_" + i, randomWeightGenerator.nextInt(6) + 10);
                        balancer.addServer(firstServer);
                        balancer.addServer(secondServer);
                        System.out.println("Added servers: " + firstServer + " and " + secondServer);
                    }
            );
            latch.countDown();
        };
    }

    public static Runnable removeServersWithLatch(WeightedRoundRobinLoadBalancer balancer, CountDownLatch latch) {
        return () ->
        {
            IntStream.range(0, 5).forEach(i -> balancer.removeServer(new WeightedServer("Server_" + i, i)));
            latch.countDown();
        };
    }

    public static Runnable requestNextAvailableServerWithLatch(WeightedRoundRobinLoadBalancer balancer,
                                                               List<WeightedServer> resultingServers,
                                                               CountDownLatch latch) {
        return () -> {
            IntStream.range(0, 5).forEach(i -> {
                try {
                    System.out.println("Requested server for " + i + " time");
                    WeightedServer receivedServer = balancer.getNextServer();
                    resultingServers.add(receivedServer);
                    System.out.println("Received server  " + receivedServer);
                } catch (IllegalStateException ex) {
                    fail("Server pool is empty");
                }
            });
            latch.countDown();
        };
    }
}

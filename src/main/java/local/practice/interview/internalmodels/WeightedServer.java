package local.practice.interview.internalmodels;

import java.util.Objects;

public final class WeightedServer {
    private final String name;
    private final int initialWeight;

    private int currentWeight = 0;

    public WeightedServer(String name, int weight) {
        this.name = name;
        this.initialWeight = weight;
    }

    public String getName() {
        return this.name;
    }

    public int getInitialWeight() {
        return this.initialWeight;
    }

    public int getCurrentWeight() {
        return this.currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void increaseCurrentWeight() {
        this.currentWeight += this.initialWeight;
    }


    @Override
    public String toString() {
        return String.format("WeightedServer{ name = %s, weight = %d }", this.name, this.initialWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightedServer that = (WeightedServer) o;
        return initialWeight == that.initialWeight && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, initialWeight);
    }
}

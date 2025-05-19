package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandomSelect<T extends Object> {

    private class Entry {
        double accumulatedWeight;
        int object;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private Random rand = new Random();

    public void addEntry(int i, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = i;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    @SuppressWarnings("null")
	public int getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return (Integer) null; //should only happen when there are no entries
    }
}
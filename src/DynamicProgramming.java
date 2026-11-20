public class DynamicProgramming {
    public static long solve(Knapsack problem) {
        // best[c] is the largest value that fits in capacity c.
        long[] best = new long[problem.capacity + 1];

        for (int i = 0; i < problem.values.length; i++) {
            int weight = problem.weights[i];
            int value = problem.values[i];
            // Visit capacities backwards so each item can be taken only once.
            for (int c = problem.capacity; c >= weight; c--) {
                long skip = best[c];
                long take = best[c - weight] + value;
                best[c] = Math.max(skip, take);
            }
        }
        return best[problem.capacity];
    }
}

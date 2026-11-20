import java.util.Arrays;

public class Greedy {
    public static long solve(Knapsack problem) {
        Integer[] order = new Integer[problem.values.length];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }

        // sort by decreasing value to weight ratio
        Arrays.sort(order, (i, j) -> {
            double ratioI = (double) problem.values[i] / problem.weights[i];
            double ratioJ = (double) problem.values[j] / problem.weights[j];
            return Double.compare(ratioJ, ratioI);
        });

        int remaining = problem.capacity;
        long value = 0;
        for (int i : order) {
            if (problem.weights[i] > remaining) {
                break;
            }
            remaining -= problem.weights[i];
            value += problem.values[i];
        }
        return value;
    }
}

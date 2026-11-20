import java.util.Arrays;

public class Fptas {
    public static long solve(Knapsack problem, double epsilon) {
        int n = problem.values.length;
        int maxValue = 0;
        for (int i = 0; i < n; i++) {
            maxValue = Math.max(maxValue, problem.values[i]);
        } // assuming all items fit

        double K = Math.max(1.0, epsilon * maxValue / n);
        int[] scaledValues = new int[n];
        int totalProfit = 0;
        for (int i = 0; i < n; i++) {
            if (problem.weights[i] <= problem.capacity) {
                // Casting a nonnegative value to int rounds it down
                scaledValues[i] = (int) (problem.values[i] / K);
                totalProfit = Math.addExact(totalProfit, scaledValues[i]);
            }
        }

        // For each scaled profit, keep the lightest packing and its original value
        int[] minWeight = new int[totalProfit + 1];
        long[] originalValue = new long[totalProfit + 1];
        Arrays.fill(minWeight, -1);
        minWeight[0] = 0;
        int bestProfit = 0;
        for (int i = 0; i < n; i++) {
            if (scaledValues[i] == 0) {
                continue;
            }
            // Going backwards means item i can only be taken once
            for (int p = bestProfit; p >= 0; p--) {
                if (minWeight[p] < 0 || minWeight[p] > problem.capacity - problem.weights[i]) {
                    continue;
                }
                int newProfit = p + scaledValues[i];
                int newWeight = minWeight[p] + problem.weights[i];
                if (minWeight[newProfit] < 0 || newWeight < minWeight[newProfit]) {
                    minWeight[newProfit] = newWeight;
                    originalValue[newProfit] = originalValue[p] + problem.values[i];
                    bestProfit = Math.max(bestProfit, newProfit);
                }
            }
        }
        return originalValue[bestProfit];
    }
}

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Knapsack {
    final int capacity;
    final int[] values;
    final int[] weights;

    public Knapsack(int capacity, int[] values, int[] weights) {
        this.capacity = capacity;
        this.values = values;
        this.weights = weights;
    }

    public static Knapsack read(Path file) throws IOException {
        try (Scanner input = new Scanner(file)) {
            int n = input.nextInt();
            int capacity = input.nextInt();
            int[] values = new int[n];
            int[] weights = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = input.nextInt();
            }
            for (int i = 0; i < n; i++) {
                weights[i] = input.nextInt();
            }
            return new Knapsack(capacity, values, weights);
        }
    }
}

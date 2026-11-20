import java.nio.file.Path;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws Exception {
        String mode = "all";
        int firstFile = 0;
        if (args.length > 0 && (args[0].equals("--gurobi-only") || args[0].equals("--no-gurobi"))) {
            mode = args[0];
            firstFile = 1;
        }

        System.err.println("Java " + System.getProperty("java.version"));
        if (!mode.equals("--no-gurobi")) {
            System.err.println("Gurobi " + GurobiSolver.version());
        }
        System.out.println("instance,method,epsilon,value,seconds,gap_percent,status");

        if (firstFile == args.length) {
            for (int i = 1; i <= 10; i++) {
                run(Path.of("instances", "instance" + i + ".txt"), mode);
            }
        } else {
            for (int i = firstFile; i < args.length; i++) {
                run(Path.of(args[i]), mode);
            }
        }
    }

    static void run(Path file, String mode) throws Exception {
        Knapsack problem = Knapsack.read(file);
        String instance = file.getFileName().toString();

        GurobiSolver.Result gurobi = null;
        if (!mode.equals("--no-gurobi")) {
            System.err.printf("Solving %s with Gurobi (%d-second limit)...%n",
                    instance, GurobiSolver.TIME_LIMIT_SECONDS);
            long start = System.nanoTime();
            gurobi = GurobiSolver.solve(problem);
            System.err.printf(Locale.ROOT, "Gurobi %s on %s (optimization %.3f s, total %.3f s).%n",
                    gurobi.status(), instance, gurobi.seconds(), secondsSince(start));
            if (mode.equals("--gurobi-only")) {
                printGurobiResult(instance, gurobi, null);
                return;
            }
        }

        long start = System.nanoTime();
        long optimum = DynamicProgramming.solve(problem);
        double dpSeconds = secondsSince(start);
        if (gurobi != null) {
            printGurobiResult(instance, gurobi, optimum);
        }
        printResult(instance, "DP", "", optimum, dpSeconds, optimum, "OPTIMAL");

        start = System.nanoTime();
        long greedy = Greedy.solve(problem);
        printResult(instance, "Greedy", "", greedy, secondsSince(start), optimum, "FEASIBLE");

        for (double epsilon : new double[]{10, 1, 0.1, 0.01}) {
            start = System.nanoTime();
            long value = Fptas.solve(problem, epsilon);
            double seconds = secondsSince(start);
            printResult(instance, "FPTAS", Double.toString(epsilon), value, seconds, optimum, "FEASIBLE");
        }
    }

    static void printGurobiResult(String instance, GurobiSolver.Result result, Long optimum) {
        printResult(instance, "Gurobi", "", result.value(), result.seconds(), optimum, result.status());
    }

    static double secondsSince(long start) {
        return (System.nanoTime() - start) / 1_000_000_000.0;
    }

    static void printResult(String instance, String method, String epsilon,
                            Long value, double seconds, Long optimum, String status) {
        String gap = "";
        if (value != null && optimum != null) {
            double percent = optimum == 0 ? 0 : 100.0 * (optimum - value) / optimum;
            gap = String.format(Locale.ROOT, "%.6f", percent);
        }
        System.out.printf(Locale.ROOT, "%s,%s,%s,%s,%.6f,%s,%s%n",
                instance, method, epsilon, value == null ? "" : value, seconds, gap, status);
    }
}

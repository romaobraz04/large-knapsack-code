# Large knapsack

Run these PowerShell commands from the repository root with a JDK and Gurobi installed. `GUROBI_HOME` should point to the Gurobi installation, with its `bin` directory on `PATH`. The commercial solver needs a Gurobi license.

```powershell
javac -cp "$env:GUROBI_HOME\lib\gurobi.jar" -d out src\*.java
java -cp "out;$env:GUROBI_HOME\lib\gurobi.jar" Main
```

The default run reads `instances/instance1.txt` through `instances/instance10.txt`. For each instance it runs Gurobi, DP, greedy and FPTAS in that order. To use a single instance, append its path after `Main`.

To run the binary model directly with Gurobi on instance 1 and save its result, use the following command. Omit the instance path to run Gurobi on all ten files.

```powershell
java -cp "out;$env:GUROBI_HOME\lib\gurobi.jar" Main --gurobi-only instances\instance1.txt > gurobi-results.csv
```

Use `--no-gurobi` to run DP, greedy and FPTAS. For any mode, standard output contains the CSV rows and standard error shows progress.

Each output row gives the objective in the original item values, elapsed seconds and status. Runs that include DP also report the percentage gap from its exact optimum. In Gurobi-only mode that gap column is empty. Timings exclude reading the instance. The Gurobi CSV row uses the solver's optimization runtime. The console also shows the total elapsed time including model setup and cleanup.

## Algorithms

- **Gurobi** uses one binary variable per item, maximizes total value and limits total weight to capacity. The time limit is 900 seconds per instance and the relative MIP gap is zero. A `TIME_LIMIT` row gives the best feasible objective found, if available. Gurobi can spend additional time finalizing the result. In a full run, the other methods then run on the same instance and the program moves on to the next file.
- **Exact DP** keeps the best value for each capacity. It visits capacities in descending order for each item, so it uses each item at most once. It takes O(n × capacity) time and O(capacity) memory.
- **Greedy** follows slide 20. It sorts by decreasing value per unit of weight and takes the consecutive items that fit. At the first item that is too heavy, it stops. Equal ratios use the later item first, as in the Python version.
- **FPTAS** follows slides 88–91 with ε = 10, 1, 0.1 and 0.01. It divides each value by K = max(1, ε × largest item value / n) and rounds down. Each item in the supplied instances fits on its own. When K is 1, integer values are exact. For each scaled profit, the DP keeps the lightest feasible packing and its original value in one row. This gives the objective directly and avoids storing a table for backtracking.

import com.gurobi.gurobi.*;

public class GurobiSolver {
    static final int TIME_LIMIT_SECONDS = 900;

    record Result(Long value, double seconds, String status) {}

    static String version() {
        return GRB.VERSION_MAJOR + "." + GRB.VERSION_MINOR + "." + GRB.VERSION_TECHNICAL;
    }

    static Result solve(Knapsack problem) throws GRBException {
        GRBEnv env = new GRBEnv(true);
        GRBModel model = null;
        try {
            env.set(GRB.IntParam.OutputFlag, 0);
            env.start();
            model = new GRBModel(env);
            model.set(GRB.DoubleParam.TimeLimit, TIME_LIMIT_SECONDS);
            model.set(GRB.DoubleParam.MIPGap, 0);

            GRBLinExpr totalValue = new GRBLinExpr();
            GRBLinExpr totalWeight = new GRBLinExpr();
            for (int i = 0; i < problem.values.length; i++) {
                GRBVar take = model.addVar(0, 1, 0, GRB.BINARY, "take" + i);
                totalValue.addTerm(problem.values[i], take);
                totalWeight.addTerm(problem.weights[i], take);
            }
            model.setObjective(totalValue, GRB.MAXIMIZE);
            model.addConstr(totalWeight, GRB.LESS_EQUAL, problem.capacity, "capacity");
            model.optimize();

            int statusCode = model.get(GRB.IntAttr.Status);
            String status = switch (statusCode) {
                case GRB.OPTIMAL -> "OPTIMAL";
                case GRB.TIME_LIMIT -> "TIME_LIMIT";
                default -> "STATUS_" + statusCode;
            };
            Long value = null;
            if (model.get(GRB.IntAttr.SolCount) > 0) {
                value = Math.round(model.get(GRB.DoubleAttr.ObjVal));
            }
            return new Result(value, model.get(GRB.DoubleAttr.Runtime), status);
        } finally {
            if (model != null) {
                model.dispose();
            }
            env.dispose();
        }
    }
}

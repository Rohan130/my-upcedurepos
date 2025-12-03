package project.spreadsheet.formula;

import java.util.List;

public class EvalContext {

    // For now: NO spreadsheet here until deliverable 4

    public double apply(String name, List<Expr> args) {
        return switch (name.toUpperCase()) {
            case "SIN" -> Math.sin(args.get(0).eval(this));
            case "COS" -> Math.cos(args.get(0).eval(this));
            default -> throw new IllegalArgumentException("Unknown function: " + name);
        };
    }
}

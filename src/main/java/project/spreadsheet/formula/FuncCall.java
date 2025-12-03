package project.spreadsheet.formula;

import java.util.List;

/**
 * Composite node: function call like SIN(3).
 */
public class FuncCall implements Expr {

    private final String name;
    private final List<Expr> args;

    public FuncCall(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public double eval(EvalContext ctx) {
        return ctx.apply(name, args);
    }

    public String getName() {
        return name;
    }

    public List<Expr> getArgs() {
        return args;
    }
}

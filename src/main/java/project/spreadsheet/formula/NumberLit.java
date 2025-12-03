package project.spreadsheet.formula;

public class NumberLit implements Expr {
    private final double value;

    public NumberLit(double value) {
        this.value = value;
    }

    @Override
    public double eval(EvalContext ctx) {
        return value;
    }
}

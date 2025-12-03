package project.spreadsheet.formula;

/**
 * Composite node: binary operator like +, -, *, /.
 */
public class BinaryOp implements Expr {

    private final String op;
    private final Expr left;
    private final Expr right;

    public BinaryOp(String op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public double eval(EvalContext ctx) {
        double l = left.eval(ctx);
        double r = right.eval(ctx);

        return switch (op) {
            case "+" -> l + r;
            case "-" -> l - r;
            case "*" -> l * r;
            case "/" -> l / r;
            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    public String getOp() {
        return op;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
}

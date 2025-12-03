package project.spreadsheet.formula;

public interface Expr {
    double eval(EvalContext ctx);
}

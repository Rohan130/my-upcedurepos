package project.spreadsheet.formula;

import project.spreadsheet.sheet.Range;

/**
 * Leaf node: a range like A1:B3 used mainly by functions like SUM, MAX, MIN.
 */
public class RangeExpr implements Expr {

    private final Range range;

    public RangeExpr(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    @Override
    public double eval(EvalContext ctx) {
        throw new UnsupportedOperationException("Range cannot be evaluated to a single number directly. Use it inside functions.");
    }
}
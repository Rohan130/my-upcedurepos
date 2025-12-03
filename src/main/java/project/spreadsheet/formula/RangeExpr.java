package project.spreadsheet.formula;

import project.spreadsheet.sheet.Range;

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
        throw new UnsupportedOperationException("Range evaluation not supported yet");
    }
}

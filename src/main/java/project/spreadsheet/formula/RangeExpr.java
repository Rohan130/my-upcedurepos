package project.spreadsheet.formula;

import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Range;
import java.util.Set;

public class RangeExpr implements Expr {
    private Range range;
    public EvalResult eval(EvalContext ctx){ return null; }
    public Set<Address> referencedAddresses(){ return null; }
}

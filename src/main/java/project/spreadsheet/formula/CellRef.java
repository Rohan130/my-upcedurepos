package project.spreadsheet.formula;

import project.spreadsheet.sheet.Address;
import java.util.Set;

public class CellRef implements Expr {
    private Address address;
    public EvalResult eval(EvalContext ctx){ return null; }
    public Set<Address> referencedAddresses(){ return null; }
}

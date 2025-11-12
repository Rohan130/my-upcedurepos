package project.spreadsheet.formula;

import project.spreadsheet.sheet.Address;
import java.util.List;
import java.util.Set;

public class FuncCall implements Expr {
    private String name; // SUMA, MIN, MAX, PROMEDIO
    private List<Expr> args;

    public EvalResult eval(EvalContext ctx){ return null; }
    public Set<Address> referencedAddresses(){ return null; }
}

package project.spreadsheet.formula;

import project.spreadsheet.sheet.Address;
import java.util.Set;

public class BinaryOp implements Expr {
    public enum Op { ADD, SUB, MUL, DIV }
    private Op op;
    private Expr left, right;

    public EvalResult eval(EvalContext ctx){ return null; }
    public Set<Address> referencedAddresses(){ return null; }
}

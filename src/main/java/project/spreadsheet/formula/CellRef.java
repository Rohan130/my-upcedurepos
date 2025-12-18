package project.spreadsheet.formula;

import project.spreadsheet.sheet.Address;

/**
 * Leaf node: reference to a cell like A1, B2.
 */
public class CellRef implements Expr {

    private final Address address;

    public CellRef(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public double eval(EvalContext ctx) {
        return ctx.getCellValue(address);
    }
}
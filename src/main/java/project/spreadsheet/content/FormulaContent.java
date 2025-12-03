package project.spreadsheet.content;

import project.spreadsheet.formula.Expr;
import project.spreadsheet.formula.EvalContext;
import project.spreadsheet.parser.Parser;

public class FormulaContent implements Content {

    private final String raw;
    private final Expr ast;

    public FormulaContent(String raw) {
        this.raw = raw;
        this.ast = new Parser().parse(raw.substring(1)); // skip '='
    }

    @Override
    public String raw() {
        return raw;
    }

    public double eval(EvalContext ctx) {
        return ast.eval(ctx);
    }
}

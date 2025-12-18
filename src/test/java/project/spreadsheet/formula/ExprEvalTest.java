package project.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import project.spreadsheet.parser.Parser;
import project.spreadsheet.sheet.Spreadsheet;
import project.spreadsheet.sheet.Address;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprEvalTest {

    @Test
    void simpleAddition() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Expr expr = new BinaryOp("+",
                new NumberLit(1),
                new NumberLit(2));

        double result = expr.eval(ctx);
        assertEquals(3.0, result);
    }

    @Test
    void precedenceMultiplicationFirst() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        // 1 + 2 * 3  -> should be 7
        Expr expr = new BinaryOp("+",
                new NumberLit(1),
                new BinaryOp("*",
                        new NumberLit(2),
                        new NumberLit(3)));

        double result = expr.eval(ctx);
        assertEquals(7.0, result);
    }

    @Test
    void parenthesesChangeResult() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        // (1 + 2) * 3 -> should be 9
        Expr expr = new BinaryOp("*",
                new BinaryOp("+",
                        new NumberLit(1),
                        new NumberLit(2)),
                new NumberLit(3));

        double result = expr.eval(ctx);
        assertEquals(9.0, result);
    }

    @Test
    void parserEvaluatesSimpleExpression() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Parser p = new Parser();
        Expr expr = p.parse("1+2");

        double result = expr.eval(ctx);
        assertEquals(3.0, result);
    }

    @Test
    void parserRespectsPrecedence() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Parser p = new Parser();
        Expr expr = p.parse("1+2*3");  // → should be 7

        assertEquals(7.0, expr.eval(ctx));
    }

    @Test
    void parserRespectsParentheses() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Parser p = new Parser();
        Expr expr = p.parse("(1+2)*3");  // → should be 9

        assertEquals(9.0, expr.eval(ctx));
    }

    // ✅ STEP E — Functions (no ranges)
    @Test
    void maxFunctionWorks() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Parser p = new Parser();
        Expr expr = p.parse("MAX(1, 9, 3)");

        assertEquals(9.0, expr.eval(ctx));
    }

    @Test
    void minFunctionWorks() {
        Spreadsheet sheet = new Spreadsheet();
        EvalContext ctx = new EvalContext(sheet);

        Parser p = new Parser();
        Expr expr = p.parse("MIN(4, 2, 8)");

        assertEquals(2.0, expr.eval(ctx));
    }

    // ✅ STEP F — Ranges (with SUM)
    @Test
    void sumWithRangeWorks() {
        Spreadsheet sheet = new Spreadsheet();
        sheet.setRawContent(Address.parse("A1"), "1");
        sheet.setRawContent(Address.parse("A2"), "2");
        sheet.setRawContent(Address.parse("A3"), "3");

        EvalContext ctx = new EvalContext(sheet);
        Parser p = new Parser();

        Expr expr = p.parse("SUM(A1:A3)");
        assertEquals(6.0, expr.eval(ctx));
    }
}
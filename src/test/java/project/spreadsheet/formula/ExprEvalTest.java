package project.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import project.spreadsheet.parser.Parser;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprEvalTest {

    @Test
    void simpleAddition() {
        Expr expr = new BinaryOp("+",
                new NumberLit(1),
                new NumberLit(2));

        double result = expr.eval(new EvalContext());
        assertEquals(3.0, result);
    }

    @Test
    void precedenceMultiplicationFirst() {
        // 1 + 2 * 3  -> should be 7
        Expr expr = new BinaryOp("+",
                new NumberLit(1),
                new BinaryOp("*",
                        new NumberLit(2),
                        new NumberLit(3)));

        double result = expr.eval(new EvalContext());
        assertEquals(7.0, result);
    }

    @Test
    void parenthesesChangeResult() {
        // (1 + 2) * 3 -> should be 9
        Expr expr = new BinaryOp("*",
                new BinaryOp("+",
                        new NumberLit(1),
                        new NumberLit(2)),
                new NumberLit(3));

        double result = expr.eval(new EvalContext());
        assertEquals(9.0, result);
    }

    @Test
    void parserEvaluatesSimpleExpression() {
        Parser p = new Parser();
        Expr expr = p.parse("1+2");

        double result = expr.eval(new EvalContext());
        assertEquals(3.0, result);
    }

    @Test
    void parserRespectsPrecedence() {
        Parser p = new Parser();
        Expr expr = p.parse("1+2*3");  // → should be 7

        assertEquals(7.0, expr.eval(new EvalContext()));
    }

    @Test
    void parserRespectsParentheses() {
        Parser p = new Parser();
        Expr expr = p.parse("(1+2)*3");  // → should be 9

        assertEquals(9.0, expr.eval(new EvalContext()));
    }
}
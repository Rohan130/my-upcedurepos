package project.spreadsheet.parser;

import project.spreadsheet.formula.BinaryOp;
import project.spreadsheet.formula.Expr;
import project.spreadsheet.formula.NumberLit;

public class Parser {

    private String input;
    private int pos;

    public Expr parse(String text) {
        this.input = text.replaceAll("\\s+", ""); // remove spaces
        this.pos = 0;
        Expr expr = parseExpr();
        if (pos != input.length()) {
            throw new IllegalArgumentException("Unexpected trailing characters: " + input.substring(pos));
        }
        return expr;
    }

    // expr := term (('+' | '-') term)*
    private Expr parseExpr() {
        Expr left = parseTerm();
        while (true) {
            if (match('+')) {
                Expr right = parseTerm();
                left = new BinaryOp("+", left, right);
            } else if (match('-')) {
                Expr right = parseTerm();
                left = new BinaryOp("-", left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // term := factor (('*' | '/') factor)*
    private Expr parseTerm() {
        Expr left = parseFactor();
        while (true) {
            if (match('*')) {
                Expr right = parseFactor();
                left = new BinaryOp("*", left, right);
            } else if (match('/')) {
                Expr right = parseFactor();
                left = new BinaryOp("/", left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // factor := NUMBER | '(' expr ')'
    private Expr parseFactor() {
        if (match('(')) {
            Expr inside = parseExpr();
            if (!match(')')) {
                throw new IllegalArgumentException("Missing closing parenthesis at position " + pos);
            }
            return inside;
        }

        // parse number
        int start = pos;
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected number at position " + pos + " in '" + input + "'");
        }
        double value = Double.parseDouble(input.substring(start, pos));
        return new NumberLit(value);
    }

    private boolean match(char c) {
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }
}

package project.spreadsheet.parser;

import project.spreadsheet.formula.*;
import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Range;

import java.util.*;

/**
 * Shunting Yard parser:
 * - numbers, + - * /, parentheses
 * - cell refs: A1, B12, AA3
 * - functions: NAME(arg1, arg2, ...)
 * - ranges: A1:B3 (as RangeExpr)
 */
public class Parser {

    public Expr parse(String text) {
        List<Token> tokens = tokenize(text);
        List<Token> rpn = toRpn(tokens);
        return rpnToAst(rpn);
    }

    // ---------------- Tokenization ----------------

    private List<Token> tokenize(String s) {
        String input = s.replaceAll("\\s+", "");
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // number
            if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < input.length()) {
                    char ch = input.charAt(i);
                    if (Character.isDigit(ch) || ch == '.') i++;
                    else break;
                }
                tokens.add(Token.number(input.substring(start, i)));
                continue;
            }

            // identifier / cell ref
            if (Character.isLetter(c)) {
                int start = i;
                while (i < input.length() && Character.isLetter(input.charAt(i))) i++;
                String letters = input.substring(start, i).toUpperCase();

                // digits after letters => cell ref
                int digitStart = i;
                while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                if (digitStart != i) {
                    String digits = input.substring(digitStart, i);
                    String cell1 = letters + digits;

                    // range?
                    if (i < input.length() && input.charAt(i) == ':') {
                        i++; // skip ':'

                        int start2 = i;
                        while (i < input.length() && Character.isLetter(input.charAt(i))) i++;
                        String letters2 = input.substring(start2, i).toUpperCase();

                        int digitStart2 = i;
                        while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                        if (digitStart2 == i) {
                            throw new IllegalArgumentException("Invalid range end near: " + input.substring(start2));
                        }
                        String cell2 = letters2 + input.substring(digitStart2, i);

                        tokens.add(Token.range(cell1 + ":" + cell2));
                    } else {
                        tokens.add(Token.cell(cell1));
                    }
                    continue;
                }

                // otherwise => function name (must be followed by '(')
                tokens.add(Token.ident(letters));
                continue;
            }

            // operators / punctuation
            switch (c) {
                case '+', '-', '*', '/' -> { tokens.add(Token.op(String.valueOf(c))); i++; }
                case '(' -> { tokens.add(Token.lparen()); i++; }
                case ')' -> { tokens.add(Token.rparen()); i++; }
                case ',' -> { tokens.add(Token.comma()); i++; }
                default -> throw new IllegalArgumentException("Unexpected character: " + c);
            }
        }

        return tokens;
    }

    // ---------------- Shunting Yard (infix -> RPN) ----------------

    private List<Token> toRpn(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        Deque<Token> ops = new ArrayDeque<>();
        Deque<Integer> argCount = new ArrayDeque<>();

        Token prev = null;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            switch (t.type) {
                case NUMBER, CELL, RANGE -> out.add(t);

                case IDENT -> {
                    boolean isFunc = (i + 1 < tokens.size() && tokens.get(i + 1).type == TokenType.LPAREN);
                    if (!isFunc) throw new IllegalArgumentException("Unknown identifier: " + t.text);
                    ops.push(t.asFunction());
                }

                case COMMA -> {
                    while (!ops.isEmpty() && ops.peek().type != TokenType.LPAREN) {
                        out.add(ops.pop());
                    }
                    if (argCount.isEmpty()) throw new IllegalArgumentException("Comma outside function call");
                    argCount.push(argCount.pop() + 1);
                }

                case OP -> {
                    while (!ops.isEmpty() && ops.peek().type == TokenType.OP
                            && precedence(ops.peek().text) >= precedence(t.text)) {
                        out.add(ops.pop());
                    }
                    ops.push(t);
                }

                case LPAREN -> {
                    if (prev != null && prev.type == TokenType.FUNCTION) {
                        argCount.push(0);
                    }
                    ops.push(t);
                }

                case RPAREN -> {
                    while (!ops.isEmpty() && ops.peek().type != TokenType.LPAREN) {
                        out.add(ops.pop());
                    }
                    if (ops.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                    ops.pop(); // pop '('

                    if (!ops.isEmpty() && ops.peek().type == TokenType.FUNCTION) {
                        Token fn = ops.pop();
                        int commas = argCount.isEmpty() ? 0 : argCount.pop();
                        int finalArgs = (prev != null && prev.type == TokenType.LPAREN) ? 0 : (commas + 1);
                        out.add(fn.withArity(finalArgs));
                    }
                }

                default -> throw new IllegalStateException("Unhandled token: " + t);
            }

            prev = t.type == TokenType.IDENT ? t.asFunction() : t;
        }

        while (!ops.isEmpty()) {
            Token t = ops.pop();
            if (t.type == TokenType.LPAREN || t.type == TokenType.RPAREN) throw new IllegalArgumentException("Mismatched parentheses");
            out.add(t);
        }

        return out;
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }

    // ---------------- RPN -> AST ----------------

    private Expr rpnToAst(List<Token> rpn) {
        Deque<Expr> st = new ArrayDeque<>();

        for (Token t : rpn) {
            switch (t.type) {
                case NUMBER -> st.push(new NumberLit(Double.parseDouble(t.text)));

                case CELL -> st.push(new CellRef(Address.parse(t.text)));

                case RANGE -> st.push(new RangeExpr(Range.parse(t.text)));

                case OP -> {
                    Expr right = st.pop();
                    Expr left = st.pop();
                    st.push(new BinaryOp(t.text, left, right));
                }

                case FUNCTION -> {
                    int n = t.arity;
                    List<Expr> args = new ArrayList<>();
                    for (int i = 0; i < n; i++) args.add(st.pop());
                    Collections.reverse(args);
                    st.push(new FuncCall(t.text, args));
                }

                default -> throw new IllegalArgumentException("Bad RPN token: " + t.type);
            }
        }

        if (st.size() != 1) throw new IllegalArgumentException("Invalid expression");
        return st.pop();
    }

    // ---------------- Token model ----------------

    private enum TokenType { NUMBER, CELL, RANGE, IDENT, FUNCTION, OP, LPAREN, RPAREN, COMMA }

    private static class Token {
        final TokenType type;
        final String text;
        final int arity;

        Token(TokenType type, String text) { this(type, text, -1); }
        Token(TokenType type, String text, int arity) {
            this.type = type;
            this.text = text;
            this.arity = arity;
        }

        static Token number(String t) { return new Token(TokenType.NUMBER, t); }
        static Token cell(String t) { return new Token(TokenType.CELL, t); }
        static Token range(String t) { return new Token(TokenType.RANGE, t); }
        static Token ident(String t) { return new Token(TokenType.IDENT, t); }
        static Token op(String t) { return new Token(TokenType.OP, t); }
        static Token lparen() { return new Token(TokenType.LPAREN, "("); }
        static Token rparen() { return new Token(TokenType.RPAREN, ")"); }
        static Token comma() { return new Token(TokenType.COMMA, ","); }

        Token asFunction() { return new Token(TokenType.FUNCTION, this.text); }
        Token withArity(int a) { return new Token(TokenType.FUNCTION, this.text, a); }
    }
}
package project.spreadsheet.formula;

import project.spreadsheet.parser.Parser;
import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Range;
import project.spreadsheet.sheet.Spreadsheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluation context: provides access to spreadsheet values, function evaluation,
 * and circular reference (cycle) detection.
 */
public class EvalContext {

    private final Spreadsheet sheet;
    private final Parser parser;

    // cycle detection: addresses currently being evaluated
    private final Set<Address> visiting = new HashSet<>();

    public EvalContext(Spreadsheet sheet) {
        this.sheet = sheet;
        this.parser = new Parser();
    }

    /**
     * Get numeric value of a cell. Rules:
     * - empty -> 0
     * - number -> parsed
     * - formula -> parsed & evaluated recursively
     * - text -> 0  (to avoid errors as teacher suggested)
     * - cycles -> throw error
     */
    public double getCellValue(Address addr) {
        if (visiting.contains(addr)) {
            throw new IllegalStateException("Circular reference detected at " + addr);
        }

        visiting.add(addr);
        try {
            String raw = sheet.getRawContent(addr);

            if (raw == null || raw.isBlank()) return 0.0;

            if (raw.startsWith("=")) {
                Expr expr = parser.parse(raw.substring(1));
                return expr.eval(this);
            }

            if (raw.matches("-?\\d+(\\.\\d+)?")) {
                return Double.parseDouble(raw);
            }

            // text or unknown -> 0 by policy
            return 0.0;

        } finally {
            visiting.remove(addr);
        }
    }

    /**
     * Apply functions (Step 8, Step 9).
     * Supports: SUM, AVG/AVERAGE, MIN, MAX, SIN, COS.
     * Ranges are allowed as arguments and are expanded into cell values.
     */
    public double apply(String name, List<Expr> args) {
        String fn = name.toUpperCase();

        // flatten arguments: if RangeExpr -> expand into many numbers
        List<Double> values = new ArrayList<>();
        for (Expr arg : args) {
            if (arg instanceof RangeExpr re) {
                values.addAll(expandRange(re.getRange()));
            } else {
                values.add(arg.eval(this));
            }
        }

        return switch (fn) {
            case "SUM" -> {
                double sum = 0.0;
                for (double v : values) sum += v;
                yield sum;
            }
            case "AVG", "AVERAGE" -> {
                if (values.isEmpty()) yield 0.0;
                double sum = 0.0;
                for (double v : values) sum += v;
                yield sum / values.size();
            }
            case "MIN" -> {
                if (values.isEmpty()) yield 0.0;
                double m = values.get(0);
                for (int i = 1; i < values.size(); i++) m = Math.min(m, values.get(i));
                yield m;
            }
            case "MAX" -> {
                if (values.isEmpty()) yield 0.0;
                double m = values.get(0);
                for (int i = 1; i < values.size(); i++) m = Math.max(m, values.get(i));
                yield m;
            }
            case "SIN" -> values.isEmpty() ? 0.0 : Math.sin(values.get(0));
            case "COS" -> values.isEmpty() ? 0.0 : Math.cos(values.get(0));
            default -> throw new IllegalArgumentException("Unknown function: " + name);
        };
    }

    private List<Double> expandRange(Range range) {
        List<Double> out = new ArrayList<>();
        Address s = range.getStart();
        Address e = range.getEnd();

        for (int row = s.getRow(); row <= e.getRow(); row++) {
            for (int col = s.getColumn(); col <= e.getColumn(); col++) {
                out.add(getCellValue(new Address(col, row)));
            }
        }
        return out;
    }
}

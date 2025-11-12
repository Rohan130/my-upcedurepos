package project.spreadsheet.content;

public final class ContentParser {

    private ContentParser() {}

    public static Content parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            return new TextContent("");
        }
        if (raw.startsWith("=")) {     // formula (no evaluation yet)
            return new FormulaContent(raw);
        }
        try {
            double n = Double.parseDouble(raw);
            return new NumericContent(n);
        } catch (NumberFormatException e) {
            return new TextContent(raw);
        }
    }
}

package project.spreadsheet.content;

public class FormulaContent implements Content {
    private final String raw;

    public FormulaContent(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public String raw() {
        return raw;
    }
}

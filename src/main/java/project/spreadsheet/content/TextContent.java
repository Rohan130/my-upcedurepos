package project.spreadsheet.content;

public class TextContent implements Content {
    private final String text;

    public TextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String raw() {
        return text;
    }
}
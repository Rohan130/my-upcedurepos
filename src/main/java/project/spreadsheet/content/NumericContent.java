package project.spreadsheet.content;

public class NumericContent implements Content {
    private final double number;

    public NumericContent(double number) {
        this.number = number;
    }

    public double getNumber() {
        return number;
    }

    @Override
    public String raw() {
        return Double.toString(number);
    }
}

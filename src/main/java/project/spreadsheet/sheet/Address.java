package project.spreadsheet.sheet;

import java.util.Objects;

public final class Address {

    private final int column;
    private final int row;

    public Address(int column, int row) {
        if (column < 1 || row < 1) {
            throw new IllegalArgumentException("column and row must be >= 1");
        }
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {

        return row;
    }

    public static Address parse(String ref) {
        String trimmed = ref.trim();
        String letters = trimmed.replaceAll("[^A-Za-z]", "").toUpperCase();
        String digits  = trimmed.replaceAll("[^0-9]", "");
        if (letters.isEmpty() || digits.isEmpty()) {
            throw new IllegalArgumentException("Invalid address: " + ref);
        }
        int col = 0;
        for (char c : letters.toCharArray()) {
            col = col * 26 + (c - 'A' + 1);
        }
        int row = Integer.parseInt(digits);
        return new Address(col, row);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = column;

        while (n > 0) {
            int r = (n - 1) % 26;
            sb.append((char) ('A' + r));
            n = (n - 1) / 26;
        }

        return sb.reverse().toString() + row;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return column == address.column && row == address.row;
    }

    @Override
    public int hashCode() {

        return Objects.hash(column, row);
    }
}

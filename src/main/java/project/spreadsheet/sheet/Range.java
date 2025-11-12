package project.spreadsheet.sheet;

public class Range {

    private final Address start;
    private final Address end;

    public Range(Address start, Address end) {
        this.start = start;
        this.end = end;
    }

    public Address getStart() { return start; }
    public Address getEnd()   { return end; }

    public static Range parse(String spec) {
        String[] parts = spec.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range: " + spec);
        }
        Address a = Address.parse(parts[0]);
        Address b = Address.parse(parts[1]);
        return new Range(a, b);
    }
}

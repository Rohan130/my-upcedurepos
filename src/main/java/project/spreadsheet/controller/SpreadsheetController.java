package project.spreadsheet.controller;

import project.spreadsheet.formula.EvalContext;
import project.spreadsheet.io.S2VReader;
import project.spreadsheet.io.S2VWriter;
import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Range;
import project.spreadsheet.sheet.Spreadsheet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpreadsheetController {

    private final Spreadsheet sheet;
    private final S2VReader reader;
    private final S2VWriter writer;

    public SpreadsheetController(Spreadsheet sheet, S2VReader reader, S2VWriter writer) {
        this.sheet = sheet;
        this.reader = reader;
        this.writer = writer;
    }

    // --- use cases ---

    /** Store raw content in a cell (text, number, or formula starting with '=') */
    public void setCellContent(String addressRef, String raw) {
        sheet.setRawContent(Address.parse(addressRef), raw);
    }

    /** Get raw content exactly as stored (not evaluated). */
    public String getCellContent(String addressRef) {
        return sheet.getRawContent(Address.parse(addressRef));
    }


    public double getCellValue(String addressRef) {
        EvalContext ctx = new EvalContext(sheet);
        return ctx.getCellValue(Address.parse(addressRef));
    }

    /** Load spreadsheet from file (S2V). */
    public void load(Path file) throws IOException {
        Spreadsheet loaded = reader.read(file);
        loaded.getUsedAddresses().forEach(addr ->
                sheet.setRawContent(addr, loaded.getRawContent(addr)));
    }

    /** Save spreadsheet to file (S2V). */
    public void save(Path file) throws IOException {
        writer.write(sheet, file);
    }

    /** Optional helper for showing raw content in a range (not evaluated). */
    public List<List<String>> showRange(String rangeSpec) {
        Range r = Range.parse(rangeSpec);
        List<List<String>> rows = new ArrayList<>();
        for (int row = r.getStart().getRow(); row <= r.getEnd().getRow(); row++) {
            List<String> line = new ArrayList<>();
            for (int col = r.getStart().getColumn(); col <= r.getEnd().getColumn(); col++) {
                line.add(sheet.getRawContent(new Address(col, row)));
            }
            rows.add(line);
        }
        return rows;
    }


    public void printRangeValues(String rangeSpec) {
        Range r = Range.parse(rangeSpec);
        EvalContext ctx = new EvalContext(sheet);

        // Print column headers
        System.out.print("      ");
        for (int col = r.getStart().getColumn(); col <= r.getEnd().getColumn(); col++) {
            String colName = new Address(col, 1).toString().replaceAll("\\d+", "");
            System.out.printf("%10s", colName);
        }
        System.out.println();

        // Print each row with evaluated values
        for (int row = r.getStart().getRow(); row <= r.getEnd().getRow(); row++) {
            System.out.printf("%5d ", row);
            for (int col = r.getStart().getColumn(); col <= r.getEnd().getColumn(); col++) {
                double v = ctx.getCellValue(new Address(col, row));
                System.out.printf("%10.2f", v);
            }
            System.out.println();
        }
    }
}
package project.spreadsheet.controller;

import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Range;
import project.spreadsheet.sheet.Spreadsheet;
import project.spreadsheet.io.S2VReader;
import project.spreadsheet.io.S2VWriter;

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

    public void setCellContent(String addressRef, String raw) {
        sheet.setRawContent(Address.parse(addressRef), raw);
    }

    public String getCellContent(String addressRef) {
        return sheet.getRawContent(Address.parse(addressRef));
    }

    public void load(Path file) throws IOException {
        Spreadsheet loaded = reader.read(file);
        loaded.getUsedAddresses().forEach(addr ->
                sheet.setRawContent(addr, loaded.getRawContent(addr)));
    }

    public void save(Path file) throws IOException {
        writer.write(sheet, file);
    }

    // Optional helper for Show / Print Range
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
}

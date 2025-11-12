package project.spreadsheet.io;

import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Spreadsheet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class S2VWriter {

    public void write(Spreadsheet sheet, Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            write(sheet, bw);
        }
    }

    public void write(Spreadsheet sheet, Writer out) throws IOException {
        int maxRow = 0, maxCol = 0;
        for (Address addr : sheet.getUsedAddresses()) {
            maxRow = Math.max(maxRow, addr.getRow());
            maxCol = Math.max(maxCol, addr.getColumn());
        }

        for (int r = 1; r <= maxRow; r++) {
            List<String> row = new ArrayList<>();
            for (int c = 1; c <= maxCol; c++) {
                Address addr = new Address(c, r);
                String raw = sheet.getRawContent(addr);
                if (raw.startsWith("=")) {
                    raw = encodeFormulaForFile(raw);
                }
                row.add(raw);
            }
            int last = row.size() - 1;
            while (last >= 0 && row.get(last).isEmpty()) last--;
            String line = String.join(";", row.subList(0, last + 1));
            out.write(line);
            out.write(System.lineSeparator());
        }
    }

    private static String encodeFormulaForFile(String raw) {
        return "=" + raw.substring(1).replace(';', ',');
    }
}

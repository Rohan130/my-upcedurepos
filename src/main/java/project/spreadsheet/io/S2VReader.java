package project.spreadsheet.io;

import project.spreadsheet.sheet.Address;
import project.spreadsheet.sheet.Spreadsheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class S2VReader {

    public Spreadsheet read(Path file) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(file)) {
            return read(br);
        }
    }

    public Spreadsheet read(Reader in) throws IOException {
        Spreadsheet sheet = new Spreadsheet();
        BufferedReader br = (in instanceof BufferedReader) ? (BufferedReader) in : new BufferedReader(in);

        String line;
        int row = 1;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) { row++; continue; }
            String[] cells = line.split(";", -1); // keep empty tail
            for (int c = 0; c < cells.length; c++) {
                String raw = cells[c];
                if (raw.isEmpty()) continue;
                if (raw.startsWith("=")) {
                    raw = decodeFormulaFromFile(raw);
                }
                sheet.setRawContent(new Address(c + 1, row), raw);
            }
            row++;
        }
        return sheet;
    }

    private static String decodeFormulaFromFile(String raw) {
        return "=" + raw.substring(1).replace(',', ';');
    }
}

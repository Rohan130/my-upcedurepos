package project.spreadsheet;

import project.spreadsheet.controller.SpreadsheetController;
import project.spreadsheet.io.S2VReader;
import project.spreadsheet.io.S2VWriter;
import project.spreadsheet.sheet.Spreadsheet;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {

        // Create spreadsheet + controller
        Spreadsheet sheet = new Spreadsheet();
        SpreadsheetController ctrl =
                new SpreadsheetController(sheet, new S2VReader(), new S2VWriter());

        // 1) Set some cell contents
        ctrl.setCellContent("A1", "Hello");
        ctrl.setCellContent("B1", "123");
        ctrl.setCellContent("C1", "=A1+B1");  // stored, not evaluated

        // 2) Save to file
        Path file = Path.of("test.s2v");
        ctrl.save(file);
        System.out.println("Saved spreadsheet to: " + file.toAbsolutePath());

        // 3) Create a new spreadsheet & load the file
        Spreadsheet sheet2 = new Spreadsheet();
        SpreadsheetController ctrl2 =
                new SpreadsheetController(sheet2, new S2VReader(), new S2VWriter());

        ctrl2.load(file);

        // 4) Verify loaded content
        System.out.println("Loaded A1: " + ctrl2.getCellContent("A1")); // Hello
        System.out.println("Loaded B1: " + ctrl2.getCellContent("B1")); // 123
        System.out.println("Loaded C1: " + ctrl2.getCellContent("C1")); // =A1+B1
    }
}

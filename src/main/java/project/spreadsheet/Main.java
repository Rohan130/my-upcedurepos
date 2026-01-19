package project.spreadsheet;

import project.spreadsheet.controller.SpreadsheetController;
import project.spreadsheet.formula.EvalContext;
import project.spreadsheet.formula.Expr;
import project.spreadsheet.io.S2VReader;
import project.spreadsheet.io.S2VWriter;
import project.spreadsheet.parser.Parser;
import project.spreadsheet.sheet.Spreadsheet;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Spreadsheet sheet = new Spreadsheet();
        SpreadsheetController ctrl =
                new SpreadsheetController(sheet, new S2VReader(), new S2VWriter());

        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();

        // EvalContext needs the spreadsheet for cell references, ranges, etc.
        EvalContext ctx = new EvalContext(sheet);

        System.out.println("=== Spreadsheet Console ===");

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Set cell content");
            System.out.println("2) View cell content (raw)");
            System.out.println("3) Save spreadsheet to S2V");
            System.out.println("4) Load spreadsheet from S2V");
            System.out.println("5) Evaluate formula (e.g. 1+2*3 or A1+2)");
            System.out.println("6) View cell value (evaluated)");
            System.out.println("7) Show range values as table (evaluated)");
            System.out.println("0) Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1" -> {
                        System.out.print("Enter cell coordinate (e.g., A1): ");
                        String coord = scanner.nextLine().trim().toUpperCase();

                        System.out.print("Enter cell content: ");
                        String content = scanner.nextLine();

                        ctrl.setCellContent(coord, content);
                        System.out.println("Cell " + coord + " updated.");
                    }

                    case "2" -> {
                        System.out.print("Enter cell coordinate (e.g., A1): ");
                        String coord = scanner.nextLine().trim().toUpperCase();

                        String raw = ctrl.getCellContent(coord);
                        System.out.println("Cell " + coord + " content: " + raw);
                    }

                    case "3" -> {
                        System.out.print("Enter path to save S2V file (e.g., sheet.s2v): ");
                        String fileName = scanner.nextLine().trim();
                        Path path = Path.of(fileName);

                        ctrl.save(path);
                        System.out.println("Spreadsheet saved to: " + path.toAbsolutePath());
                    }

                    case "4" -> {
                        System.out.print("Enter path of S2V file to load: ");
                        String fileName = scanner.nextLine().trim();
                        Path path = Path.of(fileName);

                        ctrl.load(path);
                        System.out.println("Spreadsheet loaded from: " + path.toAbsolutePath());
                    }

                    case "5" -> {
                        System.out.print("Enter formula (e.g., 1+2*3 or A1+2): ");
                        String formula = scanner.nextLine().trim();

                        try {
                            Expr expr = parser.parse(formula);
                            double value = expr.eval(ctx);
                            System.out.println(formula + " = " + value);
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Error parsing formula: " + ex.getMessage());
                        }
                    }

                    case "6" -> {
                        System.out.print("Enter cell coordinate (e.g., A1): ");
                        String coord = scanner.nextLine().trim().toUpperCase();

                        double value = ctrl.getCellValue(coord);
                        System.out.println("Cell " + coord + " value: " + value);
                    }

                    case "7" -> {
                        System.out.print("Enter range (e.g., A1:C3): ");
                        String range = scanner.nextLine().trim().toUpperCase();

                        ctrl.printRangeValues(range);
                    }

                    case "0" -> {
                        running = false;
                        System.out.println("Exiting...");
                    }

                    default -> System.out.println("Unknown option. Please try again.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }

        scanner.close();
    }
}
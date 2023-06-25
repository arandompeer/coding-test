import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class Cell {
    private String value;
    private boolean isNumber;

    public Cell(String value, boolean isNumber) {
        this.value = value;
        this.isNumber = isNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public void setNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }
}

class Spreadsheet {
    private List<List<Cell>> grid;

    public Spreadsheet(List<List<Cell>> grid) {
        this.grid = grid;
    }

    private String horizontalLine(int width) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < width; i++) {
            line.append("-");
        }
        return line.toString();
    }

    private String formatCell(String value, int width, boolean isNumber) {
        StringBuilder formattedValue = new StringBuilder();

        if (isNumber) {
            formattedValue.append(String.format("%" + width + "s", value));
        } else {
            formattedValue.append(String.format("%-" + width + "s", value));
        }

        return formattedValue.toString();
    }

    public void evaluate() {
        for (List<Cell> row : grid) {
            for (Cell cell : row) {
                if (!cell.isNumber())
                    evaluateCell(cell);
            }
        }
    }

    private void evaluateCell(Cell cell) {
        String value = cell.getValue();
        if (value.equals("#hl")) {
            // ... existing horizontal line logic
        } else if (value.startsWith("#(sum")) {
            String[] cellRefs = value.substring(6, value.length() - 1).split(" ");
            double sum = 0.0;

            for (String cellRef : cellRefs) {
                int[] coordinates = parseCoordinates(cellRef);
                if (coordinates != null) {
                    Cell referencedCell = grid.get(coordinates[0]).get(coordinates[1]);
                    if (referencedCell.isNumber()) {
                        sum += Double.parseDouble(referencedCell.getValue());
                    }
                }
            }

            cell.setValue(String.valueOf(sum));
            cell.setNumber(true);
        } else if (value.startsWith("#(prod")) {
            String[] cellRefs = value.substring(7, value.length() - 1).split(" ");
            double product = 1.0;

            for (String cellRef : cellRefs) {
                int[] coordinates = parseCoordinates(cellRef);
                if (coordinates != null) {
                    Cell referencedCell = grid.get(coordinates[0]).get(coordinates[1]);
                    if (referencedCell.isNumber()) {
                        product *= Double.parseDouble(referencedCell.getValue());
                    } else {
                        product = 0.0; // Set the product to 0 if any referenced cell is not a number
                        break;
                    }
                }
            }

            cell.setValue(String.valueOf(product));
            cell.setNumber(true);
        }
    }

    private int[] parseCoordinates(String cellRef) {
        int column = cellRef.charAt(0) - 'A';
        int row = Integer.parseInt(cellRef.substring(1)) - 1;

        if (row >= 0 && row < grid.size() && column >= 0 && column < grid.get(row).size()) {
            return new int[]{row, column};
        } else {
            return null;
        }
    }

    public void print(String outputFileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            int[] columnWidths = calculateColumnWidths();

            for (List<Cell> row : grid) {
                for (int i = 0; i < row.size(); i++) {
                    Cell cell = row.get(i);
                    String value = cell.getValue();
                    int width = columnWidths[i];

                    if (cell.isNumber()) {
                        writer.printf("%" + width + "s", value);
                    } else if (value.equals("#hl")) {
                        writer.print(horizontalLine(width));
                    } else {
                        writer.print(formatCell(value, width, cell.isNumber()));
                    }

                    if (i < row.size() - 1) {
                        writer.print(" | ");
                    }
                }
                writer.println();
            }
        }
    }

    private int[] calculateColumnWidths() {
        int numColumns = grid.get(0).size();
        int[] columnWidths = new int[numColumns];

        for (List<Cell> row : grid) {
            for (int i = 0; i < numColumns; i++) {
                if (i >= row.size()) {
                    continue;
                }
                Cell cell = row.get(i);
                int width = cell.getValue().length();
                if (width > columnWidths[i]) {
                    columnWidths[i] = width;
                }
            }
        }

        return columnWidths;
    }
}

public class SpreadsheetApplication {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java SpreadsheetApplication <input_file> <output_file>");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        try {
            List<List<Cell>> grid = readSpreadsheet(inputFileName);
            Spreadsheet spreadsheet = new Spreadsheet(grid);
            spreadsheet.evaluate();
            spreadsheet.print(outputFileName);
            System.out.println("Spreadsheet successfully generated!");
        } catch (IOException e) {
            System.out.println("An error occurred while processing the spreadsheet: " + e.getMessage());
        }
    }

    private static List<List<Cell>> readSpreadsheet(String inputFileName) throws IOException {
        List<List<Cell>> grid = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<Cell> row = new ArrayList<>();
                String[] cells = line.split(",");
                for (String cell : cells) {
                    boolean isNumber;
                    try {
                        Double.parseDouble(cell);
                        isNumber = true;
                    } catch (NumberFormatException e) {
                        isNumber = false;
                    }
                    row.add(new Cell(cell, isNumber));
                }
                grid.add(row);
            }
        }

        adjustColumns(grid); // Adjust the number of columns in each row

        return grid;
    }

    private static void adjustColumns(List<List<Cell>> grid) {
        int maxColumns = 0;
        for (List<Cell> row : grid) {
            maxColumns = Math.max(maxColumns, row.size());
        }

        for (List<Cell> row : grid) {
            int diff = maxColumns - row.size();
            for (int i = 0; i < diff; i++) {
                row.add(new Cell("", false));
            }
        }
    }
}

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpreadsheetApplicationTest {

    @Test
    public void testCell() {
        Cell cell = new Cell("123", true);
        assertEquals("123", cell.getValue());
        assertTrue(cell.isNumber());

        cell.setValue("456");
        assertEquals("456", cell.getValue());

        cell.setNumber(false);
        assertFalse(cell.isNumber());
    }

    @Test
    public void testSpreadsheetSum() {
        Cell cell1 = new Cell("1", true);
        Cell cell2 = new Cell("2", true);
        Cell cell3 = new Cell("#(sum A1 B1)", false);
        List<Cell> row = new ArrayList<>(Arrays.asList(cell1, cell2, cell3));
        Spreadsheet spreadsheet = new Spreadsheet(new ArrayList<>(Arrays.asList(row)));

        spreadsheet.evaluate();

        assertTrue(cell3.isNumber());
        assertEquals("3.0", cell3.getValue());
    }

    @Test
    public void testSpreadsheetProduct() {
        Cell cell1 = new Cell("2", true);
        Cell cell2 = new Cell("3", true);
        Cell cell3 = new Cell("#(prod A1 B1)", false);
        List<Cell> row = new ArrayList<>(Arrays.asList(cell1, cell2, cell3));
        Spreadsheet spreadsheet = new Spreadsheet(new ArrayList<>(Arrays.asList(row)));

        spreadsheet.evaluate();

        assertTrue(cell3.isNumber());
        assertEquals("6.0", cell3.getValue());
    }


}
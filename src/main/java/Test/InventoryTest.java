package Test;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import static Test.TestConfig.*;
import java.io.IOException;
import java.sql.Date;

import static org.junit.Assert.assertEquals;

public class InventoryTest {
    private static InventoryDeliveryNoteTest deliveryNoteTest = new InventoryDeliveryNoteTest();
    private static InventoryReceivingNoteTest receivingNoteTest = new InventoryReceivingNoteTest();

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        deliveryNoteTest.deleteAllNotes();
        receivingNoteTest.deleteAllNotes();
    }

    @Test
    public void testGetProductInventoryInAPeriod() throws IOException {
        createAndReturnReceivingNoteJs();
        String staff = createAndReturnStaffJs();
        String product = "{\"id\": " + products.get(products.size() - 1).getId() + "}";
        String details = createAndReturnDetailJs(product, 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");

        String date = new Date(System.currentTimeMillis()).toString();
        String expectedResult = "";
        String line = new String(new char[72]).replace('\0', '-');
        expectedResult += String.format(" Date  %s  %s", date , date);
        expectedResult += line;
        expectedResult += String.format("|%s|%s|%s|%s|",
                StringUtils.center("Name", 22),
                StringUtils.center("Received", 15),
                StringUtils.center("Delivery", 15),
                StringUtils.center("Balance", 15));
        expectedResult += line;
        expectedResult += String.format("|%s|%s|%s|%s|",
        StringUtils.center(products.get(products.size() - 1).getName(), 22),
        StringUtils.center("1", 15),
        StringUtils.center("1", 15),
        StringUtils.center( "0", 15));

        assertEquals(expectedResult, setUpGetConnection("/inventory/read/period?start=" + date + "&end=" + date));
    }
}

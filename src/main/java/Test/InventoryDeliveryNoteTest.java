package Test;

import model.InventoryDeliveryNote;
import org.junit.Test;
import static Test.TestConfig.*;
import java.io.IOException;
import java.sql.Date;
import static org.junit.Assert.assertEquals;

public class InventoryDeliveryNoteTest {
    @Test
    public void testGetAllNotesWithoutPaging() throws IOException {
        deleteAllNotes();
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");
        assertEquals(0, deliveryNotes.size());

        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        assertEquals(6, deliveryNotes.size());
    }

    @Test
    public void testGetNotesWithMoreThanOnePage() throws IOException {
        deleteAllNotes();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=1");
        assertEquals(5, deliveryNotes.size());
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=2");
        assertEquals(1, deliveryNotes.size());
    }

    @Test
    public void testGetNoteById() throws IOException {
        createAndReturnDeliveryNoteJs();
        getActualOutputGetMethod("inventoryDeliveryNotes/read/id/?id=" + deliveryNotes.get(deliveryNotes.size() - 1).getId());
        assertEquals(1, deliveryNotes.size());
        assertEquals(staffs.get(staffs.size() - 1).getId(), deliveryNotes.get(deliveryNotes.size() - 1).getStaff().getId());
    }

    @Test
    public void testGetNotesByDate() throws IOException {
        deleteAllNotes();
        String staff = createAndReturnStaffJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");

        getActualOutputGetMethod("inventoryDeliveryNotes/read/date?date=1999-01-01&page=-1");
        assertEquals(2, deliveryNotes.size());
        assertEquals("1999-01-01", deliveryNotes.get(0).getDate().toString());
        getActualOutputGetMethod("inventoryDeliveryNotes/read/date?date=1000-01-01&page=-1");
        assertEquals(0, deliveryNotes.size());
    }

    @Test
    public void testGetNotesByPeriod() throws IOException {
        deleteAllNotes();
        String staff = createAndReturnStaffJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"date\": \"1999-03-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"date\": \"1999-04-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");

        getActualOutputGetMethod("inventoryDeliveryNotes/read/period?start=1999-01-01&page=-1&end=1999-03-01");
        assertEquals(2, deliveryNotes.size());
        assertEquals("1999-01-01", deliveryNotes.get(0).getDate().toString());
        assertEquals("1999-03-01", deliveryNotes.get(1).getDate().toString());
        getActualOutputGetMethod("inventoryDeliveryNotes/read/period?start=1999-03-01&page=-1&end=1999-01-01");
        assertEquals(0, deliveryNotes.size());
    }
    
    @Test
    public void testNewNoteWithoutStaff() throws IOException {
        assertEquals("Failed to create inventory delivery note to have non-existent staff or without staff"
                , getActualOutputPostMethod("inventoryDeliveryNotes/create", "{\"date\": \"2222-02-02\"}"));
    }

    @Test
    public void testNewNoteWithoutDetails() throws IOException {
        String staff = createAndReturnStaffJs();
        assertEquals("Failed to create inventory delivery note without any details"
                , getActualOutputPostMethod("inventoryDeliveryNotes/create",
                        "{\"staff\": " + staff + "}\""));
    }

    @Test
    public void testNewNoteWithoutQuantity() throws IOException {
        String staff = createAndReturnStaffJs();
        String product = createAndReturnProductJs((float) 123);
        String details = "[{\"product\": " + product + "}]";
        assertEquals("Failed to create inventory delivery note to have details without valid quantity"
                , getActualOutputPostMethod("inventoryDeliveryNotes/create",
                        "{\"staff\": " + staff + "," +
                                "\"details\": " + details + "}"));
    }

    @Test
    public void testNewNoteWithoutProduct() throws IOException {
        String staff = createAndReturnStaffJs();
        String details = "[{\"quantity\": \"1\"}]";
        assertEquals("Failed to create inventory delivery note to have details without existent product"
                , getActualOutputPostMethod("inventoryDeliveryNotes/create",
                        "{\"staff\": " + staff + "," +
                                "\"details\": " + details + "}"));
    }

    @Test
    public void testNewNoteWithShortageOfProduct() throws IOException {
        String staff = createAndReturnStaffJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 9999);
        assertEquals("Failed to create inventory delivery note to have details with shortage of product"
                , getActualOutputPostMethod("inventoryDeliveryNotes/create",
                        "{\"staff\": " + staff + "," +
                                "\"details\": " + details + "}\""));
    }

    @Test
    public void testNewNoteWithDetailsHavingTheSameProduct() throws IOException {
        String staff = createAndReturnStaffJs();
        String product = createAndReturnProductJs((float) 123);
        String details = "[{\"quantity\": 4," +
                            "\"product\": " + product + "}," +
                            "{\"quantity\": 4," +
                            "\"product\": " + product + "}]";
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        assertEquals("Failed to create inventory delivery note to have details with the same product", returnString);
    }

    @Test
    public void testNewNoteAutoGenerateDate() throws IOException {
        String staff = createAndReturnStaffJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");
        assertEquals("Successfully create new inventory delivery note with id " + deliveryNotes.get(deliveryNotes.size() - 1).getId(),
                returnString);
        assertEquals(new Date(System.currentTimeMillis()).toString(), deliveryNotes.get(deliveryNotes.size() - 1).getDate().toString());
    }

    @Test
    public void testUpdateNoteWithDetailBelongedToAnotherNote() throws IOException {
        createAndReturnDeliveryNoteJs();
        createAndReturnDeliveryNoteJs();
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                "{\"id\": " + deliveryNotes.get(deliveryNotes.size() - 2).getId() + "," +
                        "\"details\": [{\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getId() + "}]}");
        assertEquals("Failed to update inventory delivery note to have details belonged to another note", returnString);
    }

    @Test
    public void testUpdateNoteWithNonExistentNote() throws IOException {
        createAndReturnDeliveryNoteJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update", "{\"details\": " + details + "}\"");
        assertEquals("Failed to update non-existent inventory delivery note", returnString);
    }

    @Test
    public void testUpdateNoteWithAlreadyDeliveredNote() throws IOException {
        createAndReturnSalesInvoiceJs();
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId() + ", \"staff\": {\"id\" : 0}}\"");
        assertEquals("Failed to update already delivered inventory note", returnString);
    }

    @Test
    public void testUpdateNoteWithNonExistentStaff() throws IOException {
        createAndReturnDeliveryNoteJs();
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId() + ", \"staff\": {\"id\" : 0}}\"");
        assertEquals("Failed to update inventory delivery note to have non-existent staff", returnString);
    }

    @Test
    public void testUpdateNoteWithDetailsHavingTheSameProduct() throws IOException {
        String staff = createAndReturnStaffJs();
        String product = createAndReturnProductJs((float) 123);
        String details = "[{\"quantity\": 4," +
                "\"product\": " + product + "}]";
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");
        details = "[{\"quantity\": 4," +
                "\"product\": " + product + "}," +
                "{\"quantity\": 4," +
                "\"product\": " + product + "}]";
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                "{\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1).getId() +
                        ",\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        assertEquals("Failed to update inventory delivery note to have details with the same product or missing detail id", returnString);
    }

    @Test
    public void testUpdateNoteWithShortageOfProduct() throws IOException {
        createAndReturnDeliveryNoteJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 9999);
        assertEquals("Failed to update inventory delivery note to have details with shortage of product"
                , getActualOutputPostMethod("inventoryDeliveryNotes/update",
                        "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId()
                                + ", \"details\": "+ details + "}"));
    }

    @Test
    public void testUpdateNoteWithDetailChangingProduct() throws IOException {
        createAndReturnDeliveryNoteJs();
        createAndReturnProductJs((float) 123);
        assertEquals("Failed to update inventory delivery note to have details changing products"
                , getActualOutputPostMethod("inventoryDeliveryNotes/update",
                        "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId()
                                + ", \"details\": [{\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getId()  + ", " +
                                "\"product\": { \"id\": " + products.get(products.size() - 1).getId() + "}}]}"));
    }

    @Test
    public void testUpdateNoteWithInvalidQuantity() throws IOException {
        createAndReturnDeliveryNoteJs();
        String details = "[{\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getId() + "," +
                "\"quantity\": -1}]";
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId() + ", \"details\": "+ details + "}");
        assertEquals("Failed to update inventory delivery note to have details without valid quantity", returnString);
    }

    @Test
    public void testUpdateNoteNonUpdatedFieldsStaysStill() throws IOException {
        createAndReturnDeliveryNoteJs();
        int staffId = deliveryNotes.get(deliveryNotes.size() - 1).getStaff().getId();
        Date date = deliveryNotes.get(deliveryNotes.size() - 1).getDate();

        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 456), 1);
        String returnString = getActualOutputPostMethod("inventoryDeliveryNotes/update",
                                                        "{\"id\" : " + deliveryNotes.get(deliveryNotes.size() - 1).getId()
                                                              + ", \"details\": "+ details + "}");
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");

        assertEquals("Successfully update inventory delivery note with id " + deliveryNotes.get(deliveryNotes.size() - 1).getId(), returnString);
        assertEquals(staffId, deliveryNotes.get(deliveryNotes.size() - 1).getStaff().getId());
        assertEquals(date.toString(), deliveryNotes.get(deliveryNotes.size() - 1).getDate().toString());
    }

    @Test
    public void testDeleteExistentAndNonExistentNote() throws IOException{
        deleteAllNotes();
        assertEquals( "Failed to delete non-existent inventory delivery note", setupDeleteConnection("inventoryDeliveryNotes/delete/id?id=1"));
        createAndReturnDeliveryNoteJs();
        int newNoteId = deliveryNotes.get(deliveryNotes.size() - 1).getId();
        assertEquals( "Successfully delete inventory delivery note with id " + newNoteId, setupDeleteConnection("inventoryDeliveryNotes/delete/id?id=" + newNoteId));
    }

    public void deleteAllNotes() throws IOException {
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");
        for (InventoryDeliveryNote note : deliveryNotes) {
            setupDeleteConnection("inventoryDeliveryNotes/delete/id?id=" + note.getId());
        }
    }
}

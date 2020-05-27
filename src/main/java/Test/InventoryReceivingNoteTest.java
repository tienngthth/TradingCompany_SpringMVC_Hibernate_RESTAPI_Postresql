package Test;

import model.InventoryReceivingNote;
import org.junit.Test;
import static Test.TestConfig.*;
import java.io.IOException;
import java.sql.Date;
import static org.junit.Assert.assertEquals;

public class InventoryReceivingNoteTest {
    @Test
    public void testGetAllNotesWithoutPaging() throws IOException {
        deleteAllNotes();
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=-1");
        assertEquals(0, receivingNotes.size());

        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        assertEquals(6, receivingNotes.size());
    }

    @Test
    public void testGetNotesWithMoreThanOnePage() throws IOException {
        deleteAllNotes();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=1");
        assertEquals(5, receivingNotes.size());
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=2");
        assertEquals(1, receivingNotes.size());
    }

    @Test
    public void testGetNoteById() throws IOException {
        createAndReturnReceivingNoteJs();
        getActualOutputGetMethod("inventoryReceivingNotes/read/id/?id=" + receivingNotes.get(receivingNotes.size() - 1).getId());
        assertEquals(1, receivingNotes.size());
        assertEquals(staffs.get(staffs.size() - 1).getId(), receivingNotes.get(receivingNotes.size() - 1).getStaff().getId());
    }

    @Test
    public void testGetNotesByDate() throws IOException {
        deleteAllNotes();
        String order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"order\": " + order + "}");
        order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"order\": " + order + "}");

        getActualOutputGetMethod("inventoryReceivingNotes/read/date?date=1999-01-01&page=-1");
        assertEquals(2, receivingNotes.size());
        assertEquals("1999-01-01", receivingNotes.get(0).getDate().toString());
        getActualOutputGetMethod("inventoryReceivingNotes/read/date?date=1000-01-01&page=-1");
        assertEquals(0, receivingNotes.size());
    }

    @Test
    public void testGetNotesByPeriod() throws IOException {
        deleteAllNotes();
        String order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"order\": " + order + "}");
        order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"date\": \"1999-03-01\"," +
                        "\"order\": " + order + "}");
        order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"date\": \"1999-04-01\"," +
                        "\"order\": " + order + "}");

        getActualOutputGetMethod("inventoryReceivingNotes/read/period?start=1999-1-1&page=-1&end=1999-03-01");
        assertEquals(2, receivingNotes.size());
        assertEquals("1999-01-01", receivingNotes.get(0).getDate().toString());
        assertEquals("1999-03-01", receivingNotes.get(1).getDate().toString());
        getActualOutputGetMethod("inventoryReceivingNotes/read/period?start=1999-3-1&page=-1&end=1999-01-01");
        assertEquals(0, receivingNotes.size());
    }

    @Test
    public void testNewNoteWithNonExistentStaff() throws IOException {
        String order = createAndReturnOrderJs();
        assertEquals("Failed to create inventory receiving note to have non-existent staff"
                , getActualOutputPostMethod("inventoryReceivingNotes/create"
                        , "{\"staff\": {\"id\" : 0}," +
                                "\"order\" :" + order +"}"));
    }

    @Test
    public void testNewNoteWithNonExistentOrder() throws IOException {
        assertEquals("Failed to create inventory receiving note to have non-existent order or without order"
                , getActualOutputPostMethod("inventoryReceivingNotes/create"
                        , "{\"order\": {\"id\" : 0}}"));
    }

    @Test
    public void testNewNoteWithOrderBelongedToAnotherNote() throws IOException {
        createAndReturnReceivingNoteJs();
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"order\": {\"id\": " + orders.get(orders.size() - 1) .getId() + "}}");
        assertEquals("Failed to create inventory receiving note to have order belonged to another note", returnString);
    }

    @Test
    public void testNewNoteAutoGenerateData() throws IOException {
        String order = createAndReturnOrderJs();
        String returnString =  getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"order\": " + order + "}");
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=-1");
        assertEquals("Successfully create new inventory receiving note with id " + receivingNotes.get(receivingNotes.size() - 1).getId(), returnString);
        assertEquals(new Date(System.currentTimeMillis()).toString(), receivingNotes.get(receivingNotes.size() - 1).getDate().toString());
        assertEquals(orders.get(orders.size() - 1).getDetails().size(), receivingNotes.get(receivingNotes.size() - 1).getDetails().size());
        assertEquals(orders.get(orders.size() - 1).getDetails().get(0).getProduct().getId(), receivingNotes.get(receivingNotes.size() - 1).getDetails().get(0).getProduct().getId());
        assertEquals(orders.get(orders.size() - 1).getDetails().get(0).getQuantity(), receivingNotes.get(receivingNotes.size() - 1).getDetails().get(0).getQuantity());
    }

    @Test
    public void testUpdateNoteWithNonExistentNote() throws IOException {
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/update",
                "{\"date\": \"2222-02-02\"}\"");
        assertEquals("Failed to update non-existent inventory receiving note", returnString);
    }

    @Test
    public void testUpdateNoteWithNonExistentStaff() throws IOException {
        createAndReturnReceivingNoteJs();
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/update",
                "{\"id\" : " + receivingNotes.get(receivingNotes.size() - 1).getId() + ", \"staff\": {\"id\" : 0}}\"");
        assertEquals("Failed to update inventory receiving note to have non-existent staff", returnString);
    }

    @Test
    public void testUpdateNoteWithNonExistentOrder() throws IOException {
        createAndReturnReceivingNoteJs();
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/update",
                "{\"id\": " + receivingNotes.get(receivingNotes.size() - 1).getId() +
                        ", \"order\": {\"id\" : 0}}");
        assertEquals("Failed to update inventory receiving note to have non-existent order", returnString);
    }

    @Test
    public void testUpdateNoteWithOrderBelongedToAnotherNote() throws IOException {
        createAndReturnReceivingNoteJs();
        createAndReturnReceivingNoteJs();
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/update",
                "{\"id\": " + receivingNotes.get(receivingNotes.size() - 1).getId() +
                        ", \"order\": {\"id\": " + orders.get(orders.size() - 2) .getId() + "}}");
        assertEquals("Failed to update inventory receiving note to have order belonged to another note", returnString);
    }

    @Test
    public void testUpdateNoteAutoGenerateDataAndNonUpdatedFieldsStaysStill() throws IOException {
        createAndReturnReceivingNoteJs();
        String date = receivingNotes.get(receivingNotes.size() - 1).getDate().toString();
        createAndReturnOrderJs();
        String returnString = getActualOutputPostMethod("inventoryReceivingNotes/update",
                "{\"id\": " + receivingNotes.get(receivingNotes.size() - 1).getId() +
                        ", \"order\": {\"id\": " + orders.get(orders.size() - 1) .getId() + "}}");
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=-1");
        assertEquals("Successfully update inventory receiving note with id " + receivingNotes.get(receivingNotes.size() - 1).getId(), returnString);
        assertEquals(date, receivingNotes.get(receivingNotes.size() - 1).getDate().toString());
        assertEquals(orders.get(orders.size() - 1).getStaff().getName(), receivingNotes.get(receivingNotes.size() - 1).getStaff().getName());
        assertEquals(orders.get(orders.size() - 1).getDetails().size(), receivingNotes.get(receivingNotes.size() - 1).getDetails().size());
        assertEquals(orders.get(orders.size() - 1).getDetails().get(0).getProduct().getId(), receivingNotes.get(receivingNotes.size() - 1).getDetails().get(0).getProduct().getId());
        assertEquals(orders.get(orders.size() - 1).getDetails().get(0).getQuantity(), receivingNotes.get(receivingNotes.size() - 1).getDetails().get(0).getQuantity());
    }

    @Test
    public void testDeleteExistentAndNonExistentNote() throws IOException{
        deleteAllNotes();
        assertEquals( "Failed to delete non-existent inventory receiving note", setupDeleteConnection("inventoryReceivingNotes/delete/id?id=1"));
        createAndReturnReceivingNoteJs();
        int newNoteId = receivingNotes.get(receivingNotes.size() - 1).getId();
        assertEquals( "Successfully delete inventory receiving note with id " + newNoteId, setupDeleteConnection("inventoryReceivingNotes/delete/id?id=" + newNoteId));
    }

    public void deleteAllNotes() throws IOException {
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=-1");
        for (InventoryReceivingNote note : receivingNotes) {
            setupDeleteConnection("inventoryReceivingNotes/delete/id?id=" + note.getId());
        }
    }
}

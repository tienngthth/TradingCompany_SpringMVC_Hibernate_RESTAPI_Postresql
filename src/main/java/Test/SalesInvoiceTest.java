package Test;

import model.SalesInvoice;
import org.junit.Test;
import static Test.TestConfig.*;
import java.io.IOException;
import java.sql.Date;

import static org.junit.Assert.assertEquals;
public class SalesInvoiceTest {
    @Test
    public void testGetAllInvoicesWithoutPaging() throws IOException {
        deleteAllInvoices();
        getActualOutputGetMethod("salesInvoices/read/all/?page=-1");
        assertEquals(0, salesInvoices.size());

        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        assertEquals(6, salesInvoices.size());
    }

    @Test
    public void testGetInvoicesWithMoreThanOnePage() throws IOException {
        deleteAllInvoices();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        getActualOutputGetMethod("salesInvoices/read/all/?page=1");
        assertEquals(5, salesInvoices.size());
        getActualOutputGetMethod("salesInvoices/read/all/?page=2");
        assertEquals(3, salesInvoices.size());
    }

    @Test
    public void testGetInvoiceById() throws IOException {
        createAndReturnSalesInvoiceJs();
        getActualOutputGetMethod("salesInvoices/read/id/?id=" + salesInvoices.get(salesInvoices.size() - 1).getId());
        assertEquals(1, salesInvoices.size());
        assertEquals(staffs.get(staffs.size() - 1).getName(), salesInvoices.get(salesInvoices.size() - 1).getSalesStaffName());
        assertEquals(customers.get(customers.size() - 1).getId(), salesInvoices.get(salesInvoices.size() - 1).getCustomer().getId());
    }

    @Test
    public void testGetInvoicesByDate() throws IOException {
        deleteAllInvoices();
        String deliveryNote = createAndReturnDeliveryNoteJs();
        String customer = createAndReturnCustomerJs();
        getActualOutputPostMethod("salesInvoices/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
        deliveryNote = createAndReturnDeliveryNoteJs();
        getActualOutputPostMethod("salesInvoices/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");

        getActualOutputGetMethod("salesInvoices/read/date?date=1999-01-01&page=-1");
        assertEquals(2, salesInvoices.size());
        assertEquals("1999-01-01", salesInvoices.get(0).getDate().toString());
        getActualOutputGetMethod("salesInvoices/read/date?date=1000-01-01&page=-1");
        assertEquals(0, salesInvoices.size());
    }

    @Test
    public void testGetInvoicesAndRevenueByPeriod() throws IOException {
        setUpTestGetByPeriod();

        getActualOutputGetMethod("salesInvoices/read/period?start=1999-01-01&page=-1&end=1999-03-01");
        assertEquals(2, salesInvoices.size());
        assertEquals("1999-01-01", salesInvoices.get(0).getDate().toString());
        assertEquals("1999-03-01", salesInvoices.get(1).getDate().toString());
        getActualOutputGetMethod("salesInvoices/read/period?start=1999-03-01&page=-1&end=1999-01-01");
        assertEquals(0, salesInvoices.size());

        getActualOutputGetMethod("salesInvoices/read/period?salesStaffName=kat" +
                "&start=1999-01-01&page=-1&end=1999-03-01");
        assertEquals(2, salesInvoices.size());
        assertEquals("kat", salesInvoices.get(0).getSalesStaffName());
        assertEquals("kat", salesInvoices.get(1).getSalesStaffName());

        getActualOutputGetMethod("salesInvoices/read/period?cId="+ customers.get(customers.size() - 1).getId() + "&start=1999-1-1&page=-1&end=1999-3-1");
        assertEquals(2, salesInvoices.size());
        assertEquals(customers.get(customers.size() - 1).getId(), salesInvoices.get(0).getCustomer().getId());
        assertEquals(customers.get(customers.size() - 1).getId(), salesInvoices.get(1).getCustomer().getId());


        assertEquals("Total revenue: 246.0", setUpGetConnection("salesInvoices/revenue/read/customer-period" +
                "?cId=" + customers.get(customers.size() - 1).getId() + "&start=1999-01-01&page=-1&end=1999-03-01"));
        assertEquals("Total revenue: 246.0", setUpGetConnection("salesInvoices/revenue/read/staff-period" +
                "?salesStaffName=kat&start=1999-01-01&page=-1&end=1999-03-01"));
    }

    private void setUpTestGetByPeriod() throws IOException {
        deleteAllInvoices();
        String deliveryNote = createAndReturnDeliveryNoteJs();
        String customer = createAndReturnCustomerJs();
        getActualOutputGetMethod("customers/read/all/?page=-1");
        getActualOutputPostMethod("salesInvoices/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
        deliveryNote = createAndReturnDeliveryNoteJs();
        getActualOutputPostMethod("salesInvoices/create",
                "{\"date\": \"1999-03-01\"," +
                        "\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
        deliveryNote = createAndReturnDeliveryNoteJs();
        getActualOutputPostMethod("salesInvoices/create",
                "{\"date\": \"1999-04-01\"," +
                        "\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
    }

    @Test
    public void testNewInvoiceWithNonExistentCustomer() throws IOException {
        assertEquals("Failed to create new sales invoice to have non-existent customer or without customer"
                , getActualOutputPostMethod("salesInvoices/create"
                        , "{\"customer\": {\"id\" : 0}}"));
    }

    @Test
    public void testNewInvoiceWithNonExistentStaffName() throws IOException {
        assertEquals("Failed to create sales invoice with non-existent staff"
                , getActualOutputPostMethod("salesInvoices/create"
                        , "{\"salesStaffName\": \"Ben\"}"));
    }

    @Test
    public void testNewInvoiceWithNonExistentDeliveryNote() throws IOException {
        String customer = createAndReturnCustomerJs();
        assertEquals("Failed to create sales invoice to have non-existent inventory delivery note or without inventory delivery note"
                , getActualOutputPostMethod("salesInvoices/create"
                        , "{\"customer\": " + customer +
                                ",\"note\": {\"id\" : 0}}"));
    }

    @Test
    public void testNewInvoiceWithDeliveryNoteBelongedToAnotherInvoice() throws IOException {
        createAndReturnSalesInvoiceJs();
        String customer = createAndReturnCustomerJs();
        String returnString = getActualOutputPostMethod("salesInvoices/create",
                 "{\"customer\": " + customer +
                        ", \"note\": {\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1).getId() + "}}");
        assertEquals("Failed to create sales invoice to have inventory delivery note belonged to another invoice", returnString);
    }

    @Test
    public void testNewInvoiceAutoGenerateData() throws IOException {
        String deliveryNote = createAndReturnDeliveryNoteJs();
        String customer = createAndReturnCustomerJs();
        String returnString = getActualOutputPostMethod("salesInvoices/create",
                "{\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
        getActualOutputGetMethod("salesInvoices/read/all/?page=-1");
        assertEquals("Successfully create new sales invoice with id " + salesInvoices.get(salesInvoices.size() - 1).getId(), returnString);
        assertEquals(new Date(System.currentTimeMillis()).toString(), salesInvoices.get(salesInvoices.size() - 1).getDate().toString());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().size(), salesInvoices.get(salesInvoices.size() - 1).getDetails().size());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getProduct().getId(), salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getProduct().getId());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getQuantity(), salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getQuantity());
    }

    @Test
    public void testUpdateInvoiceWithNonExistentInvoice() throws IOException {
        String returnString = getActualOutputPostMethod("salesInvoices/update",
                "{\"date\": \"2222-02-02\"}\"");
        assertEquals("Failed to update non-existent sales invoice", returnString);
    }

    @Test
    public void testUpdateInvoiceWithNonExistentCustomer() throws IOException {
        createAndReturnSalesInvoiceJs();
        String returnString = getActualOutputPostMethod("salesInvoices/update",
                "{\"id\" : " + salesInvoices.get(salesInvoices.size() - 1).getId() + ", \"customer\": {\"id\" : 0}}\"");
        assertEquals("Failed to update sales invoice to have non-existent customer", returnString);
    }

    @Test
    public void testUpdateInvoiceWithNonExistentStaffName() throws IOException {
        createAndReturnSalesInvoiceJs();
        String returnString = getActualOutputPostMethod("salesInvoices/update",
                "{\"id\" : " + salesInvoices.get(salesInvoices.size() - 1).getId() + ", \"salesStaffName\": \"Ben\"}");
        assertEquals("Failed to update sales invoice to have with non-existent staff", returnString);
    }

    @Test
    public void testUpdateInvoiceWithDeliveryNoteBelongedToAnotherInvoice() throws IOException {
        createAndReturnSalesInvoiceJs();
        createAndReturnSalesInvoiceJs();
        String returnString = getActualOutputPostMethod("salesInvoices/update",
                "{\"id\": " + salesInvoices.get(salesInvoices.size() - 1).getId() +
                        ", \"note\": {\"id\": " + deliveryNotes.get(deliveryNotes.size() - 2).getId() + "}}");
        assertEquals("Failed to update sales invoice to have inventory delivery note belonged to another invoice", returnString);
    }

    @Test
    public void testUpdateInvoiceAutoGenerateDataAndNonUpdatedFieldsStaysStill() throws IOException {
        createAndReturnSalesInvoiceJs();
        int customerId = salesInvoices.get(salesInvoices.size() - 1).getCustomer().getId();
        createAndReturnDeliveryNoteJs();
        String returnString = getActualOutputPostMethod("salesInvoices/update",
                "{\"id\": " + salesInvoices.get(salesInvoices.size() - 1).getId() +
                        ", \"note\": {\"id\": " + deliveryNotes.get(deliveryNotes.size() - 1) .getId() + "}}");
        getActualOutputGetMethod("salesInvoices/read/all/?page=-1");
        assertEquals("Successfully update sales invoice with id " + salesInvoices.get(salesInvoices.size() - 1).getId(), returnString);
        assertEquals(customerId, salesInvoices.get(salesInvoices.size() - 1).getCustomer().getId());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getStaff().getName(), salesInvoices.get(salesInvoices.size() - 1).getSalesStaffName());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().size(), salesInvoices.get(salesInvoices.size() - 1).getDetails().size());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getProduct().getId(), salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getProduct().getId());
        assertEquals(deliveryNotes.get(deliveryNotes.size() - 1).getDetails().get(0).getQuantity(), salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getQuantity());
    }

    @Test
    public void testUpdatePriceForDetail() throws IOException {
        createAndReturnSalesInvoiceJs();
        setupPostConnection("salesInvoices/update/detail/price/?id="
                + salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getId() + "&price=4444");
        assertEquals("Successfully update price of detail with id "
                + salesInvoices.get(salesInvoices.size() - 1).getDetails().get(0).getId(), getPostOutputMessage());
    }

    @Test
    public void testUpdatePriceForNonExistentDetail() throws IOException {
        createAndReturnSalesInvoiceJs();
        setupPostConnection("salesInvoices/update/detail/price/?id=0&price=4444");
        assertEquals("Failed to update price of non-existent detail", getPostOutputMessage());
    }

    @Test
    public void testDeleteExistentAndNonExistentInvoice() throws IOException{
        deleteAllInvoices();
        assertEquals( "Failed to delete non-existent sales invoice", setupDeleteConnection("salesInvoices/delete/id?id=1"));
        createAndReturnSalesInvoiceJs();
        int newInvoiceId = salesInvoices.get(salesInvoices.size() - 1).getId();
        assertEquals( "Successfully delete sales invoice with id " + newInvoiceId, setupDeleteConnection("salesInvoices/delete/id?id=" + newInvoiceId));
    }

    public void deleteAllInvoices() throws IOException {
        getActualOutputGetMethod("salesInvoices/read/all/?page=-1");
        for (SalesInvoice invoice : salesInvoices) {
            setupDeleteConnection("salesInvoices/delete/id?id=" + invoice.getId());
        }
    }
}

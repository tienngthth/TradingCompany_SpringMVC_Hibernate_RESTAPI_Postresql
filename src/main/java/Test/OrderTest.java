package Test;

import model.ProviderOrder;
import org.junit.Test;
import static Test.TestConfig.*;
import java.io.IOException;
import java.sql.Date;
import static org.junit.Assert.assertEquals;

public class OrderTest {
    @Test
    public void testGetAllOrdersWithoutPaging() throws IOException {
        deleteAllOrders();
        getActualOutputGetMethod("orders/read/all/?page=-1");
        assertEquals(0, orders.size());

        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        assertEquals(6, orders.size());
    }

    @Test
    public void testGetOrdersWithMoreThanOnePage() throws IOException {
        deleteAllOrders();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        getActualOutputGetMethod("orders/read/all/?page=1");
        assertEquals(5, orders.size());
        getActualOutputGetMethod("orders/read/all/?page=2");
        assertEquals(1, orders.size());
    }

    @Test
    public void testGetOrderById() throws IOException {
        createAndReturnOrderJs();
        getActualOutputGetMethod("orders/read/id/?id=" + orders.get(orders.size() - 1).getId());
        assertEquals(1, orders.size());
        assertEquals(staffs.get(staffs.size() - 1).getId(), orders.get(orders.size() - 1).getStaff().getId());
        assertEquals(providers.get(providers.size() - 1).getId(), orders.get(orders.size() - 1).getProvider().getId());
    }

    @Test
    public void testGetOrdersByDate() throws IOException {
        deleteAllOrders();
        String staff = createAndReturnStaffJs();
        String provider = createAndReturnProviderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 132), 1);
        getActualOutputPostMethod("orders/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        provider = createAndReturnProviderJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("orders/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");

        getActualOutputGetMethod("orders/read/date?date=1999-01-01&page=-1");
        assertEquals(2, orders.size());
        assertEquals("1999-01-01", orders.get(0).getDate().toString());
        getActualOutputGetMethod("orders/read/date?date=1000-01-01&page=-1");
        assertEquals(0, orders.size());
    }

    @Test
    public void testGetOrdersByPeriod() throws IOException {
        deleteAllOrders();
        String staff = createAndReturnStaffJs();
        String provider = createAndReturnProviderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 132), 1);
        getActualOutputPostMethod("orders/create",
                "{\"date\": \"1999-01-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        provider = createAndReturnProviderJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("orders/create",
                "{\"date\": \"1999-03-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");
        staff = createAndReturnStaffJs();
        provider = createAndReturnProviderJs();
        details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("orders/create",
                "{\"date\": \"1999-04-01\"," +
                        "\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");

        getActualOutputGetMethod("orders/read/period?start=1999-01-01&page=-1&end=1999-03-01");
        assertEquals(2, orders.size());
        assertEquals("1999-01-01", orders.get(0).getDate().toString());
        assertEquals("1999-03-01", orders.get(1).getDate().toString());
        getActualOutputGetMethod("orders/read/period?start=1999-03-01&page=-1&end=1999-01-01");
        assertEquals(0, orders.size());
    }

    @Test
    public void testNewOrderWithoutProvider() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        String staff = "{\"id\": \""+ staffs.get(staffs.size() - 1).getId() + "\"}";
        assertEquals("Failed to create order to have non-existent provider or without provider"
                , getActualOutputPostMethod("orders/create", "{\"staff\": " + staff + "}"));
    }

    @Test
    public void testNewOrderWithoutDetails() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        String staff = "{\"id\": \""+ staffs.get(staffs.size() - 1).getId() + "\"}";

        getActualOutputPostMethod("providers/create",
                "{\"name\": \"Andy\"}");
        getActualOutputGetMethod("providers/read/all");
        String provider = "{\"id\": \""+ providers.get(providers.size() - 1).getId() + "\"}";

        assertEquals("Failed to create order without any details"
                , getActualOutputPostMethod("orders/create",
                        "{\"staff\": " + staff + "," +
                                "\"provider\": " + provider + "}\""));
    }

    @Test
    public void testNewOrderWithoutQuantity() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        String staff = "{\"id\": \""+ staffs.get(staffs.size() - 1).getId() + "\"}";

        getActualOutputPostMethod("providers/create",
                "{\"name\": \"Andy\"}");
        getActualOutputGetMethod("providers/read/all");
        String provider = "{\"id\": \""+ providers.get(providers.size() - 1).getId() + "\"}";

        String details = "[{}]";

        assertEquals("Failed to create order to have details without valid quantity"
                , getActualOutputPostMethod("orders/create",
                        "{\"staff\": " + staff + "," +
                                "\"provider\": " + provider + "," +
                                "\"details\": " + details + "}\""));
    }

    @Test
    public void testNewOrderWithoutProduct() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        String staff = "{\"id\": \""+ staffs.get(staffs.size() - 1).getId() + "\"}";

        getActualOutputPostMethod("providers/create",
                "{\"name\": \"Andy\"}");
        getActualOutputGetMethod("providers/read/all");
        String provider = "{\"id\": \""+ providers.get(providers.size() - 1).getId() + "\"}";

        String details = "[{\"quantity\": \"1\"}]";

        assertEquals("Failed to create order to have details without product"
                , getActualOutputPostMethod("orders/create",
                        "{\"staff\": " + staff + "," +
                                "\"provider\": " + provider + "," +
                                "\"details\": " + details + "}\""));
    }

    @Test
    public void testNewOrderNotForSellProduct() throws IOException {
        String staff = createAndReturnStaffJs();
        String provider = createAndReturnProviderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs(null), 1);
        assertEquals("Failed to create order to have details with not-for-sell product (missing selling price)"
                , getActualOutputPostMethod("orders/create",
                        "{\"staff\": " + staff + "," +
                                "\"provider\": " + provider + "," +
                                "\"details\": " + details + "}\""));
    }

    @Test
    public void testNewOrderAutoGenerateDateAndPrice() throws IOException {
        String staff = createAndReturnStaffJs();
        String provider = createAndReturnProviderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 1234), 1);
        String returnString = getActualOutputPostMethod("orders/create",
                "{\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("orders/read/all/?page=-1");
        assertEquals("Successfully create new order with id " + orders.get(orders.size() - 1).getId(),
                returnString);
        assertEquals(new Date(System.currentTimeMillis()).toString() , orders.get(orders.size() - 1).getDate().toString());
        assertEquals(true,1234 ==  orders.get(orders.size() - 1).getDetails().get(0).getPrice());
    }

    @Test
    public void testUpdateOrderWithToHaveDetailsWithTheSameId() throws IOException {
        createAndReturnOrderJs();
        String returnString = getActualOutputPostMethod("orders/update",
                "{\"id\": " + orders.get(orders.size() - 1).getId() + "," +
                        "\"details\": [{\"id\": "+ orders.get(orders.size() -1).getDetails().get(0).getId() + "}," +
                        "{\"id\": "+ orders.get(orders.size() -1).getDetails().get(0).getId() + "}]}");
        assertEquals("Failed to update order to have details with the same id", returnString);
    }

    @Test
    public void testUpdateOrderWithDetailBelongedToAnotherOrder() throws IOException {
        createAndReturnOrderJs();
        createAndReturnOrderJs();
        String returnString = getActualOutputPostMethod("orders/update",
                "{\"id\": " + orders.get(orders.size() - 2).getId() + "," +
                      "\"details\": [{\"id\": "+ orders.get(orders.size() -1).getDetails().get(0).getId() + "}]}");
        assertEquals("Failed to update order to have details belonged to another order", returnString);
    }

    @Test
    public void testUpdateOrderWithNonExistentOrder() throws IOException {
        createAndReturnOrderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        String returnString = getActualOutputPostMethod("orders/update", "{\"details\": " + details + "}\"");
        assertEquals("Failed to update non-existent order", returnString);
    }

    @Test
    public void testUpdateOrderWithAlreadyReceivedOrder() throws IOException {
        createAndReturnReceivingNoteJs();
        String returnString = getActualOutputPostMethod("orders/update",
                "{\"id\" : " + orders.get(orders.size() - 1).getId()
                        + ", \"staff\": {\"id\" : 0}}\"");
        assertEquals("Failed to update already received order", returnString);
    }

    @Test
    public void testUpdateOrderWithNonExistentStaff() throws IOException {
        createAndReturnOrderJs();
        String returnString = getActualOutputPostMethod("orders/update",
                                                            "{\"id\" : " + orders.get(orders.size() - 1).getId()
                                                                    + ", \"staff\": {\"id\" : 0}}\"");
        assertEquals("Failed to update order to have non-existent staff", returnString);
    }

    @Test
    public void testUpdateOrderWithNonExistentProvider() throws IOException {
        createAndReturnOrderJs();
        String returnString = getActualOutputPostMethod("orders/update",
                                                        "{\"id\" : " + orders.get(orders.size() - 1).getId()
                                                                + ", \"provider\": {\"id\" : 0}}\"");
        assertEquals("Failed to update order to have non-existent provider", returnString);
    }

    @Test
    public void testUpdateOrderWithNotForSellProduct() throws IOException {
        createAndReturnOrderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs(null), 1);
        String returnString = getActualOutputPostMethod("orders/update",
                                                        "{\"id\" : " + orders.get(orders.size() - 1).getId()
                                                                + ", \"details\": "+ details + "}");
        assertEquals("Failed to update order to have details with not-for-sell product (missing selling price)", returnString);
    }

    @Test
    public void testUpdateOrderWithNonExistentProduct() throws IOException {
        createAndReturnOrderJs();
        String details = createAndReturnDetailJs("{\"id\": 0}", 1);
        String returnString = getActualOutputPostMethod("orders/update",
                                                        "{\"id\" : " + orders.get(orders.size() - 1).getId()
                                                                + ", \"details\": "+ details + "}");
        assertEquals("Failed to update order to have details with non-existent product", returnString);
    }

    @Test
    public void testUpdateOrderWithInvalidQuantity() throws IOException {
        createAndReturnOrderJs();
        String details = "[{\"id\": " + orders.get(orders.size() - 1).getDetails().get(0).getId() + "," +
                            "\"quantity\": -1}]";
        String returnString = getActualOutputPostMethod("orders/update",
                                                    "{\"id\" : " + orders.get(orders.size() - 1).getId()
                                                            + ", \"details\": "+ details + "}");
        assertEquals("Failed to update order to have details without valid quantity", returnString);
    }

    @Test
    public void testUpdateOrderNonUpdatedFieldsStaysStillNewProductGeneratePrice() throws IOException {
        createAndReturnOrderJs();
        int staffId = orders.get(orders.size() - 1).getStaff().getId();
        int providerId = orders.get(orders.size() - 1).getProvider().getId();
        Date date = orders.get(orders.size() - 1).getDate();

        String returnString = getActualOutputPostMethod("orders/update",
                "{\"id\" : " + orders.get(orders.size() - 1).getId() +
                        ", \"details\": [{\"id\":" + orders.get(orders.size() - 1).getDetails().get(0).getId() + "," +
                                            "\"price\": 456}]}");
        getActualOutputGetMethod("orders/read/all/?page=-1");

        assertEquals("Successfully update order with id " + orders.get(orders.size() - 1).getId(), returnString);
        assertEquals(true,456 ==  orders.get(orders.size() - 1).getDetails().get(0).getPrice());
        assertEquals(staffId, orders.get(orders.size() - 1).getStaff().getId());
        assertEquals(providerId, orders.get(orders.size() - 1).getProvider().getId());
        assertEquals(date.toString(), orders.get(orders.size() - 1).getDate().toString());
    }

    @Test
    public void testDeleteExistentAndNonExistentOrder() throws IOException{
        deleteAllOrders();
        assertEquals( "Failed to delete non-existent order", setupDeleteConnection("orders/delete/id?id=1"));
        createAndReturnOrderJs();
        int newOrderId = orders.get(orders.size() - 1).getId();
        assertEquals( "Successfully delete order with id " + newOrderId, setupDeleteConnection("orders/delete/id?id=" + newOrderId));
    }

    public void deleteAllOrders() throws IOException {
        getActualOutputGetMethod("orders/read/all/?page=-1");
        for (ProviderOrder order : orders) {
            setupDeleteConnection("orders/delete/id?id=" + order.getId());
        }
    }
}

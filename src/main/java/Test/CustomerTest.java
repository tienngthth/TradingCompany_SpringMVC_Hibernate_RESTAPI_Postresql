package Test;

import model.Customer;
import org.junit.Test;
import java.io.IOException;
import static Test.TestConfig.*;
import static org.junit.Assert.assertEquals;

public class CustomerTest {
    @Test
    public void testGetAllCustomersWithoutPaging() throws IOException {
        deleteAllCustomers();
        getActualOutputGetMethod("customers/read/all/?page=-1");
        assertEquals(0, customers.size());

        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        assertEquals(6, customers.size());
    }

    @Test
    public void testGetCustomersWithMoreThanOnePage() throws IOException {
        deleteAllCustomers();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        createAndReturnCustomerJs();
        getActualOutputGetMethod("customers/read/all/?page=1");
        assertEquals(5, customers.size());
        getActualOutputGetMethod("customers/read/all/?page=2");
        assertEquals(1, customers.size());
    }

    @Test
    public void testGetCustomerById() throws IOException {
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("customers/read/all/?page=-1");
        int newCustomerId = customers.get(customers.size() - 1).getId();
        getActualOutputGetMethod("customers/read/id?id=" + newCustomerId);
        assertEquals(1, customers.size());
        assertEquals("andy", customers.get(0).getName());
    }

    @Test
    public void testGetCustomerByName() throws IOException {
        deleteAllCustomers();
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"andy\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("customers/read/name?name=andy&page=1");
        assertEquals(2, customers.size());
        assertEquals("andy", customers.get(0).getName());
        getActualOutputGetMethod("customers/read/name?name=xx&page=1");
        assertEquals(0, customers.size());
    }

    @Test
    public void testGetCustomerByPhone() throws IOException {
        deleteAllCustomers();
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"12341234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("customers/read/phone?phone=1234&page=1");
        assertEquals(2, customers.size());
        assertEquals("12341234", customers.get(1).getPhone());
        getActualOutputGetMethod("customers/read/phone?phone=99&page=1");
        assertEquals(0, customers.size());
    }

    @Test
    public void testGetCustomerByAddress() throws IOException {
        deleteAllCustomers();
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"ANDY\", \"phone\": \"12341234\", \"address\": \"abc\"}");
        getActualOutputGetMethod("customers/read/address?address=abc&page=1");
        assertEquals(2, customers.size());
        assertEquals("abc", customers.get(0).getAddress());
        getActualOutputGetMethod("customers/read/address?address=haha&page=1");
        assertEquals(0, customers.size());
    }

    @Test
    public void testNewCustomerWithNameAndAddressShouldBeLowerCaseAndNoSpaceInPhone() throws IOException {
        String output = getActualOutputPostMethod("customers/create",
                "{\"name\": \"KAT\", \"phone\": \"1234 1234\", \"address\": \"ABC\"" +
                        ", \"fax\": \"PO02\", \"email\": \"abc@gmail.com\", \"contactPerson\": \"Cat\"}");
        getActualOutputGetMethod("customers/read/all/?page=-1");
        int newCustomerId = customers.get(customers.size() - 1).getId();
        assertEquals("Successfully create new customer with id " + newCustomerId, output);
        assertEquals("kat", customers.get(customers.size() - 1).getName());
        assertEquals("12341234", customers.get(customers.size() - 1).getPhone());
        assertEquals("abc", customers.get(customers.size() - 1).getAddress());
        assertEquals("abc@gmail.com", customers.get(customers.size() - 1).getEmail());
        assertEquals("PO02", customers.get(customers.size() - 1).getFax());
        assertEquals("Cat", customers.get(customers.size() - 1).getContactPerson());
    }

    @Test
    public void testNewCustomerWithoutName() throws IOException {
        assertEquals("Failed to create new customer without name"
                , getActualOutputPostMethod("customers/create", "{\"address\": \"abcd\"}"));
    }

    @Test
    public void testUpdateCustomerWithoutIdOrWithNonExistentCustomer() throws IOException {
        assertEquals("Failed to update non-existent customer"
                , getActualOutputPostMethod("customers/update", "{\"address\": \"abcdefg\"}"));

        assertEquals("Failed to update non-existent customer"
                , getActualOutputPostMethod("customers/update", "{\"id\": \"-1\"}"));
    }

    @Test
    public void testUpdateCustomerWithNameAndAddressShouldBeLowerCaseAndNoSpaceInPhoneNonUpdatedFieldStaysStill() throws IOException {
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"KAT\", \"phone\": \"1234 1234\", \"address\": \"ABC\"" +
                        ", \"fax\": \"PO02\", \"email\": \"abc@gmail.com\", \"contactPerson\": \"Cat\"}");
        getActualOutputGetMethod("customers/read/all/?page=-1");
        int updatedCustomerId = customers.get(customers.size() - 1).getId();
        String updatedCustomer = getActualOutputPostMethod("customers/update",
                "{\"id\": " + updatedCustomerId +
                        ", \"name\": \"Andy\", \"phone\": \"12 12\", \"address\": \"Home\"}");
        getActualOutputGetMethod("customers/read/all/?page=-1");
        updatedCustomerId = customers.get(customers.size() - 1).getId();
        assertEquals("Successfully update customer with id " + updatedCustomerId, updatedCustomer);
        assertEquals("andy", customers.get(customers.size() - 1).getName());
        assertEquals("1212", customers.get(customers.size() - 1).getPhone());
        assertEquals("home", customers.get(customers.size() - 1).getAddress());
        assertEquals("abc@gmail.com", customers.get(customers.size() - 1).getEmail());
        assertEquals("PO02", customers.get(customers.size() - 1).getFax());
        assertEquals("Cat", customers.get(customers.size() - 1).getContactPerson());
    }

    @Test
    public void testDeleteExistentAndNonExistentCustomer() throws IOException{
        deleteAllCustomers();
        assertEquals( "Failed to delete non-existent customer", setupDeleteConnection("customers/delete/id?id=1"));

        createAndReturnCustomerJs();
        int newCustomerId = customers.get(customers.size() - 1).getId();
        assertEquals( "Successfully delete customer with id " + newCustomerId, setupDeleteConnection("customers/delete/id?id=" + newCustomerId));
    }

    public void deleteAllCustomers() throws IOException {
        getActualOutputGetMethod("customers/read/all/?page=-1");
        for (Customer customer : customers) {
            setupDeleteConnection("customers/delete/id?id=" + customer.getId());
        }
    }
}

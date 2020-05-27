package Test;

import model.Staff;
import org.junit.Test;
import java.io.IOException;
import static Test.TestConfig.*;
import static org.junit.Assert.assertEquals;

public class StaffTest {
    @Test
    public void testGetAllStaffsWithoutPaging() throws IOException {
        deleteAllStaffs();
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        assertEquals(0, staffs.size());

        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        assertEquals(6, staffs.size());
    }

    @Test
    public void testGetAllStaffsWithMoreThanOnePage() throws IOException {
        deleteAllStaffs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        createAndReturnStaffJs();
        getActualOutputGetMethod("staffs/read/all/?page=1");
        assertEquals(5, staffs.size());
        getActualOutputGetMethod("staffs/read/all/?page=2");
        assertEquals(1, staffs.size());
    }

    @Test
    public void testGetStaffById() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        int newStaffId = staffs.get(staffs.size() - 1).getId();
        getActualOutputGetMethod("staffs/read/id?id=" + newStaffId);
        assertEquals(1, staffs.size());
        assertEquals("andy", staffs.get(0).getName());
    }

    @Test
    public void testGetStaffByName() throws IOException {
        deleteAllStaffs();
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"andy\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("staffs/read/name?name=andy&page=1");
        assertEquals(2, staffs.size());
        assertEquals("andy", staffs.get(0).getName());
        getActualOutputGetMethod("staffs/read/name?name=xx&page=1");
        assertEquals(0, staffs.size());
    }

    @Test
    public void testGetStaffByPhone() throws IOException {
        deleteAllStaffs();
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"12341234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("staffs/read/phone?phone=1234&page=1");
        assertEquals(2, staffs.size());
        assertEquals("12341234", staffs.get(1).getPhone());
        getActualOutputGetMethod("staffs/read/phone?phone=0&page=1");
        assertEquals(0, staffs.size());
    }

    @Test
    public void testGetStaffByAddress() throws IOException {
        deleteAllStaffs();
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"ANDY\", \"phone\": \"12341234\", \"address\": \"abc\"}");
        getActualOutputGetMethod("staffs/read/address?address=abc&page=1");
        assertEquals(2, staffs.size());
        assertEquals("abc", staffs.get(0).getAddress());
        getActualOutputGetMethod("staffs/read/address?address=home&page=1");
        assertEquals(0, staffs.size());
    }

    @Test
    public void testNewStaffWithNameAndAddressShouldBeLowerCaseAndNoSpaceInPhone() throws IOException {
        String output = getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        int newStaffIndex = staffs.size() - 1;
        assertEquals("Successfully create new staff with id " + staffs.get(newStaffIndex).getId(), output);
        assertEquals("kat", staffs.get(newStaffIndex).getName());
        assertEquals("12341234", staffs.get(newStaffIndex).getPhone());
        assertEquals("abc", staffs.get(newStaffIndex).getAddress());
    }

    @Test
    public void testNewStaffWithoutName() throws IOException {
        assertEquals("Failed to create new staff without name"
                , getActualOutputPostMethod("staffs/create", "{\"address\": \"abcd\"}"));
    }

    @Test
    public void testUpdateStaffWithoutIdOrWithNonExistentStaff() throws IOException {
        assertEquals("Failed to update non-existent staff"
                , getActualOutputPostMethod("staffs/update", "{\"address\": \"abcdefg\"}"));

        assertEquals("Failed to update non-existent staff"
                , getActualOutputPostMethod("staffs/update", "{\"id\": \"-1\"}"));
    }

    @Test
    public void testUpdateStaffWithNameAndAddressShouldBeLowerCaseAndNoSpaceInPhoneNonUpdatedFieldsStaysStill() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\", \"phone\": \"1234 1234\", \"address\": \"ABC\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        int updatedStaffId = staffs.get(staffs.size() - 1).getId();
        String updatedStaff = getActualOutputPostMethod("staffs/update",
                "{\"id\": " + updatedStaffId +
                        ", \"name\": \"Andy\", \"phone\": \"12 12\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        updatedStaffId = staffs.get(staffs.size() - 1).getId();
        assertEquals("Successfully update staff with id " + updatedStaffId, updatedStaff);
        assertEquals("andy", staffs.get(staffs.size() - 1).getName());
        assertEquals("1212", staffs.get(staffs.size() - 1).getPhone());
        assertEquals("abc", staffs.get(staffs.size() - 1).getAddress());
    }

    @Test
    public void testDeleteExistentAndNonExistentStaff() throws IOException {
        deleteAllStaffs();
        assertEquals( "Failed to delete non-existent staff", setupDeleteConnection("staffs/delete/id?id=1"));

        createAndReturnStaffJs();
        int newStaffId = staffs.get(staffs.size() - 1).getId();
        assertEquals( "Successfully delete staff with id " + newStaffId, setupDeleteConnection("staffs/delete/id?id=" + newStaffId));
    }

    public void deleteAllStaffs() throws IOException {
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        for (Staff staff : staffs) {
            setupDeleteConnection("staffs/delete/id?id=" + staff.getId());
        }
    }
}

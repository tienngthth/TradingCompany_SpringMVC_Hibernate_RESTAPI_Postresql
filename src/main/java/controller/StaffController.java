package controller;

import model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.StaffService;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/staffs")
public class StaffController {
    @Autowired
    private StaffService staffService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<Staff> getAllStaffs(@RequestParam int page){
        return paginatedDisplay(staffService.getAllStaffs(), page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public Staff getStaffById(@RequestParam int id){
        return staffService.getStaffById(id);
    }

    @RequestMapping(path = "/read/name", method = RequestMethod.GET)
    public List<Staff> getStaffsByName(@RequestParam String name, @RequestParam int page){
        return paginatedDisplay(staffService.getStaffsByName(name.toLowerCase().replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/read/address", method = RequestMethod.GET)
    public List<Staff> getStaffsByAddress(@RequestParam String address, @RequestParam int page){
        return paginatedDisplay(staffService.getStaffsByAddress(address.toLowerCase().replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/read/phone", method = RequestMethod.GET)
    public List<Staff> getStaffsByPhone(@RequestParam String phone, @RequestParam int page){
        return paginatedDisplay(staffService.getStaffsByPhone(phone.replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newStaff(@RequestBody Staff staff){
        return staffService.newStaff(staff);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateStaff(@RequestBody Staff staff){
        return staffService.updateStaff(staff);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteStaff(@RequestParam int id){
        return staffService.deleteStaff(id);
    }

    public List<Staff> paginatedDisplay(List<Staff> staffs, int page) {
        int[] indices = SupportController.getIndices(staffs.size(), page);
        return staffs.subList(indices[0], indices[1]);
    }
}

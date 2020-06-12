package controller;

import model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.CustomerService;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<Customer> getAllCustomer(@RequestParam int page){
        return paginatedDisplay(customerService.getAllCustomers(), page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public Customer getCustomersById(@RequestParam int id){
        return  customerService.getCustomerById(id);
    }

    @RequestMapping(path = "/read/name", method = RequestMethod.GET)
    public List<Customer> getCustomersByName(@RequestParam String name, @RequestParam int page){
        return paginatedDisplay(customerService.getCustomersByName(name.toLowerCase().replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/read/address", method = RequestMethod.GET)
    public List<Customer> getCustomersByAddress(@RequestParam String address, @RequestParam int page){
        return paginatedDisplay(customerService.getCustomersByAddress(address.toLowerCase().replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/read/phone", method = RequestMethod.GET)
    public List<Customer> getCustomersByPhone(@RequestParam String phone, @RequestParam int page){
        return paginatedDisplay(customerService.getCustomersByPhone(phone.replaceAll(" ","")), page);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newCustomer(@RequestBody Customer customer){
        return customerService.newCustomer(customer);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateCustomer(@RequestBody Customer customer){
        return customerService.updateCustomer(customer);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteCustomer(@RequestParam int id){
        return customerService.deleteCustomer(id);
    }

    public List<Customer> paginatedDisplay(List<Customer> customers, int page) {
        int[] indices = SupportController.getIndices(customers.size(), page);
        return customers.subList(indices[0], indices[1]);
    }
}

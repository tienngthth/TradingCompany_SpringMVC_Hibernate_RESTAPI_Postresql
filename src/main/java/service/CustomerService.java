package service;

import model.Customer;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@Service
public class CustomerService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SalesInvoiceService salesInvoiceService;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<Customer> getAllCustomers(){
        return sessionFactory.getCurrentSession().createQuery("from Customer order by id").list();
    }

    public Customer getCustomerById(int Id){
        return (Customer) sessionFactory.getCurrentSession().get(Customer.class, Id);
    }

    public List<Customer> getCustomersByName(String name){
        Query query = createQuery("from Customer where name like :name order by id");
        query.setString("name", "%"+name+"%");
        return query.list();
    }

    public List<Customer> getCustomersByAddress(String address){
        Query query = createQuery("from Customer where address like :address order by id");
        query.setString("address", "%"+address+"%");
        return query.list();
    }

    public List<Customer> getCustomersByPhone(String phone){
        Query query = createQuery("from Customer where phone like :phone order by id");
        query.setString("phone", "%"+phone+"%");
        return query.list();
    }

    public String newCustomer(Customer customer) {
        if (customer.getName() == null) {
            return "Failed to create new customer without name";
        } else {
            customer.setName(customer.getName().toLowerCase());
        }
        if (customer.getAddress() != null) {
            customer.setAddress(customer.getAddress().toLowerCase());
        }
        if (customer.getPhone() != null) {
            customer.setPhone(customer.getPhone().replaceAll(" ",""));
        }
        sessionFactory.getCurrentSession().save(customer);
        return "Successfully create new customer with id " + customer.getId();
    }

    public String updateCustomer(Customer customer) {
        Customer currentCustomer = getCustomerById(customer.getId());
        if (currentCustomer != null) {
            if (customer.getName() != null) {
                currentCustomer.setName(customer.getName().toLowerCase());
            }
            if (customer.getEmail() != null) {
                currentCustomer.setEmail(customer.getEmail());
            }
            if (customer.getPhone() != null) {
                currentCustomer.setPhone(customer.getPhone().replaceAll(" ",""));
            }
            if (customer.getAddress() != null) {
                currentCustomer.setAddress(customer.getAddress().toLowerCase());
            }
            if (customer.getFax() != null) {
                currentCustomer.setFax(customer.getFax());
            }
            if (customer.getContactPerson() != null) {
                currentCustomer.setContactPerson(customer.getContactPerson());
            }
            sessionFactory.getCurrentSession().update(currentCustomer);
            return "Successfully update customer with id " + customer.getId();
        } else {
            return "Failed to update non-existent customer";
        }
    }

    public String deleteCustomer(int id) {
        Customer customer = getCustomerById(id);
        if (customer != null) {
            salesInvoiceService.removeCustomerFromInvoices(id);
            sessionFactory.getCurrentSession().delete(customer);
            return "Successfully delete customer with id " + customer.getId();
        } else {
            return "Failed to delete non-existent customer";
        }
    }
}
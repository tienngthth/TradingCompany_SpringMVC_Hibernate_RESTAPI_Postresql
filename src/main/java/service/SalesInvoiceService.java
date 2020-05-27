package service;

import model.SalesInvoice;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
@Service
public class SalesInvoiceService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private InventoryDeliveryNoteService deliveryNoteService;

    @Autowired
    private SalesInvoiceDetailService invoiceDetailService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<SalesInvoice> getAllInvoices(){
        return createQuery("from SalesInvoice order by id").list();
    }

    public SalesInvoice getInvoiceById(int id){
        return (SalesInvoice) sessionFactory.getCurrentSession().get(SalesInvoice.class, id);
    }

    public List<SalesInvoice> getInvoicesByDate(Date date){
        Query query = createQuery("from SalesInvoice where date = :date order by id");
        query.setDate("date", date);
        return query.list();
    }

    public List<SalesInvoice> getInvoicesByPeriod(Date start, Date end){
        Query query = createQuery("from SalesInvoice " +
                "where date BETWEEN :start AND :end order by date, id");
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }

    public List<SalesInvoice> getInvoicesByASalesStaffInAPeriod(String salesStaffName, Date start, Date end) {
        String queryString = "from SalesInvoice where " +
                                "(date BETWEEN :start AND :end)" +
                                "AND (salesStaffName like :salesStaffName)" +
                                "order by date, id";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        query.setString("salesStaffName", "%" + salesStaffName + "%");
        return query.list();
    }

    public List<SalesInvoice> getInvoicesByACustomerIdInAPeriod(int cId, Date start, Date end) {
        String queryString = "from SalesInvoice where " +
                "(date BETWEEN :start AND :end)" +
                "AND (customer.id = :cId)" +
                "order by date, id";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        query.setInteger("cId", cId) ;
        return query.list();
    }

    public String getRevenueByASalesStaffInAPeriod(String salesStaffName, Date start, Date end) {
        List<SalesInvoice> salesInvoices = getInvoicesByASalesStaffInAPeriod(salesStaffName, start, end);
        return calculateTotalRevenue(salesInvoices);
    }

    public String getRevenueByACustomerInAPeriod(int cId, Date start, Date end) {
        List<SalesInvoice> salesInvoices = getInvoicesByACustomerIdInAPeriod(cId, start, end);
        return calculateTotalRevenue(salesInvoices);
    }

    private String calculateTotalRevenue(List<SalesInvoice> invoices) {
        if (invoices.size() != 0) {
            float revenue = 0;
            for (SalesInvoice invoice : invoices) {
                revenue += invoice.getTotalValue();
            }
            return "Total revenue: " + revenue;
        } else {
            return "No available invoices";
        }
    }

    private List<SalesInvoice> getInvoicesByCustomerId(int cId) {
        String queryString = "from SalesInvoice where customer.id = :cId";
        Query query = createQuery(queryString);
        query.setInteger("cId", cId) ;
        return query.list();
    }

    private List<SalesInvoice> getInvoicesByInventoryDeliveryNoteId(int nId) {
        String queryString = "from SalesInvoice where note.id = :nId";
        Query query = createQuery(queryString);
        query.setInteger("nId", nId) ;
        return query.list();
    }

    public boolean checkDeliveredProducts(int nId) {
        if (getInvoicesByInventoryDeliveryNoteId(nId).size() == 0) {
            return false;
        }
        return true;
    }

    public void removeNoteFromInvoices(int nId) {
        List<SalesInvoice> invoices = getInvoicesByInventoryDeliveryNoteId(nId);
        if (invoices != null) {
            for (SalesInvoice invoice : invoices) {
                invoice.setNote(null);
            }
        }
    }

    public void removeCustomerFromInvoices(int cId) {
        List<SalesInvoice> salesInvoices = getInvoicesByCustomerId(cId);
        if (salesInvoices != null) {
            for (SalesInvoice salesInvoice : salesInvoices) {
                salesInvoice.setCustomer(null);
            }
        }
    }

    public String newInvoice(SalesInvoice invoice) {
        if (invoice.getSalesStaffName() != null) {
            if (staffService.getStaffsByName(invoice.getSalesStaffName().toLowerCase()).size() == 0) {
                return "Failed to create sales invoice with non-existent staff";
            } else {
                invoice.setSalesStaffName(invoice.getSalesStaffName().toLowerCase());
            }
        }
        if (invoice.getCustomer() == null || customerService.getCustomerById(invoice.getCustomer().getId()) == null) {
            return "Failed to create new sales invoice to have non-existent customer or without customer";
        }
        if (invoice.getDate() == null) {
            invoice.setDate(new Date(System.currentTimeMillis()));
        }
        if (invoice.getDetails() != null) {
            invoice.setDetails(null);
        }
        if (invoice.getNote() == null || deliveryNoteService.getNoteById(invoice.getNote().getId()) == null) {
            return "Failed to create sales invoice to have non-existent inventory delivery note" +
                    " or without inventory delivery note";
        } else if (getInvoicesByInventoryDeliveryNoteId(invoice.getNote().getId()).size() == 0) {
            invoice.setNote(deliveryNoteService.getNoteById(invoice.getNote().getId()));
            invoice.autoGenerateInfo();
        } else {
            return "Failed to create sales invoice to have inventory delivery note belonged to another invoice";
        }
        sessionFactory.getCurrentSession().save(invoice);
        return "Successfully create new sales invoice with id " + invoice.getId();
    }

    public String updateInvoice(SalesInvoice invoice) {
        SalesInvoice currentInvoice = getInvoiceById(invoice.getId());
        if (currentInvoice != null) {
            if (invoice.getDate() != null) {
                currentInvoice.setDate(invoice.getDate());
            }
            if (invoice.getSalesStaffName() != null) {
                if (staffService.getStaffsByName(invoice.getSalesStaffName().toLowerCase()).size() == 0) {
                    return "Failed to update sales invoice to have with non-existent staff";
                } else {
                    currentInvoice.setSalesStaffName(invoice.getSalesStaffName().toLowerCase());
                }
            }
            if (invoice.getCustomer() != null) {
                if (customerService.getCustomerById(invoice.getCustomer().getId()) == null) {
                    return "Failed to update sales invoice to have non-existent customer";
                } else {
                    currentInvoice.setCustomer(invoice.getCustomer());
                }
            }
            if (invoice.getDetails() != null) {
                invoice.setDetails(null);
            }
            if (invoice.getNote() != null)  {
                if (deliveryNoteService.getNoteById(invoice.getNote().getId()) == null) {
                    return "Failed to update sales invoice to have non-existent inventory delivery note";
                } else if(getInvoicesByInventoryDeliveryNoteId(invoice.getNote().getId()).size() == 0) {
                    currentInvoice.setNote(deliveryNoteService.getNoteById(invoice.getNote().getId()));
                    invoiceDetailService.removeInvoiceFromInvoiceDetails(currentInvoice.getId());
                    currentInvoice.setDetails(null);
                    currentInvoice.autoGenerateInfo();
                } else if (getInvoicesByInventoryDeliveryNoteId(invoice.getNote().getId()).get(0).getId() != invoice.getId()) {
                    return "Failed to update sales invoice to have inventory delivery note belonged to another invoice";
                }
            }
            sessionFactory.getCurrentSession().update(currentInvoice);
            return "Successfully update sales invoice with id " + currentInvoice.getId();
        } else {
            return "Failed to update non-existent sales invoice";
        }
    }

    public String deleteInvoice(int id) {
        SalesInvoice invoice = getInvoiceById(id);
        if (invoice != null) {
            invoiceDetailService.removeInvoiceFromInvoiceDetails(id);
            sessionFactory.getCurrentSession().delete(invoice);
            return "Successfully delete sales invoice with id " + invoice.getId();
        } else {
            return "Failed to delete non-existent sales invoice";
        }
    }
}
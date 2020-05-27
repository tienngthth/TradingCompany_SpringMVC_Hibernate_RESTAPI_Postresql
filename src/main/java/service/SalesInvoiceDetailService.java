package service;

import model.SalesInvoiceDetail;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@Service
public class SalesInvoiceDetailService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SalesInvoiceService invoiceService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public SalesInvoiceDetail getInvoiceDetailById(int id) {
        return (SalesInvoiceDetail) sessionFactory.getCurrentSession().get(SalesInvoiceDetail.class, id);
    }

    public List<SalesInvoiceDetail> getInvoiceDetailsByInvoiceId(int iId) {
        String queryString = "from SalesInvoiceDetail where invoice.id = :iId";
        Query query = createQuery(queryString);
        query.setInteger("iId", iId) ;
        return query.list();
    }

    public void removeInvoiceFromInvoiceDetails(int iId) {
        List<SalesInvoiceDetail> details = getInvoiceDetailsByInvoiceId(iId);
        for (SalesInvoiceDetail detail : details) {
            detail.setInvoice(null);
        }
    }

    public String updatePriceForDetail(int id, Float price) {
        SalesInvoiceDetail detail = getInvoiceDetailById(id);
        if (detail == null) {
            return "Failed to update price of non-existent detail";
        } else if (price != null) {
            detail.setPrice(price);
            if (detail.getInvoice() != null) {
                detail.getInvoice().setTotalValue(null);
                detail.getInvoice().calculateTotalValue();
                invoiceService.updateInvoice(detail.getInvoice());
            } else {
                sessionFactory.getCurrentSession().update(detail);
            }
        }
        return "Successfully update price of detail with id " + detail.getId();
    }
}

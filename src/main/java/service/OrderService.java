package service;

import model.OrderDetail;
import model.ProviderOrder;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.*;

@Transactional
@Service
public class OrderService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private InventoryReceivingNoteService noteService;

    private Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<ProviderOrder> getAllOrders(){
        return createQuery("from ProviderOrder order by id").list();
    }

    public ProviderOrder getOrderById(int id){
        return (ProviderOrder) sessionFactory.getCurrentSession().get(ProviderOrder.class, id);
    }

    public List<ProviderOrder> getOrdersByDate(Date date){
        Query query = createQuery("from ProviderOrder where date = :date order by id");
        query.setDate("date", date);
        return query.list();
    }

    public List<ProviderOrder> getOrdersByPeriod(Date start, Date end){
        String queryString = "from ProviderOrder where date BETWEEN :start AND :end order by date, id";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }

    private List<ProviderOrder> getOrdersByStaffId(int sId) {
            String queryString = "from ProviderOrder where staff.id = :sId";
            Query query = createQuery(queryString);
            query.setInteger("sId", sId) ;
            return query.list();
    }

    public void removeStaffFromOrders(int sId) {
        List<ProviderOrder> orders = getOrdersByStaffId(sId);
        if (orders != null) {
            for (ProviderOrder order : orders) {
                order.setStaff(null);
            }
        }
    }

    public String newOrder(ProviderOrder order) {
        if (order.getStaff() == null || staffService.getStaffById(order.getStaff().getId()) == null) {
            return "Failed to create order to have non-existent staff or without staff";
        }
        if (order.getProvider() == null || providerService.getProviderById(order.getProvider().getId()) == null) {
            return "Failed to create order to have non-existent provider or without provider";
        }
        if (order.getDate() == null) {
            order.setDate(new Date(System.currentTimeMillis()));
        }
        if (order.getDetails() != null && order.getDetails().size() != 0) {
            for (OrderDetail detail : order.getDetails()) {
                detail.setId(0);
                detail.setOrder(order);
                String returnString = orderDetailService.validateOrderDetail(detail);
                if (returnString != "validated") {
                    return "Failed to create order to have details " + returnString;
                }
            }
        } else {
            return "Failed to create order without any details";
        }
        sessionFactory.getCurrentSession().save(order);
        return "Successfully create new order with id " + order.getId();
    }

    public String updateOrder(ProviderOrder order) {
        String returnString = checkOrder(order);
        if (returnString != "validated") {
            return "Failed to update " + returnString;
        } else {
            ProviderOrder currentOrder = getOrderById(order.getId());
            if (order.getDate() != null) {
                currentOrder.setDate(order.getDate());
            }
            if (order.getStaff() != null) {
                if (staffService.getStaffById(order.getStaff().getId()) == null) {
                    return "Failed to update order to have non-existent staff";
                } else {
                    currentOrder.setStaff(order.getStaff());
                }
            }
            if (order.getProvider() != null) {
                if (providerService.getProviderById(order.getProvider().getId()) == null) {
                    return "Failed to update order to have non-existent provider";
                } else {
                    currentOrder.setProvider(order.getProvider());
                }
            }
            if (order.getDetails() != null) {
                returnString = orderDetailService.updateOrderDetails(currentOrder, order);
                if (returnString != "validated") {
                    return "Failed to update order " + returnString;
                } else {
                    currentOrder.setDetails(order.getDetails());
                }
            }
            sessionFactory.getCurrentSession().merge(currentOrder);
            return "Successfully update order with id " + order.getId();
        }
    }

    private String checkOrder(ProviderOrder order) {
       if (getOrderById(order.getId()) == null) {
            return "non-existent order";
        } else if (noteService.checkReceivedOrder(order.getId())) {
           return "already received order";
       } else {
            return "validated";
        }
    }

    public String deleteOrder(int id) {
        ProviderOrder providerOrder = getOrderById(id);
        if (providerOrder != null) {
            orderDetailService.removeOrderFromOrderDetails(id);
            noteService.removeOrderFromNotes(id);
            sessionFactory.getCurrentSession().delete(providerOrder);
            return "Successfully delete order with id " + providerOrder.getId();
        } else {
            return "Failed to delete non-existent order";
        }
    }
}
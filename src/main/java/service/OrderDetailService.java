package service;

import model.OrderDetail;
import model.ProviderOrder;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class OrderDetailService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ProductService productService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public OrderDetail getOrderDetailById(int id) {
        return (OrderDetail) sessionFactory.getCurrentSession().get(OrderDetail.class, id);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int oId) {
        String queryString = "from OrderDetail where order.id = :oId";
        Query query = createQuery(queryString);
        query.setInteger("oId", oId) ;
        return query.list();
    }

    public String validateOrderDetail(OrderDetail detail) {
        OrderDetail currentDetail = getOrderDetailById(detail.getId());
        if (currentDetail != null) {
            if (detail.getQuantity() != null && detail.getQuantity() < 0) {
                return "without valid quantity";
            }
            if (currentDetail.getOrder() != null &&
                    currentDetail.getOrder().getId() != detail.getOrder().getId()) {
                return "belonged to another order";
            }
            updateOrderDetail(detail);
        } else if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
                return "without valid quantity";
        }
        return setProductToOrderDetail(detail);
    }

    private void updateOrderDetail(OrderDetail detail) {
        OrderDetail currentDetail = getOrderDetailById(detail.getId());
        if (currentDetail != null) {
            if (detail.getQuantity() == null) {
                detail.setQuantity(currentDetail.getQuantity());
            }
            if (detail.getProduct() == null ||
                    (currentDetail.getProduct() != null
                        && currentDetail.getProduct().getId()
                            == detail.getProduct().getId())) {
                if (detail.getPrice() == null) {
                    detail.setPrice(currentDetail.getPrice());
                }
            }
        }
    }

    private String setProductToOrderDetail(OrderDetail detail) {
        OrderDetail currentDetail = getOrderDetailById(detail.getId());
        if (detail.getProduct() == null) {
            if (currentDetail != null && currentDetail.getProduct() != null) {
                detail.setProduct(currentDetail.getProduct());
            } else {
                return "without product";
            }
        } else if (productService.getProductById(detail.getProduct().getId()) != null) {
            detail.setProduct(productService.getProductById(detail.getProduct().getId()));
        } else {
            return "with non-existent product";
        }
        if (detail.getProduct().getSellingPrice() == null) {
            return "with not-for-sell product (missing selling price)";
        }
        detail.autoGeneratePrice();
        return "validated";
    }

    public String updateOrderDetails(ProviderOrder currentOrder, ProviderOrder order) {
        List<Integer> detailIndex = new ArrayList<>();
        Boolean updatedAndNewDetailsAreValidated = false;
        if (currentOrder.getDetails() != null) {
            for (OrderDetail currentDetail : currentOrder.getDetails()) {
                boolean notUpdatedDetailFlag = true;
                for (OrderDetail detail : order.getDetails()) {
                    detail.setOrder(order);
                    if (!updatedAndNewDetailsAreValidated) {
                        if (detail.getId() != 0 && detailIndex.indexOf(detail.getId()) != -1) {
                            return "to have details with the same id";
                        } else {
                            detailIndex.add(detail.getId());
                        }
                        String returnString = validateOrderDetail(detail);
                        if (returnString != "validated") {
                            return "to have details " + returnString;
                        }
                    }
                    if (currentDetail.getId() == detail.getId()) {
                        notUpdatedDetailFlag = false;
                        if (updatedAndNewDetailsAreValidated) {
                            break;
                        }
                    }
                }
                updatedAndNewDetailsAreValidated = true;
                if (notUpdatedDetailFlag == true) {
                    order.getDetails().add(currentDetail);
                }
            }
        }
        return "validated";
    }

    public void removeOrderFromOrderDetails(int oId) {
        List<OrderDetail> details = getOrderDetailsByOrderId(oId);
        for (OrderDetail detail : details) {
            detail.setOrder(null);
        }
    }
}

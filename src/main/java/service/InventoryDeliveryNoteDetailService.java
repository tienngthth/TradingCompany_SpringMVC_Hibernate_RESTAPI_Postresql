package service;

import model.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class InventoryDeliveryNoteDetailService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public InventoryDeliveryNoteDetail getNoteDetailById(int id) {
        return (InventoryDeliveryNoteDetail) sessionFactory.getCurrentSession().get(InventoryDeliveryNoteDetail.class, id);
    }

    public List<InventoryDeliveryNoteDetail> getNoteDetailsByNoteId(int nId) {
        String queryString = "from InventoryDeliveryNoteDetail where note.id = :nId";
        Query query = createQuery(queryString);
        query.setInteger("nId", nId) ;
        return query.list();
    }

    public String validateNoteDetail(InventoryDeliveryNoteDetail detail) {
        InventoryDeliveryNoteDetail currentDetail = getNoteDetailById(detail.getId());
        if (currentDetail != null) {
            if (currentDetail.getNote() != null &&
                currentDetail.getNote().getId() != detail.getNote().getId()) {
                return "belonged to another note";
            }
        }
        return setProductToNoteDetail(detail);
    }

    private String setProductToNoteDetail(InventoryDeliveryNoteDetail detail) {
        InventoryDeliveryNoteDetail currentDetail = getNoteDetailById(detail.getId());
        if (currentDetail == null) {
            if (detail.getProduct() == null || productService.getProductById(detail.getProduct().getId()) == null) {
                return "without existent product";
            }
        } else {
            if (detail.getProduct() == null) {
                detail.setProduct(currentDetail.getProduct());
            } else if (currentDetail.getProduct().getId() != detail.getProduct().getId()) {
                return "changing products";
            }
        }
        return checkQuantity(detail);
    }

    private String checkQuantity(InventoryDeliveryNoteDetail detail) {
        InventoryDeliveryNoteDetail currentDetail = getNoteDetailById(detail.getId());
        int previousQuantity = 0;
        if (currentDetail == null) {
            if (detail.getQuantity() == null || detail.getQuantity() <= 0)
            return "without valid quantity";
        } else {
            if (detail.getQuantity() != null) {
                if (detail.getQuantity() < 0) {
                    return "without valid quantity";
                }
                previousQuantity = currentDetail.getQuantity();
            }
        }
        int initialStock = productService.getProductById(detail.getProduct().getId()).getInitialStock();
        String pName = productService.getProductById(detail.getProduct().getId()).getName();
        if (!inventoryService.checkStock(
                pName, initialStock, previousQuantity, detail.getQuantity())) {
            return "with shortage of product";
        }
        return "validated";
    }

    public String updateNoteDetails(InventoryDeliveryNote currentNote, InventoryDeliveryNote note) {
        Boolean updatedAndNewDetailsAreValidated = false;
        List<Integer> productIndex = new ArrayList<>();
        if (currentNote.getDetails() != null) {
            for (InventoryDeliveryNoteDetail currentDetail : currentNote.getDetails()) {
                boolean notUpdatedDetailFlag = true;
                for (InventoryDeliveryNoteDetail detail : note.getDetails()) {
                    detail.setNote(note);
                    if (!updatedAndNewDetailsAreValidated) {
                        String returnString = validateNoteDetail(detail);
                        if (returnString != "validated") {
                            return "to have details " + returnString;
                        }
                        if (productIndex.indexOf(detail.getProduct().getId()) != -1) {
                            return "to have details with the same product or missing detail id";
                        } else {
                            productIndex.add(detail.getProduct().getId());
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
                    if (productIndex.indexOf(currentDetail.getProduct().getId()) != -1) {
                        return "to have details with the same product";
                    } else {
                        productIndex.add(currentDetail.getProduct().getId());
                    }
                    note.getDetails().add(currentDetail);
                }
            }
        }
        return "validated";
    }

    public void removeNoteFromNoteDetails(int nId) {
        List<InventoryDeliveryNoteDetail> details = getNoteDetailsByNoteId(nId);
        for (InventoryDeliveryNoteDetail detail : details) {
            detail.setNote(null);
        }
    }

    public List<InventoryDeliveryNoteDetail> getAllDetails() {
        String queryString = "from InventoryDeliveryNoteDetail";
        Query query = createQuery(queryString);
        return query.list();
    }

    public List<InventoryDeliveryNoteDetail> getDetailsByPeriod(Date start, Date end) {
        String queryString = "from InventoryDeliveryNoteDetail where note.date BETWEEN :start AND :end";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }
}

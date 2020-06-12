package service;

import model.InventoryDeliveryNote;
import model.InventoryDeliveryNoteDetail;
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
public class InventoryDeliveryNoteService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private StaffService staffService;

    @Autowired
    private InventoryDeliveryNoteDetailService noteDetailService;

    @Autowired
    private SalesInvoiceService invoiceService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<InventoryDeliveryNote> getAllNotes(){
        return createQuery("from InventoryDeliveryNote order by id").list();
    }

    public InventoryDeliveryNote getNoteById(int id){
        return (InventoryDeliveryNote) sessionFactory.getCurrentSession().get(InventoryDeliveryNote.class, id);
    }

    public List<InventoryDeliveryNote> getNotesByDate(Date date){
        Query query = createQuery("from InventoryDeliveryNote where date = :date order by id");
        query.setDate("date", date);
        return query.list();
    }

    public List<InventoryDeliveryNote> getINotesByPeriod(Date start, Date end){
        String queryString = "from InventoryDeliveryNote where date BETWEEN :start AND :end order by date, id";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }

    private List<InventoryDeliveryNote> getNotesByStaffId(int sId) {
        String queryString = "from InventoryDeliveryNote where staff.id = :sId";
        Query query = createQuery(queryString);
        query.setInteger("sId", sId) ;
        return query.list();
    }

    public void removeStaffFromNotes(int sId) {
        List<InventoryDeliveryNote> inventoryDeliveryNotes = getNotesByStaffId(sId);
        if (inventoryDeliveryNotes != null) {
            for (InventoryDeliveryNote inventoryDeliveryNote : inventoryDeliveryNotes) {
                inventoryDeliveryNote.setStaff(null);
            }
        }
    }

    public String newNote(InventoryDeliveryNote note) {
        if (note.getStaff() == null || staffService.getStaffById(note.getStaff().getId()) == null) {
            return "Failed to create inventory delivery note to have non-existent staff or without staff";
        }
        if (note.getDate() == null) {
            note.setDate(new Date(System.currentTimeMillis()));
        }
        if (note.getDetails() != null && note.getDetails().size() != 0) {
            List<Integer> productIndex = new ArrayList<>();
            for (InventoryDeliveryNoteDetail detail : note.getDetails()) {
                detail.setId(0);
                detail.setNote(note);
                String returnString = noteDetailService.validateNoteDetail(detail);
                if (returnString != "validated") {
                    return "Failed to create inventory delivery note to have details " + returnString;
                }
                if (productIndex.indexOf(detail.getProduct().getId()) != -1) {
                    return "Failed to create inventory delivery note to have details with the same product";
                } else {
                    productIndex.add(detail.getProduct().getId());
                }
            }
        } else {
            return "Failed to create inventory delivery note without any details";
        }
        sessionFactory.getCurrentSession().save(note);
        return "Successfully create new inventory delivery note";
    }

    public String updateNote(InventoryDeliveryNote note) {
        String returnString = checkNote(note);
        if (returnString != "validated") {
            return "Failed to update " + returnString;
        } else {
            InventoryDeliveryNote currentNote = getNoteById(note.getId());
            if (note.getDate() != null) {
                currentNote.setDate(note.getDate());
            }
            if (note.getStaff() != null) {
                if (staffService.getStaffById(note.getStaff().getId()) == null) {
                    return "Failed to update inventory delivery note to have non-existent staff";
                } else {
                    currentNote.setStaff(note.getStaff());
                }
            }
            if (note.getDetails() != null) {
                returnString = noteDetailService.updateNoteDetails(currentNote, note);
                if (returnString != "validated") {
                    return "Failed to update inventory delivery note " + returnString;
                } else {
                    currentNote.setDetails(note.getDetails());
                }
            }
            sessionFactory.getCurrentSession().merge(currentNote);
            return "Successfully update inventory delivery note";
        }
    }

    private String checkNote(InventoryDeliveryNote note) {
        if (getNoteById(note.getId()) == null) {
            return "non-existent inventory delivery note";
        } else if (invoiceService.checkDeliveredProducts(note.getId())) {
            return "already delivered inventory note";
        } else {
            return "validated";
        }
    }

    public String deleteNote(int id) {
        InventoryDeliveryNote note = getNoteById(id);
        if (note != null) {
            invoiceService.removeNoteFromInvoices(id);
            noteDetailService.removeNoteFromNoteDetails(id);
            sessionFactory.getCurrentSession().delete(note);
            return "Successfully delete inventory delivery note";
        } else {
            return "Failed to delete non-existent inventory delivery note";
        }
    }
}
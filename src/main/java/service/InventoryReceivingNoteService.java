package service;

import model.InventoryReceivingNote;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.List;

@Transactional
@Service
public class InventoryReceivingNoteService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private StaffService staffService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryReceivingNoteDetailService noteDetailService;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<InventoryReceivingNote> getAllNotes(){
        return createQuery("from InventoryReceivingNote order by id").list();
    }

    public InventoryReceivingNote getNoteById(int id){
        return (InventoryReceivingNote) sessionFactory.getCurrentSession().get(InventoryReceivingNote.class, id);
    }

    public List<InventoryReceivingNote> getNotesByDate(Date date){
        Query query = createQuery("from InventoryReceivingNote where date = :date order by id");
        query.setDate("date", date);
        return query.list();
    }

    public List<InventoryReceivingNote> getNotesByPeriod(Date start, Date end){
        Query query = createQuery("from InventoryReceivingNote " +
                "where date BETWEEN :start AND :end order by date, id");
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }

    private List<InventoryReceivingNote> getNotesByStaffId(int sId) {
        String queryString = "from InventoryReceivingNote where staff.id = :sId";
        Query query = createQuery(queryString);
        query.setInteger("sId", sId) ;
        return query.list();
    }

    public void removeStaffFromNotes(int sId) {
        List<InventoryReceivingNote> notes = getNotesByStaffId(sId);
        if (notes != null) {
            for (InventoryReceivingNote note : notes) {
                note.setStaff(null);
            }
        }
    }

    private List<InventoryReceivingNote> getNotesByOrderId(int oId) {
        String queryString = "from InventoryReceivingNote where order.id = :oId";
        Query query = createQuery(queryString);
        query.setInteger("oId", oId) ;
        return query.list();
    }

    public boolean checkReceivedOrder(int oId) {
        if (getNotesByOrderId(oId).size() == 0) {
            return false;
        }
        return true;
    }

    public void removeOrderFromNotes(int sId) {
        List<InventoryReceivingNote> notes = getNotesByOrderId(sId);
        if (notes != null) {
            for (InventoryReceivingNote note : notes) {
                note.setOrder(null);
            }
        }
    }

    public String newNote(InventoryReceivingNote note) {
        if (note.getStaff() != null && staffService.getStaffById(note.getStaff().getId()) == null) {
            return "Failed to create inventory receiving note to have non-existent staff";
        }
        if (note.getDate() == null) {
            note.setDate(new Date(System.currentTimeMillis()));
        }
        if (note.getDetails() != null) {
            note.setDetails(null);
        }
        if (note.getOrder() == null || orderService.getOrderById(note.getOrder().getId()) == null) {
            return "Failed to create inventory receiving note to have non-existent order or without order";
        } else if (getNotesByOrderId(note.getOrder().getId()).size() == 0) {
            note.setOrder(orderService.getOrderById(note.getOrder().getId()));
            note.autoGenerateInfo();
        } else {
            return "Failed to create inventory receiving note to have order belonged to another note";
        }
        sessionFactory.getCurrentSession().save(note);
        return "Successfully create new inventory receiving note with id " + note.getId();
    }

    public String updateNote(InventoryReceivingNote note) {
        InventoryReceivingNote currentNote = getNoteById(note.getId());
        if (currentNote != null) {
            if (note.getDate() != null) {
                currentNote.setDate(note.getDate());
            }
            if (note.getStaff() != null) {
                if (staffService.getStaffById(note.getStaff().getId()) == null) {
                    return "Failed to update inventory receiving note to have non-existent staff";
                } else {
                    currentNote.setStaff(note.getStaff());
                }
            }
            if (note.getDetails() != null) {
                note.setDetails(null);
            }
            if (note.getOrder() != null)  {
                if (orderService.getOrderById(note.getOrder().getId()) == null) {
                    return "Failed to update inventory receiving note to have non-existent order";
                } else if (getNotesByOrderId(note.getOrder().getId()).size() == 0){
                    currentNote.setOrder(orderService.getOrderById(note.getOrder().getId()));
                    noteDetailService.removeNoteFromNoteDetails(currentNote.getId());
                    currentNote.setDetails(null);
                    currentNote.autoGenerateInfo();
                } else if (getNotesByOrderId(note.getOrder().getId()).get(0).getId() != note.getId()) {
                    return "Failed to update inventory receiving note to have order belonged to another note";
                }
            }
            sessionFactory.getCurrentSession().update(currentNote);
            return "Successfully update inventory receiving note with id " + note.getId();
        } else {
            return "Failed to update non-existent inventory receiving note";
        }
    }

    public String deleteNote(int id) {
        InventoryReceivingNote note = getNoteById(id);
        if (note != null) {
            noteDetailService.removeNoteFromNoteDetails(id);
            sessionFactory.getCurrentSession().delete(note);
            return "Successfully delete inventory receiving note with id " + note.getId();
        } else {
            return "Failed to delete non-existent inventory receiving note";
        }
    }
}
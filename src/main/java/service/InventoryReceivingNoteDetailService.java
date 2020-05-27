package service;

import model.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Transactional
@Service
public class InventoryReceivingNoteDetailService {
    @Autowired
    private SessionFactory sessionFactory;

    public Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<InventoryReceivingNoteDetail> getAllDetails() {
        return createQuery("from InventoryReceivingNoteDetail").list();
    }

    public List<InventoryReceivingNoteDetail> getNoteDetailsByNoteId(int nId) {
        String queryString = "from InventoryReceivingNoteDetail where note.id = :nId";
        Query query = createQuery(queryString);
        query.setInteger("nId", nId) ;
        return query.list();
    }

    public void removeNoteFromNoteDetails(int nId) {
        List<InventoryReceivingNoteDetail> details = getNoteDetailsByNoteId(nId);
        for (InventoryReceivingNoteDetail detail : details) {
            detail.setNote(null);
        }
    }

    public List<InventoryReceivingNoteDetail> getDetailsByPeriod(Date start, Date end) {
        String queryString = "from InventoryReceivingNoteDetail where note.date BETWEEN :start AND :end";
        Query query = createQuery(queryString);
        query.setDate("start", start);
        query.setDate("end", end);
        return query.list();
    }
}

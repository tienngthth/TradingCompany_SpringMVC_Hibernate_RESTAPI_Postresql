package service;

import model.Staff;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@Service
public class StaffService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private InventoryReceivingNoteService inventoryReceivingNoteService;

    @Autowired
    private InventoryDeliveryNoteService inventoryDeliveryNoteService;

    @Autowired
    private OrderService orderService;

    private Query createQuery(String stringQuery) {
        return sessionFactory.getCurrentSession().createQuery(stringQuery);
    }

    public List<Staff> getAllStaffs(){
        return createQuery("from Staff order by id").list();
    }

    public Staff getStaffById(int id){
        return (Staff) this.sessionFactory.getCurrentSession().get(Staff.class, id);
    }

    public List<Staff> getStaffsByName(String name){
        Query query = createQuery("from Staff where name like :name order by id");
        query.setString("name", "%"+name+"%");
        return query.list();
    }

    public List<Staff> getStaffsByAddress(String address){
        Query query = createQuery("from Staff where address like :address order by id");
        query.setString("address", "%"+address+"%");
        return query.list();
    }

    public List<Staff> getStaffsByPhone(String phone){
        Query query = createQuery("from Staff where phone like :phone order by id");
        query.setString("phone", "%"+phone+"%");
        return query.list();
    }

    public String newStaff(Staff staff) {
        if (staff.getName() == null) {
            return "Failed to create new staff without name";
        } else {
            staff.setName(staff.getName().toLowerCase().replaceAll(" ",""));
        }
        if (staff.getAddress() != null) {
            staff.setAddress(staff.getAddress().toLowerCase().replaceAll(" ",""));
        }
        if (staff.getPhone() != null) {
            staff.setPhone(staff.getPhone().replaceAll(" ",""));
        }
        sessionFactory.getCurrentSession().save(staff);
        return "Successfully create new staff";
    }

    public String updateStaff(Staff staff) {
        Staff currentStaff = getStaffById(staff.getId());
        if (currentStaff != null) {
            if (staff.getName() != null) {
                currentStaff.setName(staff.getName().toLowerCase().replaceAll(" ",""));
            }
            if (staff.getEmail() != null) {
                currentStaff.setEmail(staff.getEmail());
            }
            if (staff.getPhone() != null) {
                currentStaff.setPhone(staff.getPhone().replaceAll(" ",""));
            }
            if (staff.getAddress() != null) {
                currentStaff.setAddress(staff.getAddress().toLowerCase().replaceAll(" ",""));
            }
            sessionFactory.getCurrentSession().update(currentStaff);
            return "Successfully update staff";
        } else {
            return "Failed to update non-existent staff";
        }
    }

    public String deleteStaff(int id){
        Staff staff = getStaffById(id);
        if (staff != null) {
            orderService.removeStaffFromOrders(id);
            inventoryReceivingNoteService.removeStaffFromNotes(id);
            inventoryDeliveryNoteService.removeStaffFromNotes(id);
            sessionFactory.getCurrentSession().delete(staff);
            return "Successfully delete staff";
        } else {
            return "Failed to delete non-existent staff";
        }
    }
}
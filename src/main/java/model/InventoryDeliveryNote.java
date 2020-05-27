package model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "inventoryDeliveryNote")
public class InventoryDeliveryNote {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
    @OneToMany (fetch = FetchType.EAGER, mappedBy = "note")
    private List<InventoryDeliveryNoteDetail> details;

    public List<InventoryDeliveryNoteDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InventoryDeliveryNoteDetail> details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}

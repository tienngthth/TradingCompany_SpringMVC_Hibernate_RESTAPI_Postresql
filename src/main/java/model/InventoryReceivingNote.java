package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventoryReceivingNote")
public class InventoryReceivingNote {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
    @OneToMany (fetch = FetchType.EAGER, mappedBy = "note")
    private List<InventoryReceivingNoteDetail> details;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private ProviderOrder order;

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

    @JsonIgnore
    public ProviderOrder getOrder() {
        return order;
    }

    @JsonProperty
    public void setOrder(ProviderOrder order) {
        this.order = order;
    }

    public void setDetails(List<InventoryReceivingNoteDetail> details) {
        this.details = details;
    }

    public List<InventoryReceivingNoteDetail> getDetails() {
        return details;
    }

    public void autoGenerateInfo() {
        if (order != null) {
            if (staff == null) {
                setStaff(order.getStaff());
            }
            if (details == null && order.getDetails() != null) {
                details = new ArrayList<>();
                for (OrderDetail orderDetail : order.getDetails()) {
                    InventoryReceivingNoteDetail noteDetail = new InventoryReceivingNoteDetail();
                    noteDetail.setNote(this);
                    noteDetail.setProduct(orderDetail.getProduct());
                    noteDetail.setQuantity(orderDetail.getQuantity());
                    details.add(noteDetail);
                }
            }
        }
    }
}
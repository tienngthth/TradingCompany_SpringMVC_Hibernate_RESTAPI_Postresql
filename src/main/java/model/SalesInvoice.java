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
@Table(name = "salesInvoice")
public class SalesInvoice {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Date date;

    @Column
    private String salesStaffName;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column
    @JsonIgnore
    private Float totalValue;

    @Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
    @OneToMany (fetch = FetchType.EAGER, mappedBy = "invoice")
    private List<SalesInvoiceDetail> details;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "delivery_note_id")
    private InventoryDeliveryNote note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSalesStaffName() {
        return salesStaffName;
    }

    public void setSalesStaffName(String salesStaffName) {
        this.salesStaffName = salesStaffName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @JsonProperty
    public Float getTotalValue() {
        return totalValue;
    }

    @JsonIgnore
    public void setTotalValue(Float totalValue) {
        this.totalValue = totalValue;
    }

    @JsonIgnore
    public InventoryDeliveryNote getNote() {
        return note;
    }

    @JsonProperty
    public void setNote(InventoryDeliveryNote note) {
        this.note = note;
    }

    public List<SalesInvoiceDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SalesInvoiceDetail> details) {
        this.details = details;
    }

    private void getDataFromNote() {
        if (note != null) {
            if (salesStaffName == null && note.getStaff() != null) {
                setSalesStaffName(note.getStaff().getName());
            }
            if (details == null && note.getDetails() != null) {
                details = new ArrayList<>();
                for (InventoryDeliveryNoteDetail noteDetail : note.getDetails()) {
                    SalesInvoiceDetail invoiceDetail = new SalesInvoiceDetail();
                    invoiceDetail.setInvoice(this);
                    invoiceDetail.setProduct(noteDetail.getProduct());
                    invoiceDetail.autoGeneratePrice();
                    invoiceDetail.setQuantity(noteDetail.getQuantity());
                    details.add(invoiceDetail);
                }
            }
        }
    }

    public void calculateTotalValue() {
        if (details != null) {
            float originalTotalValue = 0;
            for (SalesInvoiceDetail detail : details) {
                originalTotalValue += detail.calculateTotalPrice();
            }
            if (totalValue == null) {
                totalValue = originalTotalValue;
            }
        }
    }

    public void autoGenerateInfo() {
        getDataFromNote();
        calculateTotalValue();
    }
}
package model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "salesInvoiceDetail")
public class SalesInvoiceDetail {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column
    private Integer quantity;

    @Column
    private Float price;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "invoice_id")
    private SalesInvoice invoice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public SalesInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(SalesInvoice invoice) {
        this.invoice = invoice;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float calculateTotalPrice() {
        if (price != null) {
            return price * quantity;
        } else {
            return Float.valueOf(0);
        }
    }

    public void autoGeneratePrice() {
        if (price == null && product != null && product.getSellingPrice() != null) {
            price = product.getSellingPrice();
        } else {
            price = Float.valueOf(0);
        }
    }
}
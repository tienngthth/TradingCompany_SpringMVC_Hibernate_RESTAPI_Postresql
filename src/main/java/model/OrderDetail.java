package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

/*
    No REST API is created to directly create, update, display, delete any records from this entity.
    The system won't delete any records of this entity by any cases when working with others REST APIs.
    The record is displayed, updated, created via ProviderOrder entity's records
    When the record is displayed via ProviderOrder entity's records only id , product, quantity and price are displayed.
*/
@Entity
@Table(name = "orderDetail")
public class OrderDetail {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column (nullable = false)
    private Integer quantity;

    @Column
    private Float price;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private ProviderOrder order;

    public ProviderOrder getOrder() {
        return order;
    }

    public void setOrder(ProviderOrder order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void autoGeneratePrice() {
        if (price == null && product.getSellingPrice() != null && product != null) {
            setPrice(product.getSellingPrice());
        }
    }
}

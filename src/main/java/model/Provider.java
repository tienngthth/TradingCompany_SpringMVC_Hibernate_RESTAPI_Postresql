package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

/*
    For this entity, it is assumed that all records are created and updated before.
    No REST API is created to directly create, update, display, delete any records from this entity.
    When working with others REST APIs, the system won't delete, update, create any records by any cases.
    When the record is displayed via ProviderOrder entity's records, only id and name are displayed.
*/
@Entity
@Table(name = "provider")
public class Provider {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    @JsonIgnore
    private String address, phone, fax, email, contactPerson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
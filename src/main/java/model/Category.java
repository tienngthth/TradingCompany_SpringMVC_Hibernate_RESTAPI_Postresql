package model;
import javax.persistence.*;

/*
    For this entity, it is assumed that all records are created and updated before.
    No REST API is created to directly create, update, display, delete any records from this entity.
    When working with others REST APIs, the system won't delete, update, create any records by any cases.
*/
@Entity
@Table(name = "category")
public class Category {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (unique = true)
    private String name;

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
}
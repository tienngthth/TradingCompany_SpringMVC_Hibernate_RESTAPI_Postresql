package service;

import model.Product;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ProductService {
    @Autowired
    private SessionFactory sessionFactory;

    public Product getProductById(int id) {
        return (Product) sessionFactory.getCurrentSession().get(Product.class, id);
    }

    public Product getProductByName(String name){
        Query query = sessionFactory.getCurrentSession().createQuery("from Product where name like :name");
        query.setString("name", "%"+name+"%");
        if (query.list().size() != 0) {
            return (Product) query.list().get(0);
        } else {
            return null;
        }
    }

    public void newProduct(Product product) {
        if (product.getName() != null && getProductByName(product.getName()) == null) {
            this.sessionFactory.getCurrentSession().save(product);
        }
    }

    public List<Product> getAllProducts() {
        return this.sessionFactory.getCurrentSession().createQuery("from Product").list();
    }
}

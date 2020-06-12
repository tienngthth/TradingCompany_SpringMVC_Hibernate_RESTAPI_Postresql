package service;

import model.Category;
import model.Provider;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CategoryService {
    @Autowired
    private SessionFactory sessionFactory;

    public Category getCategoryById(int Id){
        return (Category) this.sessionFactory.getCurrentSession().get(Category.class, Id);
    }

    public String newCategory(Category category) {
        if (category.getName() == null) {
            return "Failed to create new category without name";
        }
        this.sessionFactory.getCurrentSession().save(category);
        return "Successfully create new category";
    }

    public List<Category> getAllCategories() {
        return this.sessionFactory.getCurrentSession().createQuery("from Category").list();
    }
}

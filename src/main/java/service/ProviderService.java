package service;

import model.Provider;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ProviderService {
    @Autowired
    private SessionFactory sessionFactory;

    public Provider getProviderById(int Id){
        return (Provider) this.sessionFactory.getCurrentSession().get(Provider.class, Id);
    }

    public String newProvider(Provider provider) {
        if (provider.getName() == null) {
            return "Failed to create new provider without name";
        }
        this.sessionFactory.getCurrentSession().save(provider);
        return "Successfully create new provider";
    }

    public List<Provider> getAllProviders() {
        return this.sessionFactory.getCurrentSession().createQuery("from Provider").list();
    }
}

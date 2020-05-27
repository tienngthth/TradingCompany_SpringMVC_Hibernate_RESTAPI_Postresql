import config.AppConfig;
import model.Customer;
import model.Product;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.CustomerService;

/**
 * Created by CoT on 7/29/18.
 */
public class Main {

    public static void main(String[] args){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.refresh();
    }
}

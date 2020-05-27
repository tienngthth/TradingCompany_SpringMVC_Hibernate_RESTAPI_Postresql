package controller;

import model.Product;
import model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ProductService;
import service.ProviderService;
import java.util.List;

@RestController
@RequestMapping(path = "/")
public class SupportController {
    @Autowired
    private ProviderService providerService;

    @Autowired
    private ProductService productService;

    @RequestMapping(path = "/providers/create", method = RequestMethod.POST)
    public void newProvider(@RequestBody Provider provider){
        providerService.newProvider(provider);
    }

    @RequestMapping(path = "/providers/read/all", method = RequestMethod.GET)
    public List<Provider> getAllProvider(){
        return providerService.getAllProviders();
    }

    @RequestMapping(path = "/products/create", method = RequestMethod.POST)
    public void newProduct(@RequestBody Product product){
        productService.newProduct(product);
    }

    @RequestMapping(path = "/products/read/all", method = RequestMethod.GET)
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
}

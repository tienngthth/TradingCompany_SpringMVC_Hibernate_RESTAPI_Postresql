package controller;

import model.Category;
import model.Product;
import model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.CategoryService;
import service.ProductService;
import service.ProviderService;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/")
public class SupportController {
    @Autowired
    private ProviderService providerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(path = "/providers/create", method = RequestMethod.POST)
    public String newProvider(@RequestBody Provider provider){
        return providerService.newProvider(provider);
    }

    @RequestMapping(path = "/providers/read/all", method = RequestMethod.GET)
    public List<Provider> getAllProvider(){
        return providerService.getAllProviders();
    }

    @RequestMapping(path = "/products/create", method = RequestMethod.POST)
    public String newProduct(@RequestBody Product product){
        return productService.newProduct(product);
    }

    @RequestMapping(path = "/products/read/all", method = RequestMethod.GET)
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @RequestMapping(path = "/categories/create", method = RequestMethod.POST)
    public String newCategory(@RequestBody Category category){
        return categoryService.newCategory(category);
    }

    @RequestMapping(path = "/categories/read/all", method = RequestMethod.GET)
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    public static int[] getIndices(int listSize, int page) {
        if (page == 0) {
            return new int[] {0, listSize};
        }
        int firstIndex = (page - 1) * 5;
        if (listSize < firstIndex || page <= 0) {
            return new int[] {0, 0};
        }
        int lastIndex = firstIndex + 5;
        if (listSize < lastIndex) {
            lastIndex = listSize;
        }
        return new int[] {firstIndex, lastIndex};
    }
}

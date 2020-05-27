package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.InventoryService;
import java.sql.Date;

@RestController
@RequestMapping(path = "/")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @RequestMapping(path = "/inventory/read/period", method = RequestMethod.GET)
    public String getProductInventoryInAPeriod(@RequestParam Date start, @RequestParam Date end){
        return inventoryService.getProductInventoryInAPeriod(start, end);
    }
}
package controller;

import model.ProviderOrder;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.OrderService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<ProviderOrder> getAllOrders(@RequestParam int page){
        return paginatedDisplay(orderService.getAllOrders(), page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public ProviderOrder getOrderById(@RequestParam int id){
           return orderService.getOrderById(id);
    }

    @RequestMapping(path = "/read/date", method = RequestMethod.GET)
    public List<ProviderOrder> getOrdersByDate(@RequestParam Date date, @RequestParam int page){
        return paginatedDisplay(orderService.getOrdersByDate(date), page);
    }

    @RequestMapping(path = "/read/period", method = RequestMethod.GET)
    public List<ProviderOrder> getOrdersByPeriod(@RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(orderService.getOrdersByPeriod(start, end), page);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newOrder(@RequestBody ProviderOrder order){
        return orderService.newOrder(order);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateOrder(@RequestBody ProviderOrder order){
        return orderService.updateOrder(order);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteOrder(@RequestParam int id){
        return orderService.deleteOrder(id);
    }

    public List<ProviderOrder> paginatedDisplay(List<ProviderOrder> orders, int page) {
        if (page == -1) {
            return orders;
        }
        int firstIndex = (page - 1) * 5;
        if (orders.size() < firstIndex || page <= 0) {
            return new ArrayList<>();
        }
        int lastIndex = firstIndex + 5;
        if (orders.size() < lastIndex) {
            lastIndex = orders.size();
        }
        return orders.subList(firstIndex, lastIndex);
    }
}

package controller;

import model.SalesInvoice;
import model.SalesInvoiceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.SalesInvoiceDetailService;
import service.SalesInvoiceService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/salesInvoices")
public class SalesInvoiceController {
    @Autowired
    private SalesInvoiceService invoiceService;

    @Autowired
    private SalesInvoiceDetailService detailService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<SalesInvoice> getAllSalesInvoices(@RequestParam int page){
        return paginatedDisplay(invoiceService.getAllInvoices(), page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public SalesInvoice getSalesInvoiceById(@RequestParam int id){
        return invoiceService.getInvoiceById(id);
    }

    @RequestMapping(path = "/read/date", method = RequestMethod.GET)
    public List<SalesInvoice> getSalesInvoicesByDate(@RequestParam Date date, @RequestParam int page){
        return paginatedDisplay(invoiceService.getInvoicesByDate(date), page);
    }

    @RequestMapping(path = "/read/period", method = RequestMethod.GET)
    public List<SalesInvoice> getSalesInvoicesByPeriod(@RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(invoiceService.getInvoicesByPeriod(start, end), page);
    }

    @RequestMapping(path = "/read/staff-period", method = RequestMethod.GET)
    public List<SalesInvoice> getSalesInvoicesByASalesStaffInAPeriod(@RequestParam String salesStaffName, @RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(invoiceService.getInvoicesByASalesStaffInAPeriod(salesStaffName.toLowerCase(), start, end), page);
    }

    @RequestMapping(path = "/read/customer-period", method = RequestMethod.GET)
    public List<SalesInvoice> getSalesInvoicesByACustomerIdInAPeriod(@RequestParam int cId, @RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(invoiceService.getInvoicesByACustomerIdInAPeriod(cId, start, end), page);
    }

    @RequestMapping(path = "/revenue/read/staff-period", method = RequestMethod.GET)
    public String getRevenueByASalesStaffInAPeriod(@RequestParam String salesStaffName, @RequestParam Date start, @RequestParam Date end){
        return invoiceService.getRevenueByASalesStaffInAPeriod(salesStaffName.toLowerCase(), start, end);
    }

    @RequestMapping(path = "/revenue/read/customer-period", method = RequestMethod.GET)
    public String getRevenueByACustomerInAPeriod(@RequestParam int cId, @RequestParam Date start, @RequestParam Date end){
        return invoiceService.getRevenueByACustomerInAPeriod(cId, start, end);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newInvoice(@RequestBody SalesInvoice invoice){
        return invoiceService.newInvoice(invoice);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateInvoice(@RequestBody SalesInvoice invoice){
        return invoiceService.updateInvoice(invoice);
    }

    @RequestMapping(path = "/update/detail/price", method = RequestMethod.POST)
    public String updatePriceForDetail(@RequestParam int id, @RequestParam Float price){
        return detailService.updatePriceForDetail(id, price);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteInvoice(@RequestParam int id){
        return invoiceService.deleteInvoice(id);
    }

    public List<SalesInvoice> paginatedDisplay(List<SalesInvoice> invoices, int page) {
        int[] indices = SupportController.getIndices(invoices.size(), page);
        return invoices.subList(indices[0], indices[1]);
    }
}

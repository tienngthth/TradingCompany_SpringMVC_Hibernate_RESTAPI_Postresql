package service;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class InventoryService {
    private List<String> productNames = new ArrayList<>();
    private List<int[]> inventory = new ArrayList<>();

    @Autowired
    private InventoryDeliveryNoteDetailService deliveredDetailService;

    @Autowired
    private InventoryReceivingNoteDetailService receivedDetailService;

    @Autowired
    private ProductService productService;

    public String getProductInventoryInAPeriod(Date start, Date end) {
        List<InventoryReceivingNoteDetail> receivedDetails = receivedDetailService.getDetailsByPeriod(start, end);
        List<InventoryDeliveryNoteDetail> deliveredDetails = deliveredDetailService.getDetailsByPeriod(start, end);
        checkProductInventory(receivedDetails, deliveredDetails);
        return getOutPutString(productNames, inventory, start, end);
    }

    private void checkProductInventory(List<InventoryReceivingNoteDetail> receivedDetails, List<InventoryDeliveryNoteDetail> deliveredDetails) {
        productNames = new ArrayList<>();
        inventory = new ArrayList<>();
        checkReceivedInventoryValue(receivedDetails);
        checkDeliveryAndBalanceInventoryValue(deliveredDetails);
    }

    public void checkReceivedInventoryValue(List<InventoryReceivingNoteDetail> details) {
        int index;
        for (InventoryReceivingNoteDetail detail : details) {
            if (productNames.indexOf(detail.getProduct().getName()) == -1) {
                productNames.add(detail.getProduct().getName());
                inventory.add(new int[]{0, 0, 0});
            }
            index = productNames.indexOf(detail.getProduct().getName());
            inventory.get(index)[0] += detail.getQuantity();
            inventory.get(index)[2] += detail.getQuantity();

        }
    }

    public void checkDeliveryAndBalanceInventoryValue(List<InventoryDeliveryNoteDetail> details) {
        int index;
        for (InventoryDeliveryNoteDetail detail : details) {
            if (productNames.indexOf(detail.getProduct().getName()) == -1) {
                productNames.add(detail.getProduct().getName());
                inventory.add(new int[]{0, 0, 0});
            }
            index = productNames.indexOf(detail.getProduct().getName());
            inventory.get(index)[1] += detail.getQuantity();
            inventory.get(index)[2] = inventory.get(index)[0] - inventory.get(index)[1];

        }
    }

    public String getOutPutString(List<String> productNames, List<int[]> inventory, Date start, Date end) {
        if (productNames.size() != 0) {
            String output = "";
            String line = new String(new char[72]).replace('\0', '-');
            output += String.format(" Date  %s  %s\n", start, end);
            output += line + "\n";
            output += String.format("|%s|%s|%s|%s|%n",
                    StringUtils.center("Name", 22),
                    StringUtils.center("Received", 15),
                    StringUtils.center("Delivery", 15),
                    StringUtils.center("Balance", 15));
            output += line + "\n";
            for (int i = 0; i < productNames.size(); ++i) {
                output += String.format("|%s|%s|%s|%s|%n",
                        StringUtils.center(productNames.get(i), 22),
                        StringUtils.center(String.valueOf(inventory.get(i)[0]), 15),
                        StringUtils.center(String.valueOf(inventory.get(i)[1]), 15),
                        StringUtils.center( String.valueOf(inventory.get(i)[2]), 15));
            }
            return output;
        } else {
            return "No inventory available";
        }
    }

    public boolean checkStock(String pName, Integer initialStock, int previousQuantity, int newQuantity) {
        if (initialStock == null)  {
            initialStock = 0;
        }
        List<InventoryReceivingNoteDetail> receivedDetails = receivedDetailService.getAllDetails();
        List<InventoryDeliveryNoteDetail> deliveredDetails = deliveredDetailService.getAllDetails();
        checkProductInventory(receivedDetails, deliveredDetails);
        int index = productNames.indexOf(pName);
        if ((index == -1 && initialStock >= newQuantity) ||
            index != -1 && (inventory.get(index)[2] + initialStock + previousQuantity >= newQuantity)) {
            return true;
        } else {
            return false;
        }
    }
}
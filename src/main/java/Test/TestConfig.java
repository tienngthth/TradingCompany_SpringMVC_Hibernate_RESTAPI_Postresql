package Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestConfig {
    protected static final String URL = "http://localhost:8080/";
    protected static HttpURLConnection httpURLConnection;
    protected static String jsonInputString;
    protected static List<Staff> staffs = new ArrayList<>();
    protected static List<Customer> customers = new ArrayList<>();
    protected static List<Provider> providers = new ArrayList<>();
    protected static List<Product> products = new ArrayList<>();
    protected static List<ProviderOrder> orders = new ArrayList<>();
    protected static List<InventoryDeliveryNote> deliveryNotes = new ArrayList<>();
    protected static List<InventoryReceivingNote> receivingNotes = new ArrayList<>();
    protected static List<SalesInvoice> salesInvoices = new ArrayList<>();
    private static ObjectMapper mapper = new ObjectMapper();

    protected static void setupPostConnection(String path) throws IOException {
        setupConnection(path, "POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoOutput(true);
    }

    protected static String setupDeleteConnection(String path) throws IOException {
        setupConnection(path, "GET");
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private static void setupConnection(String path, String method) throws MalformedURLException {
        java.net.URL url = new URL(TestConfig.URL + path);
        httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    protected static String getActualOutputPostMethod(String path, String json) throws IOException {
        jsonInputString = json;
        setupPostConnection(path);
        try(OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return getPostOutputMessage();
    }

    protected static String  getPostOutputMessage() throws IOException {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    protected static String setUpGetConnection(String path) throws IOException {
        setupConnection(path, "GET");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    protected static void getActualOutputGetMethod(String path) throws IOException {
        String json = setUpGetConnection(path);
        if (path.contains("staffs/")) {
            updateStaffList(json, path);
        } else if (path.contains("customers/")) {
            updateCustomerList(json, path);
        } else if (path.contains("providers/")) {
            updateProviderList(json, path);
        } else if (path.contains("products/")) {
            updateProductList(json, path);
        } else if (path.contains("orders/")) {
            updateOrderList(json, path);
        } else if (path.contains("inventoryDeliveryNotes/")) {
            updateDeliveryNoteList(json, path);
        } else if (path.contains("inventoryReceivingNotes/")) {
            updateReceivingNoteList(json, path);
        } else if (path.contains("salesInvoices/")) {
            updateSalesInvoiceList(json, path);
        }
    }

    private static void updateStaffList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            staffs = new ArrayList<>();
            staffs.add(mapper.readValue(json, Staff.class));
        } else {
            staffs = Arrays.asList(mapper.readValue(json, Staff[].class));
        }
    }

    private static void updateCustomerList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            customers = new ArrayList<>();
            customers.add(mapper.readValue(json, Customer.class));
        } else {
            customers = Arrays.asList(mapper.readValue(json, Customer[].class));
        }
    }

    private static void updateProviderList(String json, String path) throws IOException {
        if (path.contains("?id") ) {
            providers = new ArrayList<>();
            providers.add(mapper.readValue(json, Provider.class));
        } else {
            providers = Arrays.asList(mapper.readValue(json, Provider[].class));
        }
    }

    private static void updateProductList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            products = new ArrayList<>();
            products.add(mapper.readValue(json, Product.class));
        } else {
            products = Arrays.asList(mapper.readValue(json, Product[].class));
        }
    }

    private static void updateOrderList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            orders = new ArrayList<>();
            orders.add(mapper.readValue(json, ProviderOrder.class));
        } else {
            orders = Arrays.asList(mapper.readValue(json, ProviderOrder[].class));
        }
    }

    private static void updateDeliveryNoteList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            deliveryNotes = new ArrayList<>();
            deliveryNotes.add(mapper.readValue(json, InventoryDeliveryNote.class));
        } else {
            deliveryNotes = Arrays.asList(mapper.readValue(json, InventoryDeliveryNote[].class));
        }
    }

    private static void updateReceivingNoteList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            receivingNotes = new ArrayList<>();
            receivingNotes.add(mapper.readValue(json, InventoryReceivingNote.class));
        } else {
            receivingNotes = Arrays.asList(mapper.readValue(json, InventoryReceivingNote[].class));
        }
    }

    private static void updateSalesInvoiceList(String json, String path) throws IOException {
        if (path.contains("?id")) {
            salesInvoices = new ArrayList<>();
            salesInvoices.add(mapper.readValue(json, SalesInvoice.class));
        } else {
            salesInvoices = Arrays.asList(mapper.readValue(json, SalesInvoice[].class));
        }
    }

    protected static String createAndReturnStaffJs() throws IOException {
        getActualOutputPostMethod("staffs/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("staffs/read/all/?page=-1");
        return "{\"id\": \""+ staffs.get(staffs.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnCustomerJs() throws IOException {
        getActualOutputPostMethod("customers/create",
                "{\"name\": \"KAT\"}");
        getActualOutputGetMethod("customers/read/all/?page=-1");
        return "{\"id\": \""+ customers.get(customers.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnProviderJs() throws IOException {
        getActualOutputPostMethod("providers/create",
                "{\"name\": \"Andy\"}");
        getActualOutputGetMethod("providers/read/all");
        return "{\"id\": \""+ providers.get(providers.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnProductJs(Float price) throws  IOException {
        getActualOutputGetMethod("products/read/all");
        String newProductName;
        if (products.size() == 0) {
            newProductName = "product";
        } else {
            newProductName = "product " + products.get(products.size() - 1).getId() + 1;
        }
        getActualOutputPostMethod("products/create",
                "{\"name\": \"" + newProductName + "\"," +
                        "\"initialStock\": 100," +
                        "\"sellingPrice\": " + price + "}");
        getActualOutputGetMethod("products/read/all");
        return "{\"id\": "+ products.get(products.size() - 1).getId() + "}";
    }

    protected static String createAndReturnDetailJs(String product, int quantity) {
        return "[{\"quantity\": " + quantity + "," +
                "\"product\": " + product + "}]";
    }

    protected static String createAndReturnOrderJs() throws IOException {
        String staff = createAndReturnStaffJs();
        String provider = createAndReturnProviderJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("orders/create",
                "{\"staff\": " + staff + "," +
                        "\"provider\": " + provider + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("orders/read/all/?page=-1");
        return "{\"id\": \""+ orders.get(orders.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnDeliveryNoteJs() throws IOException {
        String staff = createAndReturnStaffJs();
        String details = createAndReturnDetailJs(createAndReturnProductJs((float) 123), 1);
        getActualOutputPostMethod("inventoryDeliveryNotes/create",
                "{\"staff\": " + staff + "," +
                        "\"details\": " + details + "}");
        getActualOutputGetMethod("inventoryDeliveryNotes/read/all/?page=-1");
        return "{\"id\": \""+ deliveryNotes.get(deliveryNotes.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnReceivingNoteJs() throws IOException {
        String order = createAndReturnOrderJs();
        getActualOutputPostMethod("inventoryReceivingNotes/create",
                "{\"order\": " + order + "}");
        getActualOutputGetMethod("inventoryReceivingNotes/read/all/?page=-1");
        return "{\"id\": \""+ receivingNotes.get(receivingNotes.size() - 1).getId() + "\"}";
    }

    protected static String createAndReturnSalesInvoiceJs() throws IOException {
        String deliveryNote = createAndReturnDeliveryNoteJs();
        String customer = createAndReturnCustomerJs();
        getActualOutputPostMethod("salesInvoices/create",
                    "{\"note\": " + deliveryNote +
                        ",\"customer\": " + customer + "}");
        getActualOutputGetMethod("salesInvoices/read/all/?page=-1");
        return "{\"id\": \""+ salesInvoices.get(salesInvoices.size() - 1).getId() + "\"}";
    }
}

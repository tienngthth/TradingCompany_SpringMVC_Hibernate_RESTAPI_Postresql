let URL, name, address, email, phone, fax,
    contactPerson, company, brand, model, sellingPrice,
    initialStock, categoryId, description,
    date, staffId, providerId, orderId, issueId, customerId, salesStaffName,
    productIdFields, priceFields, quantityFields, issueIdField, customerIdField, salesStaffNameField,
    nameField, addressField, emailField, phoneField,
    faxField, contactPersonField, companyField, brandField,
    modelField, sellingPriceField, initialStockField, categoryIdField,
    descriptionField, dateField, staffIdField, providerIdField, orderIdField;
const loader = document.getElementById('loader');
const formBox = document.getElementById('createFormBox');

function addStaff() {
    URL = 'http://localhost:8080/staffs/create';
    addRecord();
}

function addCustomer() {
    URL = 'http://localhost:8080/customers/create';
    addRecord();
}

function addProvider() {
    URL = 'http://localhost:8080/providers/create';
    addRecord();
}

function addCategory() {
    URL = 'http://localhost:8080/categories/create';
    addRecord();
}

function addProduct() {
    URL = 'http://localhost:8080/products/create';
    addRecord();
}

function addOrder() {
    URL = 'http://localhost:8080/orders/create';
    addRecord();
}

function addReceipt() {
    URL = 'http://localhost:8080/inventoryReceivingNotes/create';
    addRecord();
}

function addIssue() {
    URL = 'http://localhost:8080/inventoryDeliveryNotes/create';
    addRecord();
}

function addInvoice() {
    URL = 'http://localhost:8080/salesInvoices/create';
    addRecord();
}

function addRecord(){
    startProcessing();
    resetData();
    getData();
    fetchRequest();
}

function startProcessing() {
    localStorage.setItem("processing", "true");
    loader.style.display = "block";
    formBox.style.paddingBottom = "10px";
}

function resetData() {
    nameField = document.getElementById('name');
    addressField = document.getElementById('address');
    emailField = document.getElementById('email');
    phoneField = document.getElementById('phone');
    faxField = document.getElementById('fax');
    contactPersonField = document.getElementById('contactPerson');
    companyField = document.getElementById('company');
    brandField = document.getElementById('brand');
    modelField = document.getElementById('model');
    sellingPriceField = document.getElementById('sellingPrice');
    initialStockField = document.getElementById('initialStock');
    categoryIdField = document.getElementById('categoryId');
    descriptionField = document.getElementById('description');
    dateField = document.getElementById('date');
    staffIdField = document.getElementById('staffId');
    providerIdField = document.getElementById('providerId');
    orderIdField = document.getElementById('orderId');
    customerIdField = document.getElementById('customerId');
    issueIdField = document.getElementById('issueId');
    salesStaffNameField = document.getElementById('salesStaffName');
    productIdFields = document.getElementsByClassName('productId');
    priceFields = document.getElementsByClassName('price');
    quantityFields = document.getElementsByClassName('quantity');
}

function getData() {
    name = getValue(nameField);
    address = getValue(addressField);
    email = getValue(emailField);
    phone = getValue( phoneField);
    fax = getValue(faxField);
    contactPerson = getValue(contactPersonField);
    company = getValue(companyField);
    brand = getValue(brandField);
    model = getValue(modelField);
    sellingPrice = getValue( sellingPriceField);
    initialStock = getValue(initialStockField);
    description = getValue(descriptionField);
    categoryId = getValue(categoryIdField);
    date = getValue(dateField);
    staffId = getValue(staffIdField);
    providerId = getValue(providerIdField);
    orderId = getValue(orderIdField);
    customerId = getValue(customerIdField);
    salesStaffName = getValue(salesStaffNameField);
    issueId = getValue(issueIdField);
}

function getValue(field) {
    if (field != null && field.value != "") {
        return field.value;
    }
}

function getDetailJson() {
    let rawDetails = [];
    if (productIdFields != null && productIdFields.length > 0) {
        for (let i = 0; i < productIdFields.length; ++i) {
            rawDetails.push({product: {id: productIdFields[i].value}, quantity: quantityFields[i].value, price: priceFields[i].value});
        }
        return rawDetails;
    }
    return null;
}

function fetchRequest() {
    fetch(URL, {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({
            name: name,
            email: email,
            address: address,
            phone: phone,
            fax: fax,
            contactPerson: contactPerson,
            company: company,
            brand: brand,
            model: model,
            sellingPrice: sellingPrice,
            initialStock: initialStock,
            category: {
                id: categoryId
            },
            description: description,
            date: date,
            staff : {
                id: staffId,
            },
            provider: {
                id: providerId
            },
            order : {
                id: orderId,
            },
            customer: {
                id: customerId
            },
            note : {
                id: issueId,
            },
            salesStaffName : {
                id: salesStaffName,
            },
            details: getDetailJson()
        })
    })
        .catch(function(response) {
            return response;
        })
        .then(function(response) {
            if (!response.ok) {
                finishProcessing("Error detected");
            } else {
                response.text().then(function(message) {
                    finishProcessing(message);
                });
            }
        })
}

function finishProcessing(message) {
    loader.style.display = "none";
    formBox.style.paddingBottom = "40px";
    localStorage.setItem("processing", "false");
    setTimeout(function() {
        alert(message)
    }, 150)
    if (!message.includes("Failed")) {
        resetField();
    }
}

function resetField() {
    resetValue(nameField);
    resetValue(addressField);
    resetValue(emailField);
    resetValue(phoneField);
    resetValue(faxField);
    resetValue(contactPersonField);
    resetValue(brandField);
    resetValue(modelField);
    resetValue(companyField);
    resetValue(sellingPriceField);
    resetValue(initialStockField);
    resetValue(descriptionField);
    resetValue(categoryIdField);
    resetValue(staffIdField);
    resetValue(providerIdField);
    resetValue(orderIdField);
    resetValue(customerIdField);
    resetValue(salesStaffNameField);
    resetValue(issueIdField);
    resetListOfValue(productIdFields);
    resetListOfValue(priceFields);
    resetListOfValue(quantityFields);
}

function resetListOfValue(field) {
    for (let i = 0; i < field.length; ++i) {
        field[i].value = "";
    }
}

function resetValue(field) {
    if (field != null && field.value != "") {
        field.value = "";
    }
}
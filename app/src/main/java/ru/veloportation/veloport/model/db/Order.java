package ru.veloportation.veloport.model.db;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Order {

    private String id;
    private String customerName;
    private String email;
    private String phone;
    private String addressSender;
    private String addressDelivery;
    private String message;
    private int statusOrder;
    private String customerUUID;


    public Order(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        customerUUID = tManager.getDeviceId();
        setState(false);

    }

    public Order setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public Order setEmail(String email) {
        this.email = email;
        return this;
    }

    public Order setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Order setAddressSender(String addressSender) {
        this.addressSender = addressSender;
        return this;
    }

    public Order setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
        return this;
    }

    public Order setMessage(String message) {
        this.message = message;
        return this;
    }

    public Order setState(boolean status) {
        this.statusOrder = status ? 1:0;
        this.message = message;
        return this;
    }

    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddressSender() { return addressSender; }
    public String getAddressDelivery() { return addressDelivery; }
    public String getAddress() { return message; }
    public boolean getStatus() { return statusOrder == 1 ? true:false; }
    public String getId() { return id; }




}

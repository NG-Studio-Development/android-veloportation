package ru.veloportation.veloport.model.db;

import android.content.Context;

import ru.veloportation.VeloportApplication;

public class Order {

    public static int STATE_SEARCH_COURIER = 0;
    public static int STATE_TAKE = 1;
    public static int STATE_DELIVERY = 2;

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
        //TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //customerUUID = tManager.getDeviceId();
        customerUUID = VeloportApplication.getInstance().getUUID();
        setState(STATE_SEARCH_COURIER);
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

    public Order setState(int state) {
        this.statusOrder = state;
        this.message = message;
        return this;
    }

    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddressSender() { return addressSender; }
    public String getAddressDelivery() { return addressDelivery; }
    public String getAddress() { return message; }

    public int getStatus() { return statusOrder; }

    public String getId() { return id; }




}

package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.model.db.Order;

public class OrderRequest extends StringRequest {

    private static final String className = "Orders";

    private OrderRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static OrderRequest requestCreateOrder(Order order, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String jsonOrderString = new Gson().toJson(order);
        String url = ConstantsVeloportApp.URL_SERVER+"/classes/"+className+"/"+jsonOrderString;


        Log.d("ORDER_REQUEST", "Request = " + url);
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestOfCourierOrder(String courierId, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        /*JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("statusOrder", Order.STATE_SEARCH_COURIER);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new Error("Json Error in class OrderRequest, method requestGetFreeOrder");
        } */



        String url = ConstantsVeloportApp.URL_SERVER+"/get/orders/courier/"+courierId;
        Log.d("REQUEST_GET_FREE_ORDER","requestGetFreeOrder = "+url);
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestGetFreeOrder(Response.Listener<String> listener, Response.ErrorListener errorListener) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("statusOrder", Order.STATE_SEARCH_COURIER);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new Error("Json Error in class OrderRequest, method requestGetFreeOrder");
        }



        String url = ConstantsVeloportApp.URL_SERVER+"/get/orders/"+jsonObject.toString();
        Log.d("REQUEST_GET_FREE_ORDER","requestGetFreeOrder = "+url);
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestGetListCustomerOrder (String id, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("idCustomer", id);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new Error("JSONError in class OrderRequest, method requestGetListCustomerOrder()");
        }

        String url = ConstantsVeloportApp.URL_SERVER+"/get/orders/"+jsonObject.toString();

        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestTakeOrder(Order order, String uuid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String jsonOrderString = new Gson().toJson(order);
        //String url = ConstantsVeloportApp.URL_SERVER+"/classes/2/"+className+"/"+order.getId()+"/"+jsonOrderString;
        String url = ConstantsVeloportApp.URL_SERVER+"/put/takeorder/"+order.getId()+"/"+uuid+"/"+jsonOrderString;
        Log.d("REQUEST_TAKE_ORDER","REQUEST ="+url);
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest removeDelivery(Order order, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        //String jsonOrderString = new Gson().toJson(order);
        //String url = ConstantsVeloportApp.URL_SERVER+"/classes/2/"+className+"/"+order.getId()+"/"+jsonOrderString;
        String url = ConstantsVeloportApp.URL_SERVER+"/delete/delivery/"+order.getId();
        Log.d("REMOVE_DELIVERY", "Request = " + url);
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

}

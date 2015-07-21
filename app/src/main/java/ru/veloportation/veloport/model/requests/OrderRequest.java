package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

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

    public static OrderRequest requestGetListOrder (Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsVeloportApp.URL_SERVER+"/classes/"+className;

        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestGetListCustomerOrder (Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsVeloportApp.URL_SERVER+"/classes/"+className;

        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static OrderRequest requestTakeOrder(Order order, String uuid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String jsonOrderString = new Gson().toJson(order);
        //String url = ConstantsVeloportApp.URL_SERVER+"/classes/2/"+className+"/"+order.getId()+"/"+jsonOrderString;
        String url = ConstantsVeloportApp.URL_SERVER+"/put/takeorder/"+order.getId()+"/"+uuid+"/"+jsonOrderString;
        return new OrderRequest(Request.Method.GET, url, listener, errorListener);
    }
}

package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.VeloportApplication;

public class CustomerRequest extends StringRequest {


    public CustomerRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static CustomerRequest requestCustomerMyCustomerId(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsVeloportApp.URL_SERVER+"/get/customer/"+ VeloportApplication.getInstance().getApplicationPreferences().getString(ConstantsVeloportApp.PREF_KEY_LOGIN, "NUN");
        Log.d("REQUEST_SET_EMPLOYMENT", "Request = " + url);
        return  new CustomerRequest(Request.Method.GET, url, listener, errorListener);
    }

}

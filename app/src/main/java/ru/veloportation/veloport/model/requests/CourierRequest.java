package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.veloportation.veloport.ConstantsVeloportApp;

public class CourierRequest extends StringRequest {

    public CourierRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static CourierRequest requestSetEmployment(String uuid, boolean employment, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        int employmentInt = employment ? 1:0;
        String url = ConstantsVeloportApp.URL_SERVER+"/put/courier/"+uuid+"/"+employmentInt;
        Log.d("REQUEST_SET_EMPLOYMENT", "Request = "+url);
        return  new CourierRequest(Request.Method.GET, url, listener, errorListener);
    }

}

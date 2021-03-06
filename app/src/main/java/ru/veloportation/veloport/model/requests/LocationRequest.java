package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.model.db.Order;

public class LocationRequest extends StringRequest {


    public LocationRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static LocationRequest requestUpdateLocation(String uuid, double latitude, double longitude, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsVeloportApp.URL_SERVER+"/location/"+uuid+"/"+latitude+"/"+longitude;
        Log.d("REQUEST_UPDATE_LOCATION", url);
        return new LocationRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static LocationRequest requestGetCourierLocation(Order order, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ConstantsVeloportApp.URL_SERVER+"/location/"+order.getId();
        Log.d("REQ_COURIER_LOCATION", url);
        return new LocationRequest(Request.Method.GET, url, listener, errorListener);
    }

    public static LocationRequest requestLocationByAddressFromGoogle(String address, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = "http://maps.google.com/maps/api/geocode/json?address="+address+",Rostov-on-don+RU&sensor=false";//&key=API_KEY

        //String urlAddress = String.format(Locale.ENGLISH, url);
        Log.d("LOCATION_BY_ADDRESS","Url = "+url);
        return new LocationRequest(Request.Method.GET, url, listener, errorListener);
    }

}
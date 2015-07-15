package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.ConstantsVeloportApp;

public class RegisterIdRequest extends StringRequest {


    public RegisterIdRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static RegisterIdRequest requestUpdateId(String name, String pass, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        //Log.d("REQUEST URL", "Request = "+ConstantsVeloportApp.URL_SERVER+"/classes/Users/"+name+"/"+regId);
        return new RegisterIdRequest(Request.Method.GET, ConstantsVeloportApp.URL_SERVER+"/classes/Users/"+name+"/"+regId,listener,errorListener);
    }

    public static RegisterIdRequest requestRegisterUUID(String uuid, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        //Log.d("REQUEST URL", "Request = "+ConstantsVeloportApp.URL_SERVER+"/classes/Users/"+name+"/"+regId);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("registerId",regId);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error in json data");
        }
        Log.d("REQUEST", "Request = " + ConstantsVeloportApp.URL_SERVER + "/classes/1/Users/" + uuid + "/" + jsonObject.toString());

        return new RegisterIdRequest(Request.Method.GET, ConstantsVeloportApp.URL_SERVER+"/classes/1/Users/"+uuid+"/"+jsonObject.toString(),listener,errorListener);
    }
}

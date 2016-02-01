package ru.veloportation.veloport.model.requests;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.ConstantsVeloportApp;

public class RegisterIdRequest extends StringRequest {


    private RegisterIdRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static RegisterIdRequest requestUpdateId(String login, String pass, String regId, String uuid, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        Log.d("REQUEST URL", "Request = " + ConstantsVeloportApp.URL_SERVER + "/login/" + login + "/" + pass + "/" + regId + "/" + uuid);
        return new RegisterIdRequest(Request.Method.GET, ConstantsVeloportApp.URL_SERVER+"/login/"+login+"/"+pass+"/"+regId+"/"+uuid, listener,errorListener);
    }

    public static RegisterIdRequest requestLoginCustomer(String login, String pass, String regId, String uuid, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsVeloportApp.URL_SERVER+"/get/customer/"+login+"/"+pass+"/"+regId+"/"+uuid;
        Log.d("REQUEST URL", "Request = "+url);
        return new RegisterIdRequest(Request.Method.GET, url, listener,errorListener);
    }

    public static RegisterIdRequest requestRegisterUser(String uuid, JSONObject jsonObject, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        String url = ConstantsVeloportApp.URL_SERVER+"/post/user/"+uuid+"/"+jsonObject.toString();
        Log.d("REQUEST", "Request = " + url);

        return new RegisterIdRequest(Request.Method.GET, url, listener,errorListener);
    }

    public static RegisterIdRequest requestRegisterUUID(String uuid, String regId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("registerId",regId);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error in json data");
        }
        Log.d("REQUEST", "Request = " + ConstantsVeloportApp.URL_SERVER + "/classes/ic_1/Users/" + uuid + "/" + jsonObject.toString());
        return new RegisterIdRequest(Request.Method.GET, ConstantsVeloportApp.URL_SERVER+"/classes/ic_1/Users/"+uuid+"/"+jsonObject.toString(),listener,errorListener);
    }
}

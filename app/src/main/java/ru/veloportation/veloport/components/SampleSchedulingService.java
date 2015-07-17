package ru.veloportation.veloport.components;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.model.requests.LocationRequest;

public class SampleSchedulingService extends IntentService {
    public SampleSchedulingService() {
        super("SchedulingService");
    }
    


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("SERVICE_UPDATE_LOCATION", "Update location");
        String uuid = VeloportApplication.getInstance().getUUID();
        double latitude = System.currentTimeMillis() / 10000;
        double longitude = System.currentTimeMillis() / 11000;

        LocationRequest request = LocationRequest.requestUpdateLocation(uuid, latitude, longitude, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SERVICE_UPDATE_LOCATION", "successfull");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SERVICE_UPDATE_LOCATION", "successfull");
            }
        });

        Volley.newRequestQueue(this).add(request);
        SampleAlarmReceiver.completeWakefulIntent(intent);

    }
}

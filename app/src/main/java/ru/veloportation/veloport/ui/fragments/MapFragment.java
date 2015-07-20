package ru.veloportation.veloport.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.LocationRequest;

public class MapFragment extends BaseMapFragment {

    private static final float START_ZOOM = 14.0f;

    public final static String ACTION_NEW_LOCATION = "action_new_location";

    public final static String KEY_DATA = "key_data";

    public final static String DATA_LATITUDE = "data_latitude";

    public final static String DATA_LONGITUDE = "data_longitude";

    private Order order;

    private BroadcastReceiver broadcastReceiver;

    public MapFragment() { }

    public static MapFragment newMapFragment(Order order) {
        MapFragment fragment = new MapFragment();
        fragment.order = order;
        return fragment;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LocationRequest request =  LocationRequest.requestGetCourierLocation(order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Error")) {
                    Toast.makeText(getHostActivity(), "Probliem with location", Toast.LENGTH_LONG);
                } else {
                    try {
                        JSONArray arr = new JSONArray(response);
                        JSONObject jsonObject = arr.getJSONObject(0);

                        double latitude = jsonObject.getDouble("latitude");
                        double longitude = jsonObject.getDouble("longitude");

                        setLocation(new LatLng(latitude, longitude));

                    } catch (JSONException ex) {
                        ex.printStackTrace();}
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getHostActivity()).add(request);

        /** In future realize update location in real time */
        //IntentFilter intFilt = new IntentFilter(ConstantsVeloportApp.BROADCAST_ACTION);
        //broadcastReceiver = createBroadCast();
        //getHostActivity().registerReceiver(broadcastReceiver, intFilt);

        return super.onCreateView(inflater,container,savedInstanceState);
    }

    private void setLocation(LatLng latLng) {
        changingCameraPosition(latLng);
        updateLocations(latLng, null, null);
        zoomIn(getOptimalCameraPosition(), START_ZOOM);
    }



    BroadcastReceiver createBroadCast() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                if (intent.getStringExtra(PARAM_ACTION).equals(ACTION_NEW_LOCATION)) {
                    setLocation(intent.getBundleExtra(KEY_DATA));
                }

            }
        };
    }


    private void setLocation(Bundle extras) {

        double latitude = extras.getDouble(DATA_LATITUDE, -1);
        double longitude = extras.getDouble(DATA_LONGITUDE, -1);

        Log.d("SET_LOCATION", "latitude = "+latitude+" longitude = "+longitude);

        if (latitude != -1 && longitude != -1)
            setLocation( new LatLng(latitude, longitude) );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getHostActivity().unregisterReceiver(broadcastReceiver);
    }

}

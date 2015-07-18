package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
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

public class MapFragment extends BaseMapFragment{

    private static final float START_ZOOM = 14.0f;

    private Order order;

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

                        LatLng latLng = new LatLng(latitude, longitude);
                        changingCameraPosition(latLng);

                        updateLocations(latLng, null, null);
                        zoomIn(getOptimalCameraPosition(), START_ZOOM);



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
        return super.onCreateView(inflater,container,savedInstanceState);
    }

}

package ru.veloportation.veloport.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import ru.veloportation.veloport.VeloportApplication;
import ru.veloportation.veloport.model.requests.LocationRequest;

public class LocationUtils {
    private static LocationUtils locationUtils = null;
    private String locError = null;

    private LocationUtils() {}

    public static LocationUtils getInstance() {

        if (locationUtils == null )
            locationUtils = new LocationUtils();

        return locationUtils;
    }

    public Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /*public String calculateCostForAddresses(String addressSender, String addressDelivery) {

        Location locationAddressSender = getLocationByAddress(addressSender);
        Location locationAddressDelivery = getLocationByAddress(addressDelivery);

        if ( locationAddressSender != null &&
                locationAddressDelivery != null ) {

            float distance = locationAddressSender.distanceTo(locationAddressDelivery);
            distance = distance / 1000;
            int cost = (int) (distance > 3 ? (distance - 3) * 10 + (3*20) : distance*20);
            return String.valueOf(cost);
        }

        return locError; //VeloportApplication.getInstance().getString(R.string.no_address_found);
    } */


    Location locationSender;
    Location locationDelivery;

    public void calculateCostForAddresses (final String addressSender, final String addressDelivery, final CostListener costListeners ){

        String addressSenderEncode = null;
        String addressDeliveryEncode = null;
        try {
            addressSenderEncode = URLEncoder.encode(addressSender, "UTF-8");
            addressDeliveryEncode = URLEncoder.encode(addressDelivery, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage()!=null ? ex.getMessage() : "Error UnsupportedEncodingException, class LocationUtils in method calculateCostForAddresses");
        }


        getLocationInfo(addressSenderEncode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    locationSender = getLocation(new JSONObject(response), addressSender);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error( ex.getMessage()!=null ? ex.getMessage() : "JSONException in getLocationInfo()" );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(VeloportApplication.getInstance(),"Error in get sender location", Toast.LENGTH_LONG).show();
            }
        } );



        getLocationInfo(addressDeliveryEncode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    locationDelivery = getLocation(new JSONObject(response), addressDelivery);

                    if ( locationSender != null &&
                            locationDelivery != null ) {

                        float distance = locationSender.distanceTo(locationDelivery);
                        distance = distance / 1000;
                        int cost = (int) (distance > 3 ? (distance - 3) * 10 + (3*20) : distance*20);

                        costListeners.onCalculated( String.valueOf(cost) );
                    } else {
                        costListeners.onCalculated( String.valueOf("Error location is null") );
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new Error( ex.getMessage() != null ? ex.getMessage() : "JSONException in getLocationInfo()" );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(VeloportApplication.getInstance(), "Error in get delivery location", Toast.LENGTH_LONG).show();
            }
        } );

    }


    public interface CostListener {
        void onCalculated(String cost);
    }

    public static void getLocationInfo(final String address, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        LocationRequest request = LocationRequest.requestLocationByAddressFromGoogle(address, listener, errorListener);
        Volley.newRequestQueue(VeloportApplication.getInstance()).add(request);
        // http://maps.google.com/maps/api/geocode/json?address=Izvilistaya%2019,%20Rostov-on-don+RU&sensor=false
    }


    public static Location getLocation(JSONObject jsonObject, String address) {



        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Location location = new Location(address);
        location.setLatitude(lat);
        location.setLongitude(lon);
        //return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        return location;

    }


    private Location getLocationByAddress(String addressSender) {

        List<Address> addressesList = getAddress(addressSender);
        String errorMessage = "";

        if (addressesList == null ) {
        } else if( addressesList.size()  == 0 ) {
            locError = "Error Address list EMPTY";
        } else {
            Location location = new Location(addressSender);
            location.setLatitude(addressesList.get(0).getLatitude());
            location.setLongitude(addressesList.get(0).getLongitude());
            return location;

        }

        return null;
    }

    private List<Address> getAddress(String address) {

        Geocoder geocoder = new Geocoder(VeloportApplication.getInstance());

        try {
            return geocoder.getFromLocationName (address, 1);
        } catch (IOException ioException) {

            locError = "Error IOException";

        } catch (IllegalArgumentException illegalArgumentException) {
            locError = "Error IllegalArgumentException";
        }

        return null;
    }
}

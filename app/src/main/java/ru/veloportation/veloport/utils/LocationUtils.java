package ru.veloportation.veloport.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;

public class LocationUtils {
    private static LocationUtils locationUtils = null;

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

    public String calculateCostForAddresses(String addressSender, String addressDelivery) {
        // String addressSender = intent.getStringExtra(ConstantsVeloportApp.SENDER_DATA_EXTRA);
        // String addressDelivery = intent.getStringExtra(ConstantsVeloportApp.DELIVERY_DATA_EXTRA);

        //mReceiver = intent.getParcelableExtra(ConstantsVeloportApp.RECEIVER);

        Location locationAddressSender = getLocationByAddress(addressSender);
        Location locationAddressDelivery = getLocationByAddress(addressDelivery);

        if ( locationAddressSender != null &&
                locationAddressDelivery != null ) {

            float distance = locationAddressSender.distanceTo(locationAddressDelivery);
            distance = distance / 1000;
            int cost = (int) (distance > 3 ? (distance - 3) * 10 + (3*20) : distance*20);
            return String.valueOf(cost);
            //deliverResultToReceiver(ConstantsVeloportApp.SUCCESS_RESULT, String.valueOf(cost));
        }
        return VeloportApplication.getInstance().getString(R.string.no_address_found);
    }


    private Location getLocationByAddress(String addressSender) {

        List<Address> addressesList = getAddress(addressSender);
        String errorMessage = "";

        if (addressesList == null || addressesList.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = VeloportApplication.getInstance().getString(R.string.no_address_found);
                //Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(ConstantsVeloportApp.FAILURE_RESULT, errorMessage);
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
        List<Address> addressesList = null;
        String errorMessage = "";

        try {
            return geocoder.getFromLocationName (address, 1);
        } catch (IOException ioException) {

            errorMessage = VeloportApplication.getInstance().getString(R.string.service_not_available);
            //Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = VeloportApplication.getInstance().getString(R.string.invalid_address_used);
            //Log.e(TAG, errorMessage + ". " + "Address = " + address, illegalArgumentException);
        }

        return null;
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsVeloportApp.RESULT_DATA_KEY, message);
    }

}

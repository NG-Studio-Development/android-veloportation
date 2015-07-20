package ru.veloportation.veloport.components;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;

public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver mReceiver;

    private final String TAG = "FETCH_ADDRESS";

    public FetchAddressIntentService() {
        super("FetchAddressService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String addressSender = intent.getStringExtra(ConstantsVeloportApp.SENDER_DATA_EXTRA);
        String addressDelivery = intent.getStringExtra(ConstantsVeloportApp.DELIVERY_DATA_EXTRA);

        mReceiver = intent.getParcelableExtra(ConstantsVeloportApp.RECEIVER);

        Location locationAddressSender = getLocationByAddress(addressSender);
        Location locationAddressDelivery = getLocationByAddress(addressDelivery);

        if ( locationAddressSender != null &&
                locationAddressDelivery != null ) {

            float distance = locationAddressSender.distanceTo(locationAddressDelivery);
            int cost = (int) (distance > 3 ? (distance - 3) * 10 + (3*20) : distance*20);

            deliverResultToReceiver(ConstantsVeloportApp.SUCCESS_RESULT, String.valueOf(cost));
        }

    }

    private Location getLocationByAddress(String addressSender) {

        List<Address> addressesList = getAddress(addressSender);
        String errorMessage = "";

        if (addressesList == null || addressesList.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
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

        Geocoder geocoder = new Geocoder(this);
        List<Address> addressesList = null;
        String errorMessage = "";

        try {
            return geocoder.getFromLocationName (address, 1);
        } catch (IOException ioException) {

            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_address_used);
            Log.e(TAG, errorMessage + ". " + "Address = " + address, illegalArgumentException);
        }

        return null;
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsVeloportApp.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


}
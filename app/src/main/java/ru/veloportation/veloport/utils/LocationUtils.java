package ru.veloportation.veloport.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

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
}

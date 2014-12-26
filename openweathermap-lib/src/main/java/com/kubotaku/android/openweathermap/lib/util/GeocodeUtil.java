package com.kubotaku.android.openweathermap.lib.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.kubotaku.android.openweathermap.lib.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Geocode Utilities.
 */
public class GeocodeUtil {

    /**
     * Get location name from Address.
     *
     * @param context Context.
     * @param locale Locale.
     * @param latlng Address.
     * @return Location name of target address.
     */
    public static String pointToName(final Context context, final Locale locale, final LatLng latlng) {
        String name = "";

        try {
            Geocoder geocoder = new Geocoder(context, locale);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ((addressList != null) && !addressList.isEmpty()) {

                int loopNum = addressList.size();
                for (int i = 0; i < loopNum; i++) {
                    Address address = addressList.get(i);

                    name = address.getLocality();
                    if ((name == null) || (name.length() == 0)) {
                        name = address.getSubAdminArea();
                        if ((name != null) && (name.length() != 0)) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * Get location address from target location name.
     *
     * @param context Context.
     * @param locale Locale.
     * @param name Location name.
     * @return Location address.
     */
    public static LatLng nameToPoint(final Context context, final Locale locale, final String name) {
        LatLng address = null;

        try {
            Geocoder geocoder = new Geocoder(context, locale);
            List<Address> addressList = null;

            try {
                addressList = geocoder.getFromLocationName(name, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ((addressList != null) && !addressList.isEmpty()) {
                for (Address point : addressList) {
                    double latitude = point.getLatitude();
                    double longitude = point.getLongitude();
                    address = new LatLng(latitude, longitude);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

}

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

    public static String pointToName(final Context context, final Locale locale, final LatLng latlng) {
        String name = "";

        try {
            Geocoder geocoder = new Geocoder(context, locale);
            List<Address> list_address = null;
            try {
                list_address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!list_address.isEmpty()) {

                int loopNum = list_address.size();
                for (int i = 0; i < loopNum; i++) {
                    Address address = list_address.get(i);

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

}

package com.kubotaku.android.openweathermap.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.WeatherIconGetter;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;
import com.kubotaku.android.openweathermap.lib.WeatherParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Geocode Utilities.
 */
public class GeocodeUtil {

    /**
     * API Base URL : Google Maps Geocode api use get location from name.
     */
    private static final String API_FROM_NAME = "http://maps.googleapis.com/maps/api/geocode/json?address=%1$s&sensor=true";

    /**
     * API Base URL : Google Maps Geocode api use get name from location.
     */
    private static final String API_FROM_LOCATION = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true";

    /**
     * Get location name from Address.
     * <p/>
     * Use Google Maps Geocode api.
     * <p/>
     *
     * @param locale Locale.
     * @param latlng Address.
     * @return Location name of target address.
     */
    public static String pointToName(final Locale locale, final LatLng latlng) {
        String name = "";
        try {
            String requestURL = String.format(API_FROM_LOCATION, latlng.latitude, latlng.longitude);

            String lang = locale.getLanguage();
            requestURL += "&language=" + lang;

            URL url = new URL(requestURL);
            InputStream is = url.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();

            name = GeocodeParser.parseLocationName(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * Get location address from target location name.
     * <p/>
     * Use Google Maps Geocode api.
     * <p/>
     *
     * @param locale  Locale.
     * @param name    Location name.
     * @return Location address.
     */
    public static LatLng nameToPoint(final Locale locale, final String name) {
        LatLng address = null;

        try {
            String utf8Name = URLEncoder.encode(name, "UTF-8");
            String requestURL = String.format(API_FROM_NAME, utf8Name);

            String lang = locale.getLanguage();
            requestURL += "&language=" + lang;

            URL url = new URL(requestURL);
            InputStream is = url.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();

            address = GeocodeParser.parseLocation(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    /**
     * Get location name from Address.
     * <p/>
     * Use Android Geocode class.
     * <p/>
     *
     * @param context Context.
     * @param locale  Locale.
     * @param latlng  Address.
     * @return Location name of target address.
     */
    @Deprecated
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
     * <p/>
     * Use Android Geocode class.
     * <p/>
     *
     * @param context Context.
     * @param locale  Locale.
     * @param name    Location name.
     * @return Location address.
     */
    @Deprecated
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

    /**
     * Geocode result parse class.
     */
    private static final class GeocodeParser {

        private static final String KEY_RESULT = "results";
        private static final String KEY_ADDRESS_COMPONENT = "address_components";
        private static final String KEY_NAME = "long_name";
        private static final String KEY_TYPE = "types";
        private static final String KEY_GEO = "geometry";
        private static final String KEY_LOCATION = "location";
        private static final String KEY_LAT = "lat";
        private static final String KEY_LNG = "lng";

        private static final String VALUE_CITY = "locality";

        public static String parseLocationName(String data) {
            String location = "";

            try {
                boolean getName = false;
                JSONObject rootObject = new JSONObject(data);
                if (rootObject.has(KEY_RESULT)) {
                    JSONArray resultObject = rootObject.getJSONArray(KEY_RESULT);
                    int num = resultObject.length();

                    for (int index = 0; index < num; index++) {
                        JSONObject object = resultObject.getJSONObject(index);
                        if (object.has(KEY_ADDRESS_COMPONENT)) {

                            JSONArray addressArray = object.getJSONArray(KEY_ADDRESS_COMPONENT);
                            int addressNum = addressArray.length();
                            for (int i = 0; i < addressNum; i++) {

                                JSONObject addressObject = addressArray.getJSONObject(i);
                                if (addressObject.has(KEY_TYPE)) {
                                    boolean isCity = false;
                                    JSONArray typeObject = addressObject.getJSONArray(KEY_TYPE);
                                    int typeNum = typeObject.length();
                                    for (int j = 0; j < typeNum; j++) {
                                        String type = typeObject.getString(j);
                                        if (type.equals(VALUE_CITY)) {
                                            isCity = true;
                                            break;
                                        }
                                    }

                                    if (isCity) {
                                        if (addressObject.has(KEY_NAME)) {
                                            location = addressObject.getString(KEY_NAME);
                                            getName = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (getName) {
                            break;
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return location;
        }

        public static LatLng parseLocation(String data) {
            LatLng address = null;

            try {
                JSONObject rootObject = new JSONObject(data);
                if (rootObject.has(KEY_RESULT)) {
                    JSONArray resultObject = rootObject.getJSONArray(KEY_RESULT);
                    int num = resultObject.length();

                    for (int index = 0; index < num; index++) {
                        JSONObject object = resultObject.getJSONObject(index);
                        if (object.has(KEY_GEO)) {

                            JSONObject geoObject = object.getJSONObject(KEY_GEO);
                            if (geoObject.has(KEY_LOCATION)) {
                                double latitude = 0;
                                double longitude = 0;
                                JSONObject locObject = geoObject.getJSONObject(KEY_LOCATION);
                                if (locObject.has(KEY_LAT)) {
                                    latitude = locObject.getDouble(KEY_LAT);
                                }

                                if (locObject.has(KEY_LNG)) {
                                    longitude = locObject.getDouble(KEY_LNG);
                                }

                                address = new LatLng(latitude, longitude);
                                break;
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return address;
        }
    }
}

package com.kubotaku.android.openweathermap.lib;

/**
 * Latitude and Longitude info.
 */
public class LatLng {

    public double latitude;

    public double longitude;

    public LatLng() {
    }

    public LatLng(final double lat, final double lon) {
        latitude = lat;
        longitude = lon;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("latitude : " + latitude + ", longitude : " + longitude);
        return sb.toString();
    }
}

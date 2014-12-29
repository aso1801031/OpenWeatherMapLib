package com.kubotaku.android.openweathermap.lib.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.InstrumentationTestSuite;

import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.util.GeocodeUtil;

import java.util.Locale;

/**
 * UnitTest for GeocodeUtil class.
 */
public class GeocodeTest extends InstrumentationTestCase {

    public void test001GetNameFromLocation() {
        // Use Android Geocode class.
//        Locale locale = Locale.JAPAN;
//        LatLng latLng = new LatLng(36.4, 138.27);
//        String result = GeocodeUtil.pointToName(getContext(), locale, latLng);
//
//        assertEquals("上田市", result);
    }

    public void test001GetNameFromLocationByGoogleMapsAPI() {
        // Use Google Map Geocode api.
        Locale locale = Locale.JAPAN;
        LatLng latLng = new LatLng(36.4, 138.27);
        String result = GeocodeUtil.pointToName(locale, latLng);

        assertEquals("上田市", result);

        // wait for api limit.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test002GetLocationFromName() {
        // Use Android Geocode class.
//        Locale locale = Locale.JAPAN;
//        LatLng result = GeocodeUtil.nameToPoint(getContext(), locale, "上田市");
//
//        assertEquals(36.4019115, result.latitude);
//        assertEquals(138.2488888, result.longitude);
    }

    public void test002GetLocationFromNameByGoogleMapsAPI() {
        // Use Google Map Geocode api.
        Locale locale = Locale.JAPAN;
        LatLng result = GeocodeUtil.nameToPoint(locale, "上田市");

        assertEquals(36.4019115, result.latitude);
        assertEquals(138.2488888, result.longitude);
    }

    private Context getContext() {
        return getInstrumentation().getTargetContext();
    }
}

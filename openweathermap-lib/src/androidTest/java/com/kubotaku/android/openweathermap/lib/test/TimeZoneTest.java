package com.kubotaku.android.openweathermap.lib.test;

import android.test.InstrumentationTestCase;

import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.util.TimeZoneUtil;

import java.util.Date;
import java.util.TimeZone;

/**
 * UnitTest for TimeZoneUtil class.
 */
public class TimeZoneTest extends InstrumentationTestCase {

    /**
     * TODO:replace to your API KEY(https://developers.google.com/maps/documentation/timezone/#api_key)
     */
    private static final String API_KEY = "";

    public void test001() {

        LatLng latLng = new LatLng(36.4, 138.27);
        TimeZone timeZone = TimeZoneUtil.getTargetTimeZone(latLng, API_KEY);

        String timezoneId = timeZone.getID();

        assertEquals("Asia/Tokyo", timezoneId);
    }
}

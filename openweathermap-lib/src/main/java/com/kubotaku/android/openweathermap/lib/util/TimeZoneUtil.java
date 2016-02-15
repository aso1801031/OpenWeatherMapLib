/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 kubotaku1119
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kubotaku.android.openweathermap.lib.util;

import com.kubotaku.android.openweathermap.lib.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * TimeZone Utilities class.
 */
public class TimeZoneUtil {

    /**
     * API Base URL
     */
    private static final String API_URL = "https://maps.googleapis.com/maps/api/timezone/json?location=%1$f,%2$f&timestamp=%3$d&key=%4$s";


    public static TimeZone getTargetTimeZone(LatLng latlng, String apiKey) {
        TimeZone timeZone = null;

        try {
            Date now = new Date();
            long currentTime = now.getTime();
            String requestURL = String.format(API_URL, latlng.latitude, latlng.longitude, currentTime, apiKey);

            URL url = new URL(requestURL);
            InputStream is = url.openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();

            timeZone = TimeZoneParser.parseTimeZone(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return timeZone;
    }

    /**
     * TimeZone request result parse class.
     */
    private static final class TimeZoneParser {

        private static final String KEY_STATUS = "status";
        private static final String KEY_TIMEZONE_ID = "timeZoneId";

        public static TimeZone parseTimeZone(String data) {
            TimeZone timeZone = null;

            try {
                JSONObject jsonObject = new JSONObject(data);

                if (jsonObject.has(KEY_STATUS)) {
                    String status = jsonObject.getString(KEY_STATUS);
                    if ("OK".equals(status)) {
                        if (jsonObject.has(KEY_TIMEZONE_ID)) {
                            String timezoneId = jsonObject.getString(KEY_TIMEZONE_ID);
                            timeZone = TimeZone.getTimeZone(timezoneId);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return timeZone;
        }
    }
}

package com.kubotaku.android.openweathermap.lib;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kubotaku1119 on 2014/08/11.
 */
public class WeatherIconGetter {

    private static final String BASE_URL = "http://openweathermap.org/img/w/%1$s.png";

    public static Bitmap getWeatherIcon(final String id, final int iconSize) {
        Bitmap icon = null;
        if (id != null) {
            try {
                String requestUrl = String.format(BASE_URL, id);
                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                Bitmap iconTmp = BitmapFactory.decodeStream(in);
                if (iconSize != 0) {
                    icon = Bitmap.createScaledBitmap(iconTmp, iconSize, iconSize, true);
                    iconTmp.recycle();
                } else {
                    icon = iconTmp;
                }
                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return icon;
    }

}

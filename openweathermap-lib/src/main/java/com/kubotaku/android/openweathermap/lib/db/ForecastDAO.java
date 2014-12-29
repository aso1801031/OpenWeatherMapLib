/**
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2014 kubotaku1119
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
package com.kubotaku.android.openweathermap.lib.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;

import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;
import com.kubotaku.android.openweathermap.lib.db.ForecastDBHelper.ForecastDB;
import com.kubotaku.android.openweathermap.lib.db.ForecastDBHelper.ForecastLocationDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Forecast.
 */
public class ForecastDAO {
    /**
     * Update distance time (1hour)
     */
    private static final long UPDATE_DISTANCE_TIME_DEFAULT = (60 * 60 * 1000);

    /**
     * Minimum distance time for Debug
     */
    private static final long UPDATE_DISTANCE_TIME_MIN = 30 * 1000;

    private SQLiteDatabase mDB;

    private long mUpdateDistanceTime;

    public ForecastDAO(ForecastDBHelper helper) {
        mDB = helper.getWritableDatabase();
        mUpdateDistanceTime = UPDATE_DISTANCE_TIME_DEFAULT;
    }

    public ForecastDAO(ForecastDBHelper helper, final long updateDistance) {
        this(helper);

        if (updateDistance > UPDATE_DISTANCE_TIME_MIN) {
            mUpdateDistanceTime = updateDistance;
        }
    }

    public void close() {
        if (mDB != null) {
            mDB.close();
        }
    }

    private static final String SQL_INSERT_FORECAST_STATEMENT = "insert into " + ForecastDB.TABLE_NAME_FORECAST_INFO + " ("
            + ForecastDB.COLUMN_POS_ID + ", "
            + ForecastDB.COLUMN_WEATHER + ", "
            + ForecastDB.COLUMN_WEATHER_MAIN + ", "
            + ForecastDB.COLUMN_WEATHER_DESC + ", "
            + ForecastDB.COLUMN_TYPE + ", "
            + ForecastDB.COLUMN_TIME + ", "
            + ForecastDB.COLUMN_FORECAST_DATE + ", "
            + ForecastDB.COLUMN_ICON_ID + ", "
            + ForecastDB.COLUMN_ICON + ", "
            + ForecastDB.COLUMN_RAIN + ", "
            + ForecastDB.COLUMN_SNOW_FLAG + ", "
            + ForecastDB.COLUMN_HUMIDITY + ", "
            + ForecastDB.COLUMN_TEMP + ", "
            + ForecastDB.COLUMN_MAX_TEMP + ", "
            + ForecastDB.COLUMN_MIN_TEMP + ", "
            + ForecastDB.COLUMN_PRESSURE + ", "
            + ForecastDB.COLUMN_WIND_SPEED + ", "
            + ForecastDB.COLUMN_WIND_DEG + ", "
            + ForecastDB.COLUMN_CLOUDS + ""
            + ") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SQL_INSERT_LOCATION_STATEMENT = "insert into " + ForecastLocationDB.TABLE_NAME_LOCATION_INFO + " ("
            + ForecastLocationDB.COLUMN_NAME + ", "
            + ForecastLocationDB.COLUMN_POS_ID + ", "
            + ForecastLocationDB.COLUMN_LAT + ", "
            + ForecastLocationDB.COLUMN_LON + ", "
            + ForecastLocationDB.COLUMN_TYPE + ", "
            + ForecastLocationDB.COLUMN_TIME + ""
            + ") values(?, ?, ?, ?, ?, ?);";

    public long insert(final List<WeatherInfo> infoList) {
        long ret = -1;

        if ((infoList == null) || (infoList.size() == 0)) {
            return ret;
        }

        mDB.beginTransaction();

        try {
            // insert location information.
            SQLiteStatement statementLoc = mDB.compileStatement(SQL_INSERT_LOCATION_STATEMENT);
            WeatherInfo topInfo = infoList.get(0);
            statementLoc.bindString(1, topInfo.getName());
            statementLoc.bindLong(2, topInfo.getId());

            double latitude = 0;
            double longitude = 0;
            if (topInfo.getLatLng() != null) {
                latitude = topInfo.getLatLng().latitude;
                longitude = topInfo.getLatLng().longitude;
            }
            statementLoc.bindDouble(3, latitude);
            statementLoc.bindDouble(4, longitude);
            statementLoc.bindLong(5, topInfo.getForecastType());

            long now = System.currentTimeMillis();
            statementLoc.bindLong(6, now);
            ret = statementLoc.executeInsert();

            if (ret != -1) {
                // insert forecast list.
                ret = insertForecast(infoList);
            }

            if (ret != -1) {
                mDB.setTransactionSuccessful();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDB.endTransaction();
        }


        return ret;
    }

    private long insertForecast(List<WeatherInfo> infoList) {
        long ret = -1;

        SQLiteStatement statement = mDB.compileStatement(SQL_INSERT_FORECAST_STATEMENT);

        for (WeatherInfo info : infoList) {

            statement.bindLong(1, info.getId());
            statement.bindLong(2, info.getWeatherId());
            statement.bindString(3, info.getWeatherMain());
            statement.bindString(4, info.getWeatherDescription());
            statement.bindLong(5, info.getForecastType());
            statement.bindLong(6, info.getTime());
            statement.bindString(7, info.getForecastDate());
            statement.bindString(8, info.getIconName());

            Bitmap icon = info.getIcon();
            if (icon != null) {
                byte[] iconBlob = DBUtil.convertBitmapToBlob(icon);
                statement.bindBlob(9, iconBlob);
            }

            statement.bindDouble(10, info.getRain());

            int snowFlag = info.isSnow() ? 1 : 0;
            statement.bindLong(11, snowFlag);

            statement.bindLong(12, info.getHumidity());
            statement.bindDouble(13, info.getCurrentTemp());
            statement.bindDouble(14, info.getMaxTemp());
            statement.bindDouble(15, info.getMinTemp());
            statement.bindLong(16, info.getPressure());
            statement.bindDouble(17, info.getWindSpeed());
            statement.bindLong(18, info.getWindDeg());
            statement.bindLong(19, info.getClouds());

            ret = statement.executeInsert();
            if (ret == -1) {
                break;
            }
        }

        return ret;
    }

    private static final String SQL_UPDATE_LOCATION_STATEMENT_BY_ID = "update " + ForecastLocationDB.TABLE_NAME_LOCATION_INFO + " set "
            + ForecastLocationDB.COLUMN_NAME + "=?, "
            + ForecastLocationDB.COLUMN_POS_ID + "=?, "
            + ForecastLocationDB.COLUMN_LAT + "=?, "
            + ForecastLocationDB.COLUMN_LON + "=?, "
            + ForecastLocationDB.COLUMN_TYPE + "=?, "
            + ForecastLocationDB.COLUMN_TIME + "=? "
            + "where " + ForecastLocationDB.COLUMN_POS_ID + "=?;";

    public long update(List<WeatherInfo> infoList) {
        long ret = -1;

        SQLiteStatement statement = mDB.compileStatement(SQL_UPDATE_LOCATION_STATEMENT_BY_ID);
        WeatherInfo topInfo = infoList.get(0);
        statement.bindString(1, topInfo.getName());
        statement.bindLong(2, topInfo.getId());
        double latitude = 0;
        double longitude = 0;
        if (topInfo.getLatLng() != null) {
            latitude = topInfo.getLatLng().latitude;
            longitude = topInfo.getLatLng().longitude;
        }
        statement.bindDouble(3, latitude);
        statement.bindDouble(4, longitude);
        statement.bindLong(5, topInfo.getForecastType());

        long now = System.currentTimeMillis();
        statement.bindLong(6, now);
        ret = statement.executeUpdateDelete();

        if (ret != -1) {
            // delete exist data
            deleteTargetPosForecast(topInfo.getId());
        }

        if (ret != -1) {
            // inseret new data
            insertForecast(infoList);
        }

        return ret;
    }

    public long deleteTargetPosForecast(final int posId) {
        long ret = -1;

        String where = ForecastDB.COLUMN_POS_ID + " = \"" + posId + "\"";

        ret = mDB.delete(
                ForecastDB.TABLE_NAME_FORECAST_INFO,
                where,
                null);

        return ret;
    }

    public void deleteAll() {
        mDB.delete(ForecastLocationDB.TABLE_NAME_LOCATION_INFO, null, null);
        mDB.delete(ForecastDB.TABLE_NAME_FORECAST_INFO, null, null);
    }

    public boolean needUpdate(final long currentTime, final WeatherInfo oldInfo) {
        boolean needUpdate = false;
        if (oldInfo != null) {
            long time = oldInfo.getTime();
            if ((currentTime - time) >= mUpdateDistanceTime) {
                needUpdate = true;
            }
        }
        return needUpdate;
    }

    public WeatherInfo findTargetLocation(final LatLng latlng, final int forecastType) {
        StringBuilder sb = new StringBuilder();
        sb.append(ForecastLocationDB.COLUMN_LAT);
        sb.append(" = \"");
        sb.append(latlng.latitude);
        sb.append("\"");
        sb.append(" and ");
        sb.append(ForecastLocationDB.COLUMN_LON);
        sb.append(" = \"");
        sb.append(latlng.longitude);
        sb.append("\"");
        sb.append(" and ");
        sb.append(ForecastLocationDB.COLUMN_TYPE);
        sb.append(" = \"");
        sb.append(forecastType);
        sb.append("\"");

        String selection = sb.toString();

        WeatherInfo info = findTargetLocationInfo(selection);

        return info;
    }

    public WeatherInfo findTargetLocation(final String name, final int forecastType) {
        StringBuilder sb = new StringBuilder();
        sb.append(ForecastLocationDB.COLUMN_NAME);
        sb.append(" = \"");
        sb.append(name);
        sb.append("\"");
        sb.append(" and ");
        sb.append(ForecastLocationDB.COLUMN_TYPE);
        sb.append(" = \"");
        sb.append(forecastType);
        sb.append("\"");

        String selection = sb.toString();

        WeatherInfo info = findTargetLocationInfo(selection);

        return info;
    }

    public WeatherInfo findTargetLocation(final int posId, final int forecastType) {
        StringBuilder sb = new StringBuilder();
        sb.append(ForecastLocationDB.COLUMN_POS_ID);
        sb.append(" = \"");
        sb.append(posId);
        sb.append("\"");
        sb.append(" and ");
        sb.append(ForecastLocationDB.COLUMN_TYPE);
        sb.append(" = \"");
        sb.append(forecastType);
        sb.append("\"");

        String selection = sb.toString();

        WeatherInfo info = findTargetLocationInfo(selection);

        return info;
    }

    private WeatherInfo findTargetLocationInfo(final String selection) {
        WeatherInfo info = null;
        Cursor cur = null;

        try {
            cur = mDB.query(ForecastLocationDB.TABLE_NAME_LOCATION_INFO,
                    ForecastLocationDB.FORECAST_LOCATION_COLUMNS,
                    selection,
                    null,
                    null,
                    null,
                    null,
                    "0,1");

            if (cur.moveToFirst()) {
                info = new WeatherInfo();
                info.setName(cur.getString(cur.getColumnIndex(ForecastLocationDB.COLUMN_NAME)));
                info.setId(cur.getInt(cur.getColumnIndex(ForecastLocationDB.COLUMN_POS_ID)));

                double latitude = cur.getDouble(cur.getColumnIndex(ForecastLocationDB.COLUMN_LAT));
                double longitude = cur.getDouble(cur.getColumnIndex(ForecastLocationDB.COLUMN_LON));
                LatLng latLng = new LatLng(latitude, longitude);
                info.setLatLng(latLng);

                info.setForecastType(cur.getInt(cur.getColumnIndex(ForecastLocationDB.COLUMN_TYPE)));
                info.setTime(cur.getLong(cur.getColumnIndex(ForecastLocationDB.COLUMN_TIME)));
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
        }

        return info;
    }

    public List<WeatherInfo> getTargetForecast(final WeatherInfo locationInfo, final int forecastType) {
        List<WeatherInfo> forecast = null;

        Cursor cur = null;

        StringBuilder sb = new StringBuilder();
        sb.append(ForecastDB.COLUMN_POS_ID);
        sb.append(" = \"");
        sb.append(locationInfo.getId());
        sb.append("\"");
        sb.append(" and ");
        sb.append(ForecastDB.COLUMN_TYPE);
        sb.append(" = \"");
        sb.append(forecastType);
        sb.append("\"");

        String selection = sb.toString();

        try {
            cur = mDB.query(ForecastDB.TABLE_NAME_FORECAST_INFO,
                    ForecastDB.FORECAST_COLUMNS,
                    selection,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cur.moveToFirst()) {
                forecast = new ArrayList<WeatherInfo>();

                do {
                    WeatherInfo info = parseWeatherInfo(cur);
                    info.setLatLng(locationInfo.getLatLng());
                    info.setName(locationInfo.getName());
                    forecast.add(info);
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }

        return forecast;
    }

    public static WeatherInfo parseWeatherInfo(Cursor cur) {
        WeatherInfo info = new WeatherInfo();
        info.setId(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_POS_ID)));
        info.setWeatherId(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_WEATHER)));
        info.setWeatherMain(cur.getString(cur.getColumnIndex(ForecastDB.COLUMN_WEATHER_MAIN)));
        info.setWeatherDescription(cur.getString(cur.getColumnIndex(ForecastDB.COLUMN_WEATHER_DESC)));
        info.setTime(cur.getLong(cur.getColumnIndex(ForecastDB.COLUMN_TIME)));
        info.setForecastDate(cur.getString(cur.getColumnIndex(ForecastDB.COLUMN_FORECAST_DATE)));
        info.setIconName(cur.getString(cur.getColumnIndex(ForecastDB.COLUMN_ICON_ID)));

        byte[] bitmapBlob = cur.getBlob(cur.getColumnIndex(ForecastDB.COLUMN_ICON));
        Bitmap icon = DBUtil.convertBlobToBitmap(bitmapBlob);
        info.setIcon(icon);

        info.setRain(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_RAIN)));

        int snowFlag = cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_SNOW_FLAG));
        boolean showSnow = (snowFlag == ForecastDB.SNOW_FLAG_TRUE) ? true : false;
        info.setSnow(showSnow);

        info.setHumidity(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_HUMIDITY)));
        info.setCurrentTemp(cur.getFloat(cur.getColumnIndex(ForecastDB.COLUMN_TEMP)));
        info.setMaxTemp(cur.getFloat(cur.getColumnIndex(ForecastDB.COLUMN_MAX_TEMP)));
        info.setMinTemp(cur.getFloat(cur.getColumnIndex(ForecastDB.COLUMN_MIN_TEMP)));
        info.setPressure(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_PRESSURE)));
        info.setWindSpeed(cur.getFloat(cur.getColumnIndex(ForecastDB.COLUMN_WIND_SPEED)));
        info.setWindDeg(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_WIND_DEG)));
        info.setClouds(cur.getInt(cur.getColumnIndex(ForecastDB.COLUMN_CLOUDS)));

        return info;
    }

}

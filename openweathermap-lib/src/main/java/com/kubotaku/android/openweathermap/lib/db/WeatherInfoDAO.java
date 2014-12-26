package com.kubotaku.android.openweathermap.lib.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;

import java.io.ByteArrayOutputStream;

/**
 * Data Access Object for Weather information.
 */
public class WeatherInfoDAO {

    private static final long UPDATE_DISTANCE_TIME_DEFAULT = 10 * 60 * 1000;

    /** Minimum distance time for Debug */
    private static final long UPDATE_DISTANCE_TIME_MIN = 30 * 1000;

    private SQLiteDatabase mDB;

    private long mUpdateDistanceTime;

    public WeatherInfoDAO(WeatherDBHelper helper) {
        mDB = helper.getWritableDatabase();
        mUpdateDistanceTime = UPDATE_DISTANCE_TIME_DEFAULT;
    }

    public WeatherInfoDAO(WeatherDBHelper helper, final long updateDistance) {
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

    private static final String SQL_INSERT_STATEMENT = "insert into " + WeatherDBHelper.TABLE_NAME_WEATHER_INFO + " ("
            + WeatherDBHelper.COLUMN_NAME + ", "
            + WeatherDBHelper.COLUMN_POS_ID + ", "
            + WeatherDBHelper.COLUMN_LAT + ", "
            + WeatherDBHelper.COLUMN_LON + ", "
            + WeatherDBHelper.COLUMN_WEATHER + ", "
            + WeatherDBHelper.COLUMN_WEATHER_MAIN + ", "
            + WeatherDBHelper.COLUMN_WEATHER_DESC + ", "
            + WeatherDBHelper.COLUMN_TIME + ", "
            + WeatherDBHelper.COLUMN_ICON_ID + ", "
            + WeatherDBHelper.COLUMN_ICON + ", "
            + WeatherDBHelper.COLUMN_RAIN + ", "
            + WeatherDBHelper.COLUMN_SNOW_FLAG + ", "
            + WeatherDBHelper.COLUMN_HUMIDITY + ", "
            + WeatherDBHelper.COLUMN_TEMP + ", "
            + WeatherDBHelper.COLUMN_MAX_TEMP + ", "
            + WeatherDBHelper.COLUMN_MIN_TEMP + ", "
            + WeatherDBHelper.COLUMN_PRESSURE + ", "
            + WeatherDBHelper.COLUMN_WIND_SPEED + ", "
            + WeatherDBHelper.COLUMN_WIND_DEG + ", "
            + WeatherDBHelper.COLUMN_CLOUDS + ""
            + ") values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public long insert(final WeatherInfo info) {
        long ret = -1;

        SQLiteStatement statement = mDB.compileStatement(SQL_INSERT_STATEMENT);

        statement.bindString(1, info.getName());
        statement.bindLong(2, info.getId());
        statement.bindDouble(3, info.getLatLng().latitude);
        statement.bindDouble(4, info.getLatLng().longitude);
        statement.bindLong(5, info.getWeatherId());
        statement.bindString(6, info.getWeatherMain());
        statement.bindString(7, info.getWeatherDescription());
        statement.bindLong(8, info.getTime());
        statement.bindString(9, info.getIconName());

        Bitmap icon = info.getIcon();
        if (icon != null) {
            byte[] iconBlob = convertBitmapToBlob(icon);
            statement.bindBlob(10, iconBlob);
        }

        statement.bindDouble(11, info.getRain());

        int snowFlag = info.isSnow() ? 1 : 0;
        statement.bindLong(12, snowFlag);

        statement.bindLong(13, info.getHumidity());
        statement.bindDouble(14, info.getCurrentTemp());
        statement.bindDouble(15, info.getMaxTemp());
        statement.bindDouble(16, info.getMinTemp());
        statement.bindLong(17, info.getPressure());
        statement.bindDouble(18, info.getWindSpeed());
        statement.bindLong(19, info.getWindDeg());
        statement.bindLong(20, info.getClouds());

        ret = statement.executeInsert();

        return ret;
    }

    private static final String SQL_UPDATE_STATEMENT = "update " + WeatherDBHelper.TABLE_NAME_WEATHER_INFO + " set "
            + WeatherDBHelper.COLUMN_NAME + "=?, "
            + WeatherDBHelper.COLUMN_POS_ID + "=?, "
            + WeatherDBHelper.COLUMN_LAT + "=?, "
            + WeatherDBHelper.COLUMN_LON + "=?, "
            + WeatherDBHelper.COLUMN_WEATHER + "=?, "
            + WeatherDBHelper.COLUMN_WEATHER_MAIN + "=?, "
            + WeatherDBHelper.COLUMN_WEATHER_DESC + "=?, "
            + WeatherDBHelper.COLUMN_TIME + "=?, "
            + WeatherDBHelper.COLUMN_ICON_ID + "=?, "
            + WeatherDBHelper.COLUMN_ICON + "=?, "
            + WeatherDBHelper.COLUMN_RAIN + "=?, "
            + WeatherDBHelper.COLUMN_SNOW_FLAG + "=?, "
            + WeatherDBHelper.COLUMN_HUMIDITY + "=?, "
            + WeatherDBHelper.COLUMN_TEMP + "=?, "
            + WeatherDBHelper.COLUMN_MAX_TEMP + "=?, "
            + WeatherDBHelper.COLUMN_MIN_TEMP + "=?, "
            + WeatherDBHelper.COLUMN_PRESSURE + "=?, "
            + WeatherDBHelper.COLUMN_WIND_SPEED + "=?, "
            + WeatherDBHelper.COLUMN_WIND_DEG + "=?, "
            + WeatherDBHelper.COLUMN_CLOUDS + "=? "
            + "where " + WeatherDBHelper.COLUMN_POS_ID +"=?;";

    public long update(final WeatherInfo info) {
        long ret = -1;

        SQLiteStatement statement = mDB.compileStatement(SQL_UPDATE_STATEMENT);

        statement.bindString(1, info.getName());
        statement.bindLong(2, info.getId());

        double latitude = 0;
        double longitude = 0;
        if (info.getLatLng() != null) {
            latitude = info.getLatLng().latitude;
            longitude = info.getLatLng().longitude;
        }
        statement.bindDouble(3, latitude);
        statement.bindDouble(4, longitude);

        statement.bindLong(5, info.getWeatherId());
        statement.bindString(6, info.getWeatherMain());
        statement.bindString(7, info.getWeatherDescription());
        statement.bindLong(8, info.getTime());
        statement.bindString(9, info.getIconName());

        Bitmap icon = info.getIcon();
        if (icon != null) {
            byte[] iconBlob = convertBitmapToBlob(icon);
            statement.bindBlob(10, iconBlob);
        }

        statement.bindDouble(11, info.getRain());

        int snowFlag = info.isSnow() ? 1 : 0;
        statement.bindLong(12, snowFlag);

        statement.bindLong(13, info.getHumidity());
        statement.bindDouble(14, info.getCurrentTemp());
        statement.bindDouble(15, info.getMaxTemp());
        statement.bindDouble(16, info.getMinTemp());
        statement.bindLong(17, info.getPressure());
        statement.bindDouble(18, info.getWindSpeed());
        statement.bindLong(19, info.getWindDeg());
        statement.bindLong(20, info.getClouds());

        statement.bindLong(21, info.getId());

        ret = statement.executeUpdateDelete();

        return ret;
    }

    public long deleteAll() {
        return mDB.delete(WeatherDBHelper.TABLE_NAME_WEATHER_INFO, null, null);
    }

    public WeatherInfo findTargetInfo(final LatLng latlng) {
        StringBuilder sb = new StringBuilder();
        sb.append(WeatherDBHelper.COLUMN_LAT);
        sb.append(" = \"");
        sb.append(latlng.latitude);
        sb.append("\"");
        sb.append(" and ");
        sb.append(WeatherDBHelper.COLUMN_LON);
        sb.append(" = \"");
        sb.append(latlng.longitude);
        sb.append("\"");

        String selection = sb.toString();

        WeatherInfo info = findTargetWeather(selection);

        return info;
    }

    public WeatherInfo findTargetInfo(final String name) {
        String selection = WeatherDBHelper.COLUMN_NAME + " = \"" + name + "\"";

        WeatherInfo info = findTargetWeather(selection);

        return info;
    }

    public WeatherInfo findTargetInfo(final int posId) {
        String selection = WeatherDBHelper.COLUMN_POS_ID + " = \"" + posId + "\"";

        WeatherInfo info = findTargetWeather(selection);

        return info;
    }

    private WeatherInfo findTargetWeather(final String selection) {
        WeatherInfo info = null;
        Cursor cur = null;

        try {
            cur = mDB.query(WeatherDBHelper.TABLE_NAME_WEATHER_INFO,
                    WeatherDBHelper.WEATHER_INFO_COLUMNS,
                    selection,
                    null,
                    null,
                    null,
                    null,
                    "0,1");

            if (cur.moveToNext()) {
                info = parseWeatherInfo(cur);
            }
        }
        finally {
            if (cur != null) {
                cur.close();
            }
        }

        return info;
    }

    public boolean existTargetInfo(final String name) {
        boolean exist = false;

        WeatherInfo info = findTargetInfo(name);
        if (info != null) {
            exist = true;
        }

        return exist;
    }

    public boolean existTargetInfo(final int id) {
        boolean exist = false;

        WeatherInfo info = findTargetInfo(id);
        if (info != null) {
            exist = true;
        }

        return exist;
    }

    public boolean existTargetInfo(final LatLng latlng) {
        boolean exist = false;

        WeatherInfo info = findTargetInfo(latlng);
        if (info != null) {
            exist = true;
        }

        return exist;
    }

    public boolean needUpdate(final String name) {
        boolean needUpdate = true;

        long currentTime = System.currentTimeMillis();
        WeatherInfo info = findTargetInfo(name);
        needUpdate = needUpdate(currentTime, info);

        return needUpdate;
    }

    public boolean needUpdate(final LatLng latlng) {
        boolean needUpdate = true;

        long currentTime = System.currentTimeMillis();
        WeatherInfo info = findTargetInfo(latlng);
        needUpdate = needUpdate(currentTime, info);

        return needUpdate;
    }

    public boolean needUpdate(final int id) {
        boolean needUpdate = true;

        long currentTime = System.currentTimeMillis();
        WeatherInfo info = findTargetInfo(id);
        needUpdate = needUpdate(currentTime, info);

        return needUpdate;
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

    private WeatherInfo parseWeatherInfo(Cursor cur) {
        WeatherInfo info = new WeatherInfo();
        info.setId(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_POS_ID)));
        info.setName(cur.getString(cur.getColumnIndex(WeatherDBHelper.COLUMN_NAME)));

        double lat = cur.getDouble(cur.getColumnIndex(WeatherDBHelper.COLUMN_LAT));
        double lon = cur.getDouble(cur.getColumnIndex(WeatherDBHelper.COLUMN_LON));
        LatLng latlng = new LatLng(lat, lon);
        info.setLatLng(latlng);

        info.setWeatherId(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_WEATHER)));
        info.setWeatherMain(cur.getString(cur.getColumnIndex(WeatherDBHelper.COLUMN_WEATHER_MAIN)));
        info.setWeatherDescription(cur.getString(cur.getColumnIndex(WeatherDBHelper.COLUMN_WEATHER_DESC)));
        info.setTime(cur.getLong(cur.getColumnIndex(WeatherDBHelper.COLUMN_TIME)));
        info.setIconName(cur.getString(cur.getColumnIndex(WeatherDBHelper.COLUMN_ICON_ID)));

        byte[] bitmapBlob = cur.getBlob(cur.getColumnIndex(WeatherDBHelper.COLUMN_ICON));
        Bitmap icon = convertBlobToBitmap(bitmapBlob);
        info.setIcon(icon);

        info.setRain(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_RAIN)));

        int snowFlag = cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_SNOW_FLAG));
        boolean showSnow = (snowFlag == WeatherDBHelper.SNOW_FLAG_TRUE) ? true : false;
        info.setSnow(showSnow);

        info.setHumidity(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_HUMIDITY)));
        info.setCurrentTemp(cur.getFloat(cur.getColumnIndex(WeatherDBHelper.COLUMN_TEMP)));
        info.setMaxTemp(cur.getFloat(cur.getColumnIndex(WeatherDBHelper.COLUMN_MAX_TEMP)));
        info.setMinTemp(cur.getFloat(cur.getColumnIndex(WeatherDBHelper.COLUMN_MIN_TEMP)));
        info.setPressure(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_PRESSURE)));
        info.setWindSpeed(cur.getFloat(cur.getColumnIndex(WeatherDBHelper.COLUMN_WIND_SPEED)));
        info.setWindDeg(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_WIND_DEG)));
        info.setClouds(cur.getInt(cur.getColumnIndex(WeatherDBHelper.COLUMN_CLOUDS)));

        return info;
    }

    private static Bitmap convertBlobToBitmap(final byte[] blob) {
        Bitmap bitmap = null;
        if (blob != null) {
            BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return bitmap;
    }

    private static byte[] convertBitmapToBlob(final Bitmap bitmap) {
        byte[] blob = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                byteArrayOutputStream)) {
            blob = byteArrayOutputStream.toByteArray();
        }
        return blob;
    }
}

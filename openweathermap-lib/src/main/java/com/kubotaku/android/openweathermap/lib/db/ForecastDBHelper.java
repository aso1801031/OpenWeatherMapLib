package com.kubotaku.android.openweathermap.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Database Helper class for Forecast.
 */
public class ForecastDBHelper extends SQLiteOpenHelper {

    public static final String DB_FORECAST = "forecast.db";

    private static final int DB_VERSION = 1;

    public static class ForecastDB implements BaseColumns {
        public static final String TABLE_NAME_FORECAST_INFO = "forecast_info";

        public static final String COLUMN_POS_ID = "pos_id";
        public static final String COLUMN_WEATHER = "weather_id";
        public static final String COLUMN_WEATHER_MAIN = "weather_main";
        public static final String COLUMN_WEATHER_DESC = "weather_desc";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ICON_ID = "icon_id";
        public static final String COLUMN_ICON = "icon_data";
        public static final String COLUMN_RAIN = "rain";
        public static final String COLUMN_SNOW_FLAG = "snow_flag";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_TEMP = "temperature";
        public static final String COLUMN_MAX_TEMP = "max_temp";
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DEG = "wind_deg";
        public static final String COLUMN_CLOUDS = "clouds";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_FORECAST_DATE = "forecast_date";

        public static final int SNOW_FLAG_TRUE = 1;

        public static final String SQL_CREATE_FORECAST_TABLE = "create table " + TABLE_NAME_FORECAST_INFO + " ("
                + _ID + " integer primary key autoincrement, "
                + COLUMN_POS_ID + " integer, "
                + COLUMN_WEATHER + " integer, "
                + COLUMN_WEATHER_MAIN + " text, "
                + COLUMN_WEATHER_DESC + " text, "
                + COLUMN_TYPE + " integer, "
                + COLUMN_TIME + " integer, "
                + COLUMN_FORECAST_DATE + " text, "
                + COLUMN_ICON_ID + " text, "
                + COLUMN_ICON + " blob, "
                + COLUMN_RAIN + " real, "
                + COLUMN_SNOW_FLAG + " integer, "
                + COLUMN_HUMIDITY + " integer, "
                + COLUMN_TEMP + " real, "
                + COLUMN_MAX_TEMP + " real, "
                + COLUMN_MIN_TEMP + " real, "
                + COLUMN_PRESSURE + " integer, "
                + COLUMN_WIND_SPEED + " real, "
                + COLUMN_WIND_DEG + " integer, "
                + COLUMN_CLOUDS + " integer);";

        private static final String SQL_DROP_FORECAST_TABLE = "drop table if exists " + TABLE_NAME_FORECAST_INFO;

        public static final String[] FORECAST_COLUMNS = {
                _ID, COLUMN_POS_ID, COLUMN_WEATHER, COLUMN_WEATHER_MAIN, COLUMN_WEATHER_DESC,
                COLUMN_TYPE, COLUMN_TIME, COLUMN_FORECAST_DATE, COLUMN_ICON_ID, COLUMN_ICON, COLUMN_RAIN,
                COLUMN_SNOW_FLAG, COLUMN_HUMIDITY, COLUMN_TEMP, COLUMN_MAX_TEMP, COLUMN_MIN_TEMP,
                COLUMN_PRESSURE, COLUMN_WIND_SPEED, COLUMN_WIND_DEG, COLUMN_CLOUDS
        };
    }

    public static class ForecastLocationDB implements BaseColumns {
        public static final String TABLE_NAME_LOCATION_INFO = "forecast_location";

        public static final String COLUMN_NAME = "pos_name";
        public static final String COLUMN_POS_ID = "pos_id";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LON = "longitude";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";

        private static final String SQL_CREATE_LOCATION_TABLE = "create table " + TABLE_NAME_LOCATION_INFO + " ("
                + _ID + " integer primary key autoincrement, "
                + COLUMN_NAME + " text not null, "
                + COLUMN_POS_ID + " integer, "
                + COLUMN_LAT + " real, "
                + COLUMN_LON + " real, "
                + COLUMN_TYPE + " integer, "
                + COLUMN_TIME + " integer);";

        private static final String SQL_DROP_LOCATION_TABLE = "drop table if exists " + TABLE_NAME_LOCATION_INFO;

        public static final String[] FORECAST_LOCATION_COLUMNS = {
                _ID, COLUMN_NAME, COLUMN_POS_ID, COLUMN_LAT, COLUMN_LON, COLUMN_TYPE, COLUMN_TIME
        };
    }

    public ForecastDBHelper(final Context context) {
        super(context, DB_FORECAST, null, DB_VERSION);
    }

    private void initialize(SQLiteDatabase db) {
        dropAllTable(db);
        createAllTable(db);
    }

    private void dropAllTable(SQLiteDatabase db) {
        db.execSQL(ForecastDB.SQL_DROP_FORECAST_TABLE);
        db.execSQL(ForecastLocationDB.SQL_DROP_LOCATION_TABLE);
    }

    private void createAllTable(SQLiteDatabase db) {
        db.execSQL(ForecastDB.SQL_CREATE_FORECAST_TABLE);
        db.execSQL(ForecastLocationDB.SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        initialize(db);
    }
}

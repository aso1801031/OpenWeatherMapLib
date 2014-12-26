package com.kubotaku.android.openweathermap.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.kubotaku.android.openweathermap.lib.db.WeatherDBHelper;
import com.kubotaku.android.openweathermap.lib.db.WeatherInfoDAO;
import com.kubotaku.android.openweathermap.lib.util.GeocodeUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Weather Getter class.
 * <p>
 *
 * <p/>
 */
public class WeatherGetter implements IWeatherGetter {

    private static final String TAG = WeatherGetter.class.getSimpleName();

    public static IWeatherGetter getInstance(final Context context, final String apiKey) throws Exception {
        if (apiKey == null) {
            throw new Exception("API Key is must be NOT null.");
        }

        if (apiKey.isEmpty()) {
            throw new Exception("API Key is must set.");
        }

        IWeatherGetter instance = new WeatherGetter(context, apiKey);
        return instance;
    }

    private Context mContext;

    private OnWeatherGetListener mListener;

    private String mApiKey;

    private Locale mLocale;

    private LatLng mLatLng;

    private String mName;

    private int mId;

    private Region mRegion;

    private boolean mEnableWeatherIcon;

    private int mWeatherIconSize;

    private long mUpdateDistanceTime;

    private boolean mUseGeocodeForGetLocation;

    private WeatherGetter(final Context context, final String apiKey) {
        mContext = context;
        mApiKey = apiKey;
        mLocale = Locale.getDefault();
        mId = -1;
        mUseGeocodeForGetLocation = false;
    }

    @Override
    public void setLocale(Locale locale) {
        mLocale = locale;
    }

    @Override
    public void setLatLng(LatLng latlng) {
        if (mLatLng == null) {
            mLatLng = new LatLng();
        }
        mLatLng = latlng;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public void setEnableWeatherIcon(boolean enable) {
        mEnableWeatherIcon = enable;
    }

    @Override
    public void setWeatherIconSize(int size) {
        mWeatherIconSize = size;
    }

    @Override
    public void setUpdateDistanceTime(long updateDistanceTime) {
        mUpdateDistanceTime = updateDistanceTime;
    }

    @Override
    public void setUseGeocodeForGetLocation(boolean useGeocode) {
        mUseGeocodeForGetLocation = useGeocode;
    }

    @Override
    public void setRegion(LatLng northeast, LatLng southwest) {
        if (mRegion == null) {
            mRegion = new Region();
        }
        mRegion.northeast = northeast;
        mRegion.southwest = southwest;
    }

    @Override
    public void getWeatherInfo(OnWeatherGetListener listener) {
        mListener = listener;

        WeatherGetTask task = new WeatherGetTask();
        task.execute(new Void[]{});
    }

    @Override
    public void getWeatherInfo(int count, OnWeatherGetListener listener) {
        mListener = listener;
    }


    private class WeatherGetterImpl {
        /** API */
        private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?lat=%1$f&lon=%2$f&APPID=%3$s";

        /** API by id */
        private static final String WEATHER_API_ID = "http://api.openweathermap.org/data/2.5/weather?id=%1$d&APPID=%2$s";

        /** API by name */
        private static final String WEATHER_API_NAME = "http://api.openweathermap.org/data/2.5/weather?q=%1$s&APPID=%2$s";

        /** API find target position */
        private static final String WEATHER_API_FIND = "http://api.openweathermap.org/data/2.5/find?lat=%1$f&lon=%2$f&cnt=%3$d&APPID=%4$s";

        /** API use bbox */
        private static final String WEAHTER_API_REGION = "http://api.openweathermap.org/data/2.5/city?bbox=%1$f,%2$f,%3$f,%4$f,%5$d&APPID=%6$s";

        private String mRequestURL;

        private boolean mNeedUpdate = false;

        private boolean mExistData = false;

        public WeatherInfo getWeatherInfo() {

            // First, check local database data
            WeatherInfo info = getWeatherInfoFromLocal();
            if (info == null) {
                info = getWeatherInfoFromService();
            }

            if (info != null) {
                saveLocal(info);
            }

            return info;
        }

        private WeatherInfo getWeatherInfoFromLocal() {
            WeatherInfo info = null;
            mNeedUpdate = false;

            long now = System.currentTimeMillis();
            WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
            WeatherInfoDAO dao = new WeatherInfoDAO(dbHelper, mUpdateDistanceTime);

            if (mLatLng != null) {
                info = dao.findTargetInfo(mLatLng);
            }

            if ((info != null) && (mName != null)) {
                info = dao.findTargetInfo(mName);
            }

            if ((info != null) && (mId != -1)) {
                info = dao.findTargetInfo(mId);
            }

            if (info != null) {
                mExistData = true;
                if (dao.needUpdate(now, info)) {
                    info = null;
                    mNeedUpdate = true;
                }
            }

            if (mExistData && !mNeedUpdate) {
                Log.d(TAG, "Get weather info from local.");
            }

            dao.close();
            return info;
        }

        private WeatherInfo getWeatherInfoFromService() {
            WeatherInfo info = null;

            createRequestURL();

            try {
                URL url = new URL(mRequestURL);
                InputStream is = url.openConnection().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                String data = sb.toString();

                // Parse Weather info form JSON format
                info = WeatherParser.parseWeather(data)[0];

                // Get weather icon from service.
                if (mEnableWeatherIcon) {
                    Bitmap icon = WeatherIconGetter.getWeatherIcon(info.getIconName(), mWeatherIconSize);
                    info.setIcon(icon);
                }

                if (mLatLng != null) {
                    info.setLatLng(mLatLng);
                }

                // Get city name from coordinate for localize.
                LatLng latlng = info.getLatLng();
                String cityName = GeocodeUtil.pointToName(mContext, mLocale, latlng);
                if (cityName != null && cityName.length() != 0) {
                    info.setName(cityName);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return info;
        }

        private void createRequestURL() {
            mRequestURL = null;

            if ((mName != null) && mUseGeocodeForGetLocation) {
                mLatLng = getLocationFromName(mName);
            }

            if (mLatLng != null) {
                mRequestURL = String.format(WEATHER_API, mLatLng.latitude, mLatLng.longitude, mApiKey);
            }

            if ((mRequestURL == null) && (mName != null)) {
                mRequestURL = String.format(WEATHER_API_NAME, mName, mApiKey);
            }

            if ((mRequestURL == null) && (mId != -1)) {
                mRequestURL = String.format(WEATHER_API_ID, mId, mApiKey);
            }
        }

        private LatLng getLocationFromName(String name) {
            LatLng latLng = GeocodeUtil.nameToPoint(mContext, mLocale, name);
            return latLng;
        }

        private void saveLocal(final WeatherInfo info) {
            WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
            WeatherInfoDAO dao = new WeatherInfoDAO(dbHelper);

            if (mNeedUpdate) {
                dao.update(info);
            } else {
                if (!mExistData) {
                    dao.insert(info);
                }
            }

            dao.close();
        }
    }

    private class WeatherGetTask extends AsyncTask<Void, Void, ArrayList<WeatherInfo>> {

        @Override
        protected ArrayList<WeatherInfo> doInBackground(Void... params) {
            ArrayList<WeatherInfo> infoList = null;

            WeatherGetterImpl weatherGetter = new WeatherGetterImpl();
            WeatherInfo info = weatherGetter.getWeatherInfo();
            if (info != null) {
                infoList = new ArrayList<WeatherInfo>();
                infoList.add(info);
            }

            return infoList;
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherInfo> result) {
            if (mListener != null) {
                mListener.onGetWeatherInfo(result);
            }
        }
    }
}

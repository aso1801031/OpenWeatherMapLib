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
package com.kubotaku.android.openweathermap.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.kubotaku.android.openweathermap.lib.db.ForecastDAO;
import com.kubotaku.android.openweathermap.lib.db.ForecastDBHelper;
import com.kubotaku.android.openweathermap.lib.util.GeocodeUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Forecast Getter class.
 */
public class ForecastGetter implements IForecastGetter {

    private static final String TAG = ForecastGetter.class.getSimpleName();

    public static IForecastGetter getInstance(final Context context, final String apiKey) throws Exception {
        if (apiKey == null) {
            throw new Exception("API Key is must be NOT null.");
        }

        if (apiKey.isEmpty()) {
            throw new Exception("API Key is must set.");
        }

        if (context == null) {
            throw new Exception("Context is must set.");
        }

        IForecastGetter instance = new ForecastGetter(context, apiKey);
        return instance;
    }

    private Context mContext;

    private OnForecastGetListener mListener;

    private String mApiKey;

    private Locale mLocale;

    private LatLng mLatLng;

    private String mName;

    private int mId;

    private boolean mEnableWeatherIcon;

    private int mWeatherIconSize;

    private long mUpdateDistanceTime;

    private boolean mUseGeocodeForGetLocation;

    private int mForecastType;

    private int mForecastCount;

    private ForecastGetter(final Context context, final String apiKey) {
        mContext = context;
        mApiKey = apiKey;
        mLocale = Locale.getDefault();
        mId = -1;
        mEnableWeatherIcon = false;
        mUseGeocodeForGetLocation = false;
        mForecastType = FORECAST_TYPE_DAILY;
        mForecastCount = -1;
    }


    @Override
    public void setLocale(Locale locale) {
        mLocale = locale;
    }

    @Override
    public void setLatLng(LatLng latlng) {
        mLatLng = latlng;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public void setUseGeocodeForGetLocation(boolean useGeocode) {
        mUseGeocodeForGetLocation = useGeocode;
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
    public void setForecastType(int type) {
        mForecastType = type;
    }

    @Override
    public void setDailyCount(int count) {
        mForecastCount = count;
    }

    @Override
    public void getForecast(OnForecastGetListener listener) {
        mListener = listener;

        new ForecastGetTask().execute();
    }

    private class ForecastGetterImpl {
        /**
         * API for daily weather forecast
         */
        private static final String FORECAST_DAILY_API = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%1$f&lon=%2$f&APPID=%3$s";

        /**
         * API for weather forecast every 3hour
         */
        private static final String FORECAST_API = "http://api.openweathermap.org/data/2.5/forecast?lat=%1$f&lon=%2$f&APPID=%3$s";

        /**
         * API by id for daily weather forecast
         */
        private static final String FORECAST_DAILY_API_ID = "http://api.openweathermap.org/data/2.5/forecast/daily?id=%1$d&APPID=%2$s";

        /**
         * API by id for weather forecast every 3hour
         */
        private static final String FORECAST_API_ID = "http://api.openweathermap.org/data/2.5/forecast?id=%1$d&APPID=%2$s";

        /**
         * API by name for daily weather forecast
         */
        private static final String FORECAST_DAILY_API_NAME = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%1$s&APPID=%2$s";

        /**
         * API by name for weather forecast every 3hour
         */
        private static final String FORECAST_API_NAME = "http://api.openweathermap.org/data/2.5/forecast?q=%1$s&APPID=%2$s";

        private String mRequestURL;

        private boolean mNeedUpdate = false;

        private boolean mExistData = false;

        public List<WeatherInfo> getForecast() {

            // First, check local database data
            List<WeatherInfo> forecast = getForecastFromLocal();
            if (forecast == null) {
                // If don't exist on database, so get from service.
                forecast = getForecastFromService();
            }

            if (forecast != null) {
                saveLocal(forecast);
            }

            return forecast;
        }

        private List<WeatherInfo> getForecastFromLocal() {
            List<WeatherInfo> forecast = null;
            mNeedUpdate = false;

            long now = System.currentTimeMillis();
            ForecastDBHelper dbHelper = new ForecastDBHelper(mContext);
            ForecastDAO dao = new ForecastDAO(dbHelper, mUpdateDistanceTime);

            WeatherInfo locationInfo = null;
            if (mLatLng != null) {
                locationInfo = dao.findTargetLocation(mLatLng, mForecastType);
            }

            if ((locationInfo != null) && (mName != null)) {
                locationInfo = dao.findTargetLocation(mName, mForecastType);
            }

            if ((locationInfo != null) && (mId != -1)) {
                locationInfo = dao.findTargetLocation(mId, mForecastType);
            }

            if (locationInfo != null) {
                mExistData = true;
                if (dao.needUpdate(now, locationInfo)) {
                    mNeedUpdate = true;
                }
            }

            if (mExistData && !mNeedUpdate) {
                forecast = dao.getTargetForecast(locationInfo, mForecastType);
                Log.d(TAG, "Get weather forecast from local.");
            }

            dao.close();
            return forecast;
        }

        private List<WeatherInfo> getForecastFromService() {
            List<WeatherInfo> forecast = null;

            createRequestURL();

            String convertedCityName = null;

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

                // Parse Weather Forecast info form JSON format
                forecast = WeatherParser.parseForecast(data);
                for (WeatherInfo dailyForecast : forecast) {
                    // set forecast type
                    dailyForecast.setForecastType(mForecastType);

                    // Get weather icon from service.
                    if (mEnableWeatherIcon) {
                        Bitmap icon = WeatherIconGetter.getWeatherIcon(dailyForecast.getIconName(), mWeatherIconSize);
                        dailyForecast.setIcon(icon);
                    }

                    if (mLatLng != null) {
                        dailyForecast.setLatLng(mLatLng);
                    }

                    String cityName = mName;
                    if (cityName == null) {
                        cityName = convertedCityName;
                    }

                    if (cityName == null) {
                        // Get city name from coordinate for localize.
                        LatLng latlng = dailyForecast.getLatLng();
                        convertedCityName = GeocodeUtil.pointToName(mLocale, latlng);
                        cityName = convertedCityName;
                    }

                    if (cityName != null && cityName.length() != 0) {
                        dailyForecast.setName(cityName);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return forecast;
        }

        private void createRequestURL() {
            mRequestURL = null;

            if ((mName != null) && mUseGeocodeForGetLocation) {
                mLatLng = getLocationFromName(mName);
            }

            if (mLatLng != null) {
                String baseFormat = FORECAST_DAILY_API;
                if (mForecastType == FORECAST_TYPE_3HOUR) {
                    baseFormat = FORECAST_API;
                }
                mRequestURL = String.format(baseFormat, mLatLng.latitude, mLatLng.longitude, mApiKey);
            }

            if ((mRequestURL == null) && (mName != null)) {
                String baseFormat = FORECAST_DAILY_API_NAME;
                if (mForecastType == FORECAST_TYPE_3HOUR) {
                    baseFormat = FORECAST_API_NAME;
                }
                mRequestURL = String.format(baseFormat, mName, mApiKey);
            }

            if ((mRequestURL == null) && (mId != -1)) {
                String baseFormat = FORECAST_DAILY_API_ID;
                if (mForecastType == FORECAST_TYPE_3HOUR) {
                    baseFormat = FORECAST_API_ID;
                }
                mRequestURL = String.format(baseFormat, mId, mApiKey);
            }

            if (mForecastCount != -1) {
                mRequestURL += "&cnt=" + mForecastCount;
            }
        }

        private LatLng getLocationFromName(String name) {
            LatLng latLng = GeocodeUtil.nameToPoint(mLocale, name);
            return latLng;
        }

        private void saveLocal(List<WeatherInfo> forecast) {
            ForecastDBHelper dbHelper = new ForecastDBHelper(mContext);
            ForecastDAO dao = new ForecastDAO(dbHelper);

            if (mNeedUpdate) {
                dao.update(forecast);
            } else {
                if (!mExistData) {
                    dao.insert(forecast);
                }
            }
            dao.close();
        }
    }

    private class ForecastGetTask extends AsyncTask<Void, Void, List<WeatherInfo>> {

        @Override
        protected List<WeatherInfo> doInBackground(Void... params) {
            List<WeatherInfo> infoList = null;

            ForecastGetterImpl forecastGetterImpl = new ForecastGetterImpl();
            infoList = forecastGetterImpl.getForecast();

            return infoList;
        }

        @Override
        protected void onPostExecute(List<WeatherInfo> result) {
            if (mListener != null) {
                mListener.onGetForecast(result);
            }
        }
    }
}

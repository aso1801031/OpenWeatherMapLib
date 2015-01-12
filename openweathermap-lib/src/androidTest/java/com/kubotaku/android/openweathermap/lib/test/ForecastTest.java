package com.kubotaku.android.openweathermap.lib.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.kubotaku.android.openweathermap.lib.ForecastGetter;
import com.kubotaku.android.openweathermap.lib.IForecastGetter;
import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.OnForecastGetListener;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;
import com.kubotaku.android.openweathermap.lib.db.ForecastDAO;
import com.kubotaku.android.openweathermap.lib.db.ForecastDBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Weather forecast information getter test.
 */
public class ForecastTest extends InstrumentationTestCase {

    private static final String TAG = ForecastTest.class.getSimpleName();

    /**
     * Replace to your API Key
     */
    private static final String API_KEY = "01234567890123456789012345678901";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        initDatabase();
    }

    public void test001GetNewDataByGeoLocation() {
        Log.d(TAG, "+++ test001GetNewDataByGeoLocation() +++");

        IForecastGetter forecastGetter = getForecastGetter();

        Log.d(TAG, "------ get daily forecast -----");
        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_DAILY);

        Log.d(TAG, "------ get forecast every 3hour -----");
        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_3HOUR);
    }

    public void test002GetAlreadyExistData() {
        Log.d(TAG, "+++ test002GetAlreadyExistData() +++");

        IForecastGetter forecastGetter = getForecastGetter();

        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_DAILY);
        List<WeatherInfo> initialData = new ArrayList<WeatherInfo>(mForecastList);

        testGetSameData(forecastGetter, IForecastGetter.FORECAST_TYPE_DAILY);
        List<WeatherInfo> newData = new ArrayList<WeatherInfo>(mForecastList);

        assertEquals(initialData.size(), newData.size());

        int size = initialData.size();
        for (int index=0; index < size; index++) {
            WeatherInfo a = initialData.get(index);
            WeatherInfo b = newData.get(index);

            assertEquals(a.getWeatherId(), b.getWeatherId());
            assertEquals(a.getTime(), b.getTime());
        }

        initialData.clear();
        initialData = null;

        newData.clear();
        newData = null;
    }

    public void test002GetAlreadyExistData3Hour() {
        Log.d(TAG, "+++ test002GetAlreadyExistData3Hour() +++");

        IForecastGetter forecastGetter = getForecastGetter();

        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_3HOUR);
        List<WeatherInfo> initialData = new ArrayList<WeatherInfo>(mForecastList);

        testGetSameData(forecastGetter, IForecastGetter.FORECAST_TYPE_3HOUR);
        List<WeatherInfo> newData = new ArrayList<WeatherInfo>(mForecastList);

        assertEquals(initialData.size(), newData.size());

        int size = initialData.size();
        for (int index=0; index < size; index++) {
            WeatherInfo a = initialData.get(index);
            WeatherInfo b = newData.get(index);

            assertEquals(a.getWeatherId(), b.getWeatherId());
            assertEquals(a.getTime(), b.getTime());
        }
    }

    public void test002GetAlreadyExistDataMixed() {
        Log.d(TAG, "+++ test002GetAlreadyExistDataMixed() +++");

        IForecastGetter forecastGetter = getForecastGetter();

        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_DAILY);
        List<WeatherInfo> dailyData = new ArrayList<WeatherInfo>(mForecastList);

        testGetNewDataByGeoLocation(forecastGetter, IForecastGetter.FORECAST_TYPE_3HOUR);
        List<WeatherInfo> threeHourData = new ArrayList<WeatherInfo>(mForecastList);

        assertFalse(dailyData.size() == threeHourData.size());

        for (WeatherInfo daily : dailyData) {
            assertEquals(IForecastGetter.FORECAST_TYPE_DAILY, daily.getForecastType());
        }

        for (WeatherInfo three : threeHourData) {
            assertEquals(IForecastGetter.FORECAST_TYPE_3HOUR, three.getForecastType());
        }
    }

    // ---------------------------------------------------------------

    private void testGetNewDataByGeoLocation(final IForecastGetter forecastGetter, final int type) {
        forecastGetter.setLocale(Locale.JAPAN);

        LatLng latLng = new LatLng(36.4, 138.27);
        forecastGetter.setLatLng(latLng);

        forecastGetter.setEnableWeatherIcon(true);

        forecastGetter.setForecastType(type);

        mCountDownLatch = new CountDownLatch(1);

        forecastGetter.getForecast(mOnForecastGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mForecastList);

        if (type == IForecastGetter.FORECAST_TYPE_DAILY) {
            int size = mForecastList.size();
            int expectSize = 7;
            assertEquals(expectSize, size);
        }

        for (WeatherInfo forecast : mForecastList) {
            Log.d(TAG, forecast.toString());
        }
    }

    private void testGetNewDataByName(final IForecastGetter forecastGetter) {
        forecastGetter.setLocale(Locale.JAPAN);

        forecastGetter.setName("Ueda");

        forecastGetter.setEnableWeatherIcon(true);

        mCountDownLatch = new CountDownLatch(1);

        forecastGetter.getForecast(mOnForecastGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mForecastList);

        int size = mForecastList.size();
        assertEquals(7, size);

        for (WeatherInfo forecast : mForecastList) {
            Log.d(TAG, forecast.toString());
        }
    }

    private void testGetNewDataById(final IForecastGetter forecastGetter) {
        forecastGetter.setLocale(Locale.JAPAN);

        forecastGetter.setId(1849429);

        forecastGetter.setEnableWeatherIcon(true);

        mCountDownLatch = new CountDownLatch(1);

        forecastGetter.getForecast(mOnForecastGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mForecastList);

        int size = mForecastList.size();
        assertEquals(7, size);

        for (WeatherInfo forecast : mForecastList) {
            Log.d(TAG, forecast.toString());
        }
    }

    private void testGetSameData(final IForecastGetter forecastGetter, final int type) {
        mCountDownLatch = new CountDownLatch(1);

        forecastGetter.getForecast(mOnForecastGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mForecastList);

        if (type == IForecastGetter.FORECAST_TYPE_DAILY) {
            int size = mForecastList.size();
            assertEquals(7, size);
        }

        for (WeatherInfo forecast : mForecastList) {
            Log.d(TAG, forecast.toString());
        }
    }



    // ---------------------------------------------------------------

    private IForecastGetter getForecastGetter() {
        IForecastGetter forecastGetter = null;

        try {
            Context context = getInstrumentation().getContext();

            forecastGetter = ForecastGetter.getInstance(context, API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecastGetter;
    }

    private CountDownLatch mCountDownLatch;

    private List<WeatherInfo> mForecastList;

    private OnForecastGetListener mOnForecastGetListener = new OnForecastGetListener() {
        @Override
        public void onGetForecast(List<WeatherInfo> forecastList) {
            if (mForecastList != null) {
                mForecastList.clear();
                mForecastList = null;
            }
            mForecastList = forecastList;
            mCountDownLatch.countDown();
        }
    };

    private void initDatabase() {
        Context context = getInstrumentation().getTargetContext();
        ForecastDBHelper helper = new ForecastDBHelper(context);
        ForecastDAO dao = new ForecastDAO(helper);
        dao.deleteAll();
        dao.close();
    }
}

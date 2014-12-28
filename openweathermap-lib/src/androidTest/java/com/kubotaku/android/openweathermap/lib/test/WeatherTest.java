package com.kubotaku.android.openweathermap.lib.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.kubotaku.android.openweathermap.lib.IWeatherGetter;
import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.OnWeatherGetListener;
import com.kubotaku.android.openweathermap.lib.WeatherGetter;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;
import com.kubotaku.android.openweathermap.lib.db.WeatherDBHelper;
import com.kubotaku.android.openweathermap.lib.db.WeatherInfoDAO;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Weather information getter test.
 */
public class WeatherTest extends InstrumentationTestCase {

    private static final String TAG = WeatherTest.class.getSimpleName();

    /** Replace to your API Key */
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
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewData(weatherGetter);
    }

    public void test001GetNewDataByName() {
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewDataByName(weatherGetter);
    }

    public void test001GetNewDataByNameToPoint() {
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewDataByNameToPoint(weatherGetter);
    }

    public void test001GetNewDataById() {
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewDataById(weatherGetter);
    }

    public void test002GetAlreadyExistData() {
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewData(weatherGetter);
        WeatherInfo initialData = mWeatherInfoList.get(0);

        testGetSameData(weatherGetter);
        WeatherInfo secondData = mWeatherInfoList.get(0);

        assertEquals(initialData.getTime(), secondData.getTime());
    }

    public void test003GetAlreadyExistDataAfterWait() {
        IWeatherGetter weatherGetter = getWeatherGetter();

        testGetNewData(weatherGetter);
        WeatherInfo initialData = mWeatherInfoList.get(0);

        testGetSameData(weatherGetter);
        WeatherInfo secondData = mWeatherInfoList.get(0);

        assertEquals(initialData.getTime(), secondData.getTime());

        testGetSameDataAfterWait(weatherGetter);
        WeatherInfo thirdData = mWeatherInfoList.get(0);

        assertTrue(thirdData.getTime() > secondData.getTime());
    }


    // ----------------------------------------
    // ----------------------------------------

    private IWeatherGetter getWeatherGetter() {
        IWeatherGetter weatherGetter = null;

        try {
            Context context = getInstrumentation().getContext();

            weatherGetter = WeatherGetter.getInstance(context, API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(weatherGetter);

        return weatherGetter;
    }

    private void testGetNewData(final IWeatherGetter weatherGetter) {
        weatherGetter.setLocale(Locale.JAPAN);

        LatLng latLng = new LatLng(36.4, 138.27);
        weatherGetter.setLatLng(latLng);

        weatherGetter.setEnableWeatherIcon(true);

        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int infoCount = mWeatherInfoList.size();
        assertEquals(infoCount, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private void testGetNewDataByName(final IWeatherGetter weatherGetter) {
        weatherGetter.setLocale(Locale.JAPAN);

        weatherGetter.setName("Ueda");

        weatherGetter.setEnableWeatherIcon(true);

        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int infoCount = mWeatherInfoList.size();
        assertEquals(infoCount, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private void testGetNewDataByNameToPoint(final IWeatherGetter weatherGetter) {
        weatherGetter.setLocale(Locale.JAPAN);

        weatherGetter.setName("上田市");

        weatherGetter.setUseGeocodeForGetLocation(true);

        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int infoCount = mWeatherInfoList.size();
        assertEquals(infoCount, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private void testGetNewDataById(final IWeatherGetter weatherGetter) {
        weatherGetter.setLocale(Locale.JAPAN);

        weatherGetter.setId(1849429);

        weatherGetter.setEnableWeatherIcon(true);

        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int infoCount = mWeatherInfoList.size();
        assertEquals(infoCount, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private void testGetSameData(final IWeatherGetter weatherGetter) {
        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int count = mWeatherInfoList.size();
        assertEquals(count, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private void testGetSameDataAfterWait(final IWeatherGetter weatherGetter) {
        mCountDownLatch = new CountDownLatch(1);

        weatherGetter.setUpdateDistanceTime(31 * 1000);

        try {
            Thread.sleep(40 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        weatherGetter.getWeatherInfo(mOnWeatherGetListener);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mWeatherInfoList);

        int count = mWeatherInfoList.size();
        assertEquals(count, 1);

        WeatherInfo info = mWeatherInfoList.get(0);
        Log.d(TAG, info.toString());
    }

    private CountDownLatch mCountDownLatch;

    private ArrayList<WeatherInfo> mWeatherInfoList;

    private OnWeatherGetListener mOnWeatherGetListener = new OnWeatherGetListener() {
        @Override
        public void onGetWeatherInfo(ArrayList<WeatherInfo> weatherInfo) {
            if (mWeatherInfoList != null) {
                mWeatherInfoList.clear();
                mWeatherInfoList = null;
            }
            mWeatherInfoList = weatherInfo;
            mCountDownLatch.countDown();
        }
    };

    private void initDatabase() {
        Context context = getInstrumentation().getTargetContext();
        WeatherDBHelper helper = new WeatherDBHelper(context);
        WeatherInfoDAO dao = new WeatherInfoDAO(helper);
        dao.deleteAll();
        dao.close();
    }
}

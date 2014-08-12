package com.kubotaku.android.openweathermap.lib;

import android.graphics.Bitmap;

/**
 * Weather Info
 */
public class WeatherInfo {

    /** for Kelvin to convert to Celsius */
    private static final float BASE_TEMP = 273.15f;

    /** City identification */
    private int mId;

    /** City name */
    private String mName;

    /** Data receiving time, unix time, GMT */
    private long mTime;

    /** City geo location */
    private LatLng mLatLng;

    /** Temperature, Kelvin (subtract 273.15 to convert to Celsius) */
    private float mCurrentTemp;

    /** Minimum temperature at the moment.  */
    private float mMinTemp;

    /** Maximum temperature at the moment.  */
    private float mMaxTemp;

    /** Atmospheric pressure, hPa */
    private int mPressure;

    /** Humidity, % */
    private int mHumidity;

    /** Weather condition id */
    private int mWeatherId;

    /** Group of weather parameters (Rain, Snow, Extreme etc.) */
    private String mWeatherMain;

    /** Weather condition within the group */
    private String mWeatherDescription;

    /** Weather icon id */
    private String mIconName;

    /** Weather icon Bitmap */
    private Bitmap mIcon;

    /** Precipitation volume for last 3 hours, mm */
    private float mRain;

    /** Snow volume for last 3 hours, mm */
    private boolean mIsSnow;

    /** Wind speed, mps */
    private float mWindSpeed;

    /** Wind direction, degrees (meteorological) */
    private int mWindDeg;

    /** Cloudiness, % */
    private int mClouds;

    private String mForecastDate;

    private String mUrl;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }

    public float getCurrentTemp() {
        return mCurrentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.mCurrentTemp = currentTemp;
    }

    public float getMinTemp() {
        return mMinTemp;
    }

    public void setMinTemp(float minTemp) {
        this.mMinTemp = minTemp;
    }

    public float getMaxTemp() {
        return mMaxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.mMaxTemp = maxTemp;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        this.mHumidity = humidity;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        this.mIconName = iconName;
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(Bitmap icon) {
        this.mIcon = icon;
    }

    public float getRain() {
        return mRain;
    }

    public void setRain(float rain) {
        this.mRain = rain;
    }

    public boolean isSnow() {
        return mIsSnow;
    }

    public void setSnow(boolean isSnow) {
        this.mIsSnow = isSnow;
    }

    public String getForecastDate() {
        return mForecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.mForecastDate = forecastDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int weatherId) {
        this.mWeatherId = weatherId;
    }

    public int getPressure() {
        return mPressure;
    }

    public void setPressure(int pressure) {
        this.mPressure = pressure;
    }

    public float getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    public int getWindDeg() {
        return mWindDeg;
    }

    public void setWindDeg(int windDeg) {
        this.mWindDeg = windDeg;
    }

    public int getClouds() {
        return mClouds;
    }

    public void setClouds(int clouds) {
        this.mClouds = clouds;
    }

    public float getCurrentTempCelsius() {
        return (getCurrentTemp() - BASE_TEMP);
    }

    public float getMaxTempCelsius() {
        return (getMaxTemp() - BASE_TEMP);
    }

    public float getMinTempCelsius() {
        return (getMinTemp() - BASE_TEMP);
    }

    public String getWeatherMain() {
        return mWeatherMain;
    }

    public void setWeatherMain(String weatherMain) {
        this.mWeatherMain = weatherMain;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.mWeatherDescription = weatherDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Weather information :\n");
        sb.append("    time : " + mTime + "\n");
        sb.append("    city id : " + mId + "\n");
        sb.append("         name : " + mName + "\n");
        sb.append("         LatLng : " + mLatLng.toString() + "\n");
        sb.append("    temp(℃) : " + getCurrentTempCelsius() + "\n");
        sb.append("         max(℃) : " + getMaxTempCelsius() + "\n");
        sb.append("         min(℃) : " + getMinTempCelsius() + "\n");
        sb.append("    pressure(hPa) : " + mPressure + "\n");
        sb.append("    humidity(%) : " + mHumidity + "\n");
        sb.append("    Weather id : " + mWeatherId + "\n");
        sb.append("            main : " + mWeatherMain + "\n");
        sb.append("            desc : " + mWeatherDescription + "\n");
        sb.append("            icon : " + mIconName + "\n");
        sb.append("    Wind speed : " + mWindSpeed + "\n");
        sb.append("         deg : " + mWindDeg + "\n");
        sb.append("    Clouds(%) : " + mClouds + "\n");
        if (mIsSnow) {
            sb.append("    snow(mm) : " + mRain + "\n");
        } else {
            sb.append("    rain(mm) : " + mRain + "\n");
        }

        return sb.toString();
    }
}

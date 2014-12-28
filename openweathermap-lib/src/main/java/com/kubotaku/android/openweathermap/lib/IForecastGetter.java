package com.kubotaku.android.openweathermap.lib;

import java.util.Locale;

/**
 * Weather Forecast Access Interface.
 */
public interface IForecastGetter {

    /**
     * Forecast type : daily weather forecast up to 16 days(Default)
     */
    public static final int FORECAST_TYPE_DAILY = 0;

    /**
     * Forecast type : weather forecast for 5 days with data every 3 hours
     */
    public static final int FORECAST_TYPE_3HOUR = 1;

    /**
     * Set locale info.
     * if not set, use default locale.
     *
     * @param locale
     */
    void setLocale(final Locale locale);

    /**
     * Set Latitude and Longitude, you want to get weather information.
     *
     * @param latlng
     * @see com.kubotaku.android.openweathermap.lib.LatLng
     */
    void setLatLng(final LatLng latlng);

    /**
     * Set point name(ie. tokyo), you want to get weather information.
     *
     * @param name
     */
    void setName(final String name);

    /**
     * Set flag use geocode class for location address by name.<br/>
     * Name is set by {@link #setName(String)}
     *
     * @param useGeocode true : use geocode.
     */
    void setUseGeocodeForGetLocation(final boolean useGeocode);

    /**
     * Set point id, you want to get weather information.
     *
     * @param id
     */
    void setId(final int id);

    /**
     * Set whether get weather icon or not.
     *
     * @param enable
     */
    void setEnableWeatherIcon(final boolean enable);

    /**
     * Set weather icon size(px). Only enable if {@link #setWeatherIconSize(int)} is set true.
     *
     * @param size
     */
    void setWeatherIconSize(final int size);

    /**
     * Set time for check weather information form Web service.
     *
     * @param updateDistanceTime
     */
    void setUpdateDistanceTime(final long updateDistanceTime);

    /**
     * Set weather forecast type you can get.
     *
     * @param type
     * @see {@link #FORECAST_TYPE_3HOUR}
     * @see {@link #FORECAST_TYPE_DAILY}
     */
    void setForecastType(final int type);

    /**
     * Set limit of daily count(default is 7).
     * <p/>
     * Only enable if {@link #setForecastType(int)} is set {@link #FORECAST_TYPE_DAILY}.
     * <p/>
     *
     * @param count
     */
    void setDailyCount(final int count);

    /**
     * Get forecast of you will set point.
     *
     * @param listener
     */
    void getForecast(OnForecastGetListener listener);
}

package com.kubotaku.android.openweathermap.lib;

import java.util.List;
import java.util.Locale;

/**
 * Weather Info Access Interface
 */
public interface IWeatherGetter {

    /**
     * Set locale info.
     * if not set, use default locale.
     * @param locale
     */
    void setLocale(final Locale locale);

    /**
     * Set Latitude and Longitude, you want to get weather information.
     * @param latlng
     * @see com.kubotaku.android.openweathermap.lib.LatLng
     */
    void setLatLng(final LatLng latlng);

    /**
     * Set point name(ie. tokyo), you want to get weather information.
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
     * @param size
     */
    void setWeatherIconSize(final int size);

    /**
     * Set time for check weather information form Web service.
     * @param updateDistanceTime
     */
    void setUpdateDistanceTime(final long updateDistanceTime);

    /**
     * Set region, you want to get weather information.
     * You may use {@link #getWeatherInfo(int, com.kubotaku.android.openweathermap.lib.OnWeatherGetListener)}.
     * @param northeast
     * @param southwest
     */
    void setRegion(final LatLng northeast, final LatLng southwest);

    /**
     * Get current weather information, you will set point.
     * @return
     */
    void getWeatherInfo(OnWeatherGetListener listener);

    /**
     * Get current weather information, you will set point.
     * @param count get information count.
     * @return
     */
    void getWeatherInfo(final int count, OnWeatherGetListener listener);
}

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

import java.util.List;
import java.util.Locale;

/**
 * Weather Info Access Interface
 */
public interface IWeatherGetter {

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
     * Set region, you want to get weather information.
     * You may use {@link #getWeatherInfo(int, com.kubotaku.android.openweathermap.lib.OnWeatherGetListener)}.
     *
     * @param northeast
     * @param southwest
     */
    void setRegion(final LatLng northeast, final LatLng southwest);

    /**
     * Get current weather information, you will set point.
     *
     * @param listener listener.
     */
    void getWeatherInfo(OnWeatherGetListener listener);

    /**
     * Get current weather information, you will set point.
     *
     * @param count    get information count.
     * @param listener listener.
     */
    void getWeatherInfo(final int count, OnWeatherGetListener listener);
}

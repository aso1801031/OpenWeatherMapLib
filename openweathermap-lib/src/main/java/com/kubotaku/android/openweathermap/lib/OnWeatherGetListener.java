package com.kubotaku.android.openweathermap.lib;

import java.util.ArrayList;

/**
 * Weather info get listener.</br>
 *
 * @see {@link com.kubotaku.android.openweathermap.lib.IWeatherGetter}
 */
public interface OnWeatherGetListener {

    void onGetWeatherInfo(ArrayList<WeatherInfo> weatherInfo);

}

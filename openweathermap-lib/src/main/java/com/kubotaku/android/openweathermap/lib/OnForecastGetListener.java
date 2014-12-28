package com.kubotaku.android.openweathermap.lib;

import java.util.List;

/**
 * Forecast get listener.<br/>
 *
 * @see {@link com.kubotaku.android.openweathermap.lib.IForecastGetter}
 */
public interface OnForecastGetListener {

    void onGetForecast(List<WeatherInfo> forecastList);
}

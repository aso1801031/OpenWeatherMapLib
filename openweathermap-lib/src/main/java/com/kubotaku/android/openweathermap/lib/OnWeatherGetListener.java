package com.kubotaku.android.openweathermap.lib;

import java.util.ArrayList;

/**
 * Created by kubotaku1119 on 2014/08/11.
 */
public interface OnWeatherGetListener {

    void onGetWeatherInfo(ArrayList<WeatherInfo> weatherInfo);

}

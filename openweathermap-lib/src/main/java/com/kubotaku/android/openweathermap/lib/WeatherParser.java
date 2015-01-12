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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Weather info Parser.
 */
public class WeatherParser {

    /**
     * TAG for Log
     */
    private static final String TAG = WeatherParser.class.getSimpleName();

    private static final String KEY_CODE = "cod";
    private static final String KEY_LIST = "list";
    private static final String KEY_ID = "id";
    private static final String KEY_DT = "dt";
    private static final String KEY_NAME = "name";
    private static final String KEY_COORD = "coord";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_MAIN = "main";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_TEMP_MAX = "temp_max";
    private static final String KEY_MAX = "max";
    private static final String KEY_TEMP_MIN = "temp_min";
    private static final String KEY_MIN = "min";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_PRESSURE = "pressure";
    private static final String KEY_WIND = "wind";
    private static final String KEY_WIND_SPEED = "speed";
    private static final String KEY_WIND_DEG = "deg";
    private static final String KEY_CLOUDS = "clouds";
    private static final String KEY_CLOUDS_ALL = "all";
    private static final String KEY_WEATHER = "weather";
    private static final String KEY_WEATHER_ID = "id";
    private static final String KEY_WEATHER_MAIN = "main";
    private static final String KEY_WEATHER_DESC = "description";
    private static final String KEY_WEATHER_ICON = "icon";
    private static final String KEY_RAIN = "rain";
    private static final String KEY_RAIN_3H = "3h";
    private static final String KEY_SHOW = "snow";
    private static final String KEY_SNOW_3H = "3h";
    private static final String KEY_CITY = "city";

    public static List<WeatherInfo> parseWeather(String data) {
        List<WeatherInfo> weathers = null;
        try {
            JSONObject rootObj = new JSONObject(data);

            if (rootObj.has(KEY_CODE)) {
                String code = rootObj.getString(KEY_CODE);
                int codeNumber = Integer.parseInt(code);
                if (codeNumber != 200) {
                    return weathers;
                }
            }

            if (rootObj.has(KEY_LIST)) {
                JSONArray listArray = rootObj.getJSONArray(KEY_LIST);
                int num = listArray.length();
                if (num != 0) {
                    weathers = new ArrayList<WeatherInfo>();
                }

                for (int i = 0; i < num; i++) {
                    JSONObject obj = listArray.getJSONObject(i);

                    WeatherInfo info = new WeatherInfo();
                    parseWeather(info, obj, false);
                    weathers.add(info);
                }
            } else {
                WeatherInfo info = new WeatherInfo();
                parseWeather(info, rootObj, false);

                weathers = new ArrayList<WeatherInfo>();
                weathers.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weathers;
    }

    public static List<WeatherInfo> parseForecast(String data, final int type) {
        List<WeatherInfo> forecast = null;
        try {
            JSONObject rootObj = new JSONObject(data);

            // Get city information
            WeatherInfo locationInfo = new WeatherInfo();
            if (rootObj.has(KEY_CITY)) {
                JSONObject cityObj = rootObj.getJSONObject(KEY_CITY);
                parseCityInfo(locationInfo, cityObj);
            }

            // Get Weather forecast.
            if (rootObj.has(KEY_LIST)) {
                JSONArray listArray = rootObj.getJSONArray(KEY_LIST);
                int num = listArray.length();

                if (num != 0) {
                    forecast = new ArrayList<WeatherInfo>();
                }

                for (int i = 0; i < num; i++) {
                    JSONObject obj = listArray.getJSONObject(i);

                    WeatherInfo dailyForecast = new WeatherInfo();
                    dailyForecast.setName(locationInfo.getName());
                    dailyForecast.setLatLng(locationInfo.getLatLng());
                    dailyForecast.setId(locationInfo.getId());

                    if (type == IForecastGetter.FORECAST_TYPE_DAILY) {
                        parseForecast(dailyForecast, obj);
                    } else if (type == IForecastGetter.FORECAST_TYPE_3HOUR) {
                        parseWeather(dailyForecast, obj, true);
                    }
                    dailyForecast.setForecastType(type);

                    forecast.add(dailyForecast);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return forecast;
    }

    private static void parseWeather(WeatherInfo info, JSONObject obj, final boolean isForecast) {
        try {
            // Parse city information
            parseCityInfo(info, obj);

            // Weather main information
            if (obj.has(KEY_MAIN)) {
                JSONObject mainObj = obj.getJSONObject(KEY_MAIN);

                if (mainObj.has(KEY_TEMP)) {
                    float currentTemp = (float) (mainObj.getDouble(KEY_TEMP));
                    info.setCurrentTemp(currentTemp);
                }

                if (mainObj.has(KEY_TEMP_MIN)) {
                    float minTemp = (float) (mainObj.getDouble(KEY_TEMP_MIN));
                    info.setMinTemp(minTemp);
                }

                if (mainObj.has(KEY_TEMP_MAX)) {
                    float maxTemp = (float) (mainObj.getDouble(KEY_TEMP_MAX));
                    info.setMaxTemp(maxTemp);
                }

                if (mainObj.has(KEY_HUMIDITY)) {
                    int humidity = mainObj.getInt(KEY_HUMIDITY);
                    info.setHumidity(humidity);
                }

                if (mainObj.has(KEY_PRESSURE)) {
                    int pressure = mainObj.getInt(KEY_PRESSURE);
                    info.setPressure(pressure);
                }
            }

            // Data receiving time, unix time
            if (!isForecast) {
                long time = System.currentTimeMillis();
                info.setTime(time);
            } else {
                parseForecastDateTime(info, obj);
            }

            // Weather
            parseWeatherInfo(info, obj);

            // rain
            if (obj.has(KEY_RAIN)) {
                JSONObject rainObj = obj.getJSONObject(KEY_RAIN);
                if (rainObj.has(KEY_RAIN_3H)) {
                    float rain = (float) rainObj.getDouble(KEY_RAIN_3H);
                    info.setRain(rain);
                }
            }

            // snow
            if (obj.has(KEY_SHOW)) {
                JSONObject snowObj = obj.getJSONObject(KEY_SHOW);
                if (snowObj.has(KEY_SNOW_3H)) {
                    float snow = (float) snowObj.getDouble(KEY_SNOW_3H);
                    info.setRain(snow);
                    info.setSnow(true);
                }
            }

            // wind
            if (obj.has(KEY_WIND)) {
                JSONObject windObj = obj.getJSONObject(KEY_WIND);
                if (windObj.has(KEY_WIND_SPEED)) {
                    float speed = (float) windObj.getDouble(KEY_WIND_SPEED);
                    info.setWindSpeed(speed);
                }

                if (windObj.has(KEY_WIND_DEG)) {
                    int deg = windObj.getInt(KEY_WIND_DEG);
                    info.setWindDeg(deg);
                }
            }

            // clouds
            if (obj.has(KEY_CLOUDS)) {
                JSONObject cloudsObj = obj.getJSONObject(KEY_CLOUDS);
                if (cloudsObj.has(KEY_CLOUDS_ALL)) {
                    int clouds = cloudsObj.getInt(KEY_CLOUDS_ALL);
                    info.setClouds(clouds);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, info.toString());
    }

    private static void parseForecast(WeatherInfo forecast, JSONObject object) {
        try {
            if (object.has(KEY_TEMP)) {
                JSONObject tempObj = object.getJSONObject(KEY_TEMP);
                if (tempObj.has(KEY_MIN)) {
                    float minTemp = (float) (tempObj.getDouble(KEY_MIN));
                    forecast.setMinTemp(minTemp);
                }

                if (tempObj.has(KEY_MAX)) {
                    float maxTemp = (float) (tempObj.getDouble(KEY_MAX));
                    forecast.setMaxTemp(maxTemp);
                }
            }

            if (object.has(KEY_HUMIDITY)) {
                int humidity = object.getInt(KEY_HUMIDITY);
                forecast.setHumidity(humidity);
            }

            if (object.has(KEY_PRESSURE)) {
                int pressure = object.getInt(KEY_PRESSURE);
                forecast.setPressure(pressure);
            }

            // Data receiving time, unix time
            parseForecastDateTime(forecast, object);

            // Weather
            parseWeatherInfo(forecast, object);

            // wind
            if (object.has(KEY_WIND_SPEED)) {
                float speed = (float) object.getDouble(KEY_WIND_SPEED);
                forecast.setWindSpeed(speed);
            }

            if (object.has(KEY_WIND_DEG)) {
                int deg = object.getInt(KEY_WIND_DEG);
                forecast.setWindDeg(deg);
            }

            // clouds
            if (object.has(KEY_CLOUDS_ALL)) {
                int clouds = object.getInt(KEY_CLOUDS_ALL);
                forecast.setClouds(clouds);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseCityInfo(WeatherInfo weather, JSONObject object) {
        try {
            // City Id
            if (object.has(KEY_ID)) {
                int id = object.getInt(KEY_ID);
                weather.setId(id);
            }

            // City Name
            if (object.has(KEY_NAME)) {
                String cityName = object.getString(KEY_NAME);
                weather.setName(cityName);
            }

            // Geo location
            if (object.has(KEY_COORD)) {
                JSONObject coordObj = object.getJSONObject(KEY_COORD);
                if (coordObj.has(KEY_LAT) && coordObj.has(KEY_LON)) {
                    double lat = coordObj.getDouble("lat");
                    double lng = coordObj.getDouble("lon");
                    LatLng latlng = new LatLng(lat, lng);
                    weather.setLatLng(latlng);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseWeatherInfo(WeatherInfo weather, JSONObject object) {
        try {
            if (object.has(KEY_WEATHER)) {
                JSONArray weatherArray = object.getJSONArray(KEY_WEATHER);
                JSONObject weatherObj = weatherArray.getJSONObject(0);

                if (weatherObj.has(KEY_WEATHER_ID)) {
                    int id = weatherObj.getInt(KEY_WEATHER_ID);
                    weather.setWeatherId(id);
                }

                if (weatherObj.has(KEY_WEATHER_ICON)) {
                    String iconId = weatherObj.getString(KEY_WEATHER_ICON);
                    weather.setIconName(iconId);
                }

                if (weatherObj.has(KEY_WEATHER_MAIN)) {
                    String main = weatherObj.getString(KEY_WEATHER_MAIN);
                    weather.setWeatherMain(main);
                }

                if (weatherObj.has(KEY_WEATHER_DESC)) {
                    String description = weatherObj.getString(KEY_WEATHER_DESC);
                    weather.setWeatherDescription(description);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseForecastDateTime(WeatherInfo info, JSONObject object) {
        try {
            if (object.has(KEY_DT)) {
                // received data's unit is sec, so convert unit to millisecond.
                long time = object.getLong(KEY_DT) * 1000;
                info.setTime(time);

                TimeZone tz = TimeZone.getDefault();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm");
                sdf.setTimeZone(tz);
                Date date = new Date(time);
                String forecastDate = sdf.format(date);
                info.setForecastDate(forecastDate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

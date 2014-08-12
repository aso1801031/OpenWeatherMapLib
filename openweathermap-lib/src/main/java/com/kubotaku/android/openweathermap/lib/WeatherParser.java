package com.kubotaku.android.openweathermap.lib;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Weather info Parser.
 */
public class WeatherParser {

    /** TAG for Log */
    private static final String TAG = WeatherParser.class.getSimpleName();

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
    private static final String KEY_TEMP_MIN = "temp_min";
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

    public static WeatherInfo[] parseWeather(String data) {
        WeatherInfo[] weathers = null;
        try {
            JSONObject rootObj = new JSONObject(data);

            if (rootObj.has(KEY_LIST)) {
                JSONArray listArray = rootObj.getJSONArray(KEY_LIST);

                int num = listArray.length();
                weathers = new WeatherInfo[num];
                for (int i = 0; i < num; i++) {
                    JSONObject obj = listArray.getJSONObject(i);
                    weathers[i] = parseWeather(obj);
                }
            } else {
                weathers = new WeatherInfo[1];
                weathers[0] = parseWeather(rootObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weathers;
    }

    private static WeatherInfo parseWeather(JSONObject obj) {
        return parseWeather(obj, false);
    }

    private static WeatherInfo parseWeather(JSONObject obj, final boolean isForecast) {
        WeatherInfo weather = new WeatherInfo();

        try {
            if (!isForecast) {
                // City Id
                if (obj.has(KEY_ID)) {
                    int id = obj.getInt(KEY_ID);
                    weather.setId(id);
                }

                // City Name
                if (obj.has(KEY_NAME)) {
                    String cityName = obj.getString(KEY_NAME);
                    weather.setName(cityName);
                }

                // Geo location
                if (obj.has(KEY_COORD)) {
                    JSONObject coordObj = obj.getJSONObject(KEY_COORD);
                    if (coordObj.has(KEY_LAT) && coordObj.has(KEY_LON)) {
                        double lat = coordObj.getDouble("lat");
                        double lng = coordObj.getDouble("lon");
                        LatLng latlng = new LatLng(lat, lng);
                        weather.setLatLng(latlng);
                    }
                }
            }

            // Weather main information
            if (obj.has(KEY_MAIN)) {
                JSONObject mainObj = obj.getJSONObject(KEY_MAIN);

                if (mainObj.has(KEY_TEMP)) {
                    float currentTemp = (float) (mainObj.getDouble(KEY_TEMP));
                    weather.setCurrentTemp(currentTemp);
                }

                if (mainObj.has(KEY_TEMP_MIN)) {
                    float minTemp = (float) (mainObj.getDouble(KEY_TEMP_MIN));
                    weather.setMinTemp(minTemp);
                }

                if (mainObj.has(KEY_TEMP_MAX)) {
                    float maxTemp = (float) (mainObj.getDouble(KEY_TEMP_MAX));
                    weather.setMaxTemp(maxTemp);
                }

                if (mainObj.has(KEY_HUMIDITY)) {
                    int humidity = mainObj.getInt(KEY_HUMIDITY);
                    weather.setHumidity(humidity);
                }

                if (mainObj.has(KEY_PRESSURE)) {
                    int pressure = mainObj.getInt(KEY_PRESSURE);
                    weather.setPressure(pressure);
                }
            }

            // Data receiving time, unix time
            long time = System.currentTimeMillis();
            weather.setTime(time);

            // Weather
            if (obj.has(KEY_WEATHER)) {
                JSONArray weatherArray = obj.getJSONArray(KEY_WEATHER);
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

            // rain
            if (obj.has(KEY_RAIN)) {
                JSONObject rainObj = obj.getJSONObject(KEY_RAIN);
                if (rainObj.has(KEY_RAIN_3H)) {
                    float rain = (float) rainObj.getDouble(KEY_RAIN_3H);
                    weather.setRain(rain);
                }
            }

            // snow
            if (obj.has(KEY_SHOW)) {
                JSONObject snowObj = obj.getJSONObject(KEY_SHOW);
                if (snowObj.has(KEY_SNOW_3H)) {
                    float snow = (float) snowObj.getDouble(KEY_SNOW_3H);
                    weather.setRain(snow);
                    weather.setSnow(true);
                }
            }

            // wind
            if (obj.has(KEY_WIND)) {
                JSONObject windObj = obj.getJSONObject(KEY_WIND);
                if (windObj.has(KEY_WIND_SPEED)) {
                    float speed = (float) windObj.getDouble(KEY_WIND_SPEED);
                    weather.setWindSpeed(speed);
                }

                if (windObj.has(KEY_WIND_DEG)) {
                    int deg = windObj.getInt(KEY_WIND_DEG);
                    weather.setWindDeg(deg);
                }
            }

            // clouds
            if (obj.has(KEY_CLOUDS)) {
                JSONObject cloudsObj = obj.getJSONObject(KEY_CLOUDS);
                if (cloudsObj.has(KEY_CLOUDS_ALL)) {
                    int clouds = cloudsObj.getInt(KEY_CLOUDS_ALL);
                    weather.setClouds(clouds);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

		Log.d(TAG, weather.toString());

        return weather;
    }

}

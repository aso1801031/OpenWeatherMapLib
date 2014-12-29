OpenWeatherMapLib
=================

OpenWeatherMapのAPIを使った天気取得を手軽に行うための
Android用のLibrary Projectです。

使用にはOpenWeatherMap.orgでユーザー登録して取得したAPIキーが必要です。
http://openweathermap.org


できること
---------------
* 指定した地点の現在天気情報の取得(都市名、ID、緯度経度)
* 指定した地点の天気予報の取得(都市名、ID、経度緯度）
* 取得したデータのローカルDBへのキャッシュ(一定時間内に同一地点のリクエストを行った場合にローカルDBの情報を使用)


使用方法
------------

* 天気情報取得用のインターフェース取得

        IWeatherGetter weatherGetter = null;

        try {
            // API_KEYにはOpenWeatherMapで取得したキーをセット
            weatherGetter = WeatherGetter.getInstance(context, API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }


* 取得する情報を設定して、取得

        // Location指定(指定したロケールに応じて都市名を取得します)
        weatherGetter.setLocale(Locale.JAPAN);

        // 天気を取得したい地点の緯度・経度を指定
        LatLng latLng = new LatLng(36.4, 138.27);
        weatherGetter.setLatLng(latLng);

        // 天気アイコンの取得設定(任意、trueを指定した場合Bitmapオブジェクトが取得できます)
        weatherGetter.setEnableWeatherIcon(true);

        // 情報取得時に呼び出されるリスナーを指定して取得APIを呼び出し
        weatherGetter.getWeatherInfo(new OnWeatherGetListener() {
            @Override
            public void onGetWeatherInfo(ArrayList<WeatherInfo> weatherInfo) {
                
            }
        });

* 天気予報取得用のインターフェース取得
 
        IForecastGetter forecastGetter = null;

        try {
            // API_KEYにはOpenWeatherMapで取得したキーをセット
            forecastGetter = ForecastGetter.getInstance(context, API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }

* 取得する情報を設定して、取得

        // Location指定(指定したロケールに応じて都市名を取得します)
        forecastGetter.setLocale(Locale.JAPAN);

        // 天気を取得したい地点の緯度・経度を指定
        LatLng latLng = new LatLng(36.4, 138.27);
        forecastGetter.setLatLng(latLng);

        // 天気アイコンの取得設定(任意、trueを指定した場合Bitmapオブジェクトが取得できます)
        forecastGetter.setEnableWeatherIcon(true);

        // 情報取得時に呼び出されるリスナーを指定して取得APIを呼び出し
        forecastGetter.getForecast(new OnForecastGetListener() {
            @Override
            public void onGetForecast(List<WeatherInfo> infoList) {
                    
            }
        });

Android Studioからの利用
-----------------------------------

        repositories {
                maven { url 'http://raw.github.com/kubotaku1119/OpenWeatherMapLib/master/repository/' }
        }

        dependencies {
                compile 'com.kubotaku:openweathermap-lib:0.1.0'
        }


ライセンス
-----------------------------------
        The MIT License (MIT)

        Copyright (c) 2014 kubotaku1119

        Permission is hereby granted, free of charge, to any person obtaining a copy of
        this software and associated documentation files (the "Software"), to deal in
        the Software without restriction, including without limitation the rights to
        use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
        the Software, and to permit persons to whom the Software is furnished to do so,
        subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
        FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
        COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
        IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
        CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

OpenWeatherMapLib
=================

OpenWeatherMapのAPIを使った天気取得を手軽に行うための
Android用のLibrary Projectです。

使用にはOpenWeatherMap.orgでユーザー登録して取得したAPIキーが必要です。
http://openweathermap.org


できること
---------------
* 指定した地点の現在天気情報の取得(都市名、ID、緯度経度)
* 取得したデータのローカルDBへのキャッシュ(一定時間内に同一地点のリクエストを行った場合にローカルDBの情報を使用)

使用方法
------------

* 天気情報取得用インスタンスの取得

        IWeatherGetter weatherGetter = null;

        try {
            // API_KEYにはOpenWeatherMapで取得したキーをセット
            weatherGetter = WeatherGetter.getInstance(context, API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }


* 取得する情報を設定

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


Android Studioからの利用
-----------------------------------

準備中

更新予定
------------

以下の機能を順次追加予定です。

* 天気予報取得
* 指定範囲の天気情報取得
* 複数都市の情報を同時取得

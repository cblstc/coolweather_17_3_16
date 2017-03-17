package com.coolweather.android.gson;

/**
 * 环境质量
 * Created by Administrator on 2017/3/17.
 */

public class AQI {

    public AQICity city;

    public class AQICity {

        public String aqi;

        public String pm25;
    }
}

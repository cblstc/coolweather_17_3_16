package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 城市的基本内容
 * Created by Administrator on 2017/3/17.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;  // 城市名

    @SerializedName("id")
    public String weatherId;  // 天气编号

    public Update update;

    public class Update {

        @SerializedName("loc")  // 位置信息
        public String updateTime;
    }
}

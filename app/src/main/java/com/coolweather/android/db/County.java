package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 城市下的区县
 * Created by Administrator on 2017/3/16.
 */

public class County extends DataSupport {

    private int id;

    private String countyName;  // 区县名称

    private String weatherId;  // 天气id

    private int cityId;  // 引用城市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

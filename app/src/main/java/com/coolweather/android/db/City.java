package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 省份下的城市
 * Created by Administrator on 2017/3/16.
 */

public class City extends DataSupport {

    private int id;

    private String cityName;  // 城市名

    private int cityCode;  // 城市代码

    private int provinceId;  // 引用省份的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

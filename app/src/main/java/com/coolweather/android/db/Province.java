package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 省份类
 * Created by Administrator on 2017/3/16.
 */

public class Province extends DataSupport {

    private int id;

    private String provinceName;  // 省份名

    private int provinceCode;  // 省份代码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

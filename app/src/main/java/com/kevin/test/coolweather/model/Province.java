package com.kevin.test.coolweather.model;

/**
 * Created by Kevin-He on 2016/7/7.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public Province(){

    }

    public Province(String provinceCode, int id, String provinceName) {
        this.provinceCode = provinceCode;
        this.id = id;
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String porvinceName) {
        this.provinceName = porvinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}

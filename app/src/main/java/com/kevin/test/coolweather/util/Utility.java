package com.kevin.test.coolweather.util;

import android.text.TextUtils;

import com.kevin.test.coolweather.db.CoolWeatherDB;
import com.kevin.test.coolweather.model.City;
import com.kevin.test.coolweather.model.County;
import com.kevin.test.coolweather.model.Province;

/**
 * Created by Kevin-He on 2016/7/14.
 */
public class Utility {
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces =  response.split(",");
            if (allProvinces !=null && allProvinces.length > 0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }

        }
        return false;
    }
    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,int privinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities =  response.split(",");
            if (allCities !=null && allCities.length > 0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(privinceId);
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }

        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
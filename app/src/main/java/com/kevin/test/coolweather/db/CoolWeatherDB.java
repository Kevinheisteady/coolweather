package com.kevin.test.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kevin.test.coolweather.model.City;
import com.kevin.test.coolweather.model.County;
import com.kevin.test.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin-He on 2016/7/7.
 */
public class CoolWeatherDB {
    //数据库名
    public static final String DB_NAME = "cool_weather";
    //数据库版本
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;
    //单例模式
    private CoolWeatherDB(Context context){
        db = new CoolWeatherOpenHelper(context,DB_NAME,null,1).getWritableDatabase();
    }
    //获取CoolWeatherDB的实例
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 将省份信息保存到数据库
     * @param province Province实例
     */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name",province.getProvinceName());
            contentValues.put("province_code",province.getProvinceCode());
            db.insert("Province",null,contentValues);
        }
    }

    /**
     * 将省份信息从数据库取出
     * @return 省份信息列表
     */
    public List<Province> loadProvince(){
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.rawQuery("select * from Province",null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        return provinceList;
    }

    /**
     * 将城市信息存入数据库
     * @param city
     */
    public void saveCity(City city){
         if(city != null){
             ContentValues contentValues = new ContentValues();
             contentValues.put("city_name",city.getCityName());
             contentValues.put("city_code",city.getCityCode());
             contentValues.put("province_id",city.getProvinceId());
             db.insert("City",null,contentValues);
         }
    }

    /**
     * 将城市信息从数据库取出
     * @return
     */
    public List<City> loadCities(int provinceId){
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[] { String.valueOf(provinceId) }, null, null, null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        return cityList;
    }

    /**
     * 将县信息保存到数据库
     * @param county
     */
    public void saveCounty(County county){
        if(county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_id",county.getCityId());
            contentValues.put("county_code",county.getCountyCode());
            contentValues.put("county_name",county.getCountyName());
            db.insert("County",null,contentValues);
        }
    }

    /**
     * 将县的信息从数据库取出
     * @return
     */
    public List<County> loadCounties(int cityId){
        List<County> countyList = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);
        if(cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        return countyList;
    }
}

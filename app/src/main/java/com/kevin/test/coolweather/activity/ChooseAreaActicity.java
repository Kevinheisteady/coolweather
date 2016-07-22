package com.kevin.test.coolweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.test.coolweather.R;
import com.kevin.test.coolweather.db.CoolWeatherDB;
import com.kevin.test.coolweather.model.City;
import com.kevin.test.coolweather.model.County;
import com.kevin.test.coolweather.model.Province;
import com.kevin.test.coolweather.util.HttpCallBackListener;
import com.kevin.test.coolweather.util.HttpUtil;
import com.kevin.test.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActicity extends AppCompatActivity {

    public static final int LEVEL_PEOVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvice;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if(currentLevel == LEVEL_PEOVINCE){
                    selectedProvice = provinceList.get(index);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(index).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActicity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }

    private void queryProvinces(){
        provinceList =  coolWeatherDB.loadProvince();
        /*
        当省份信息变更时怎么做。
         */
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel =LEVEL_PEOVINCE;
        }else{
            queryFromServer(null,"province");
        }

    }

    private void queryCities(){
        cityList =  coolWeatherDB.loadCities(selectedProvice.getId()    );
        /*
        当省份信息变更时怎么做。
         */
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvice.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvice.getProvinceCode(),"city");
        }
    }

    private void queryCounties(){
        countyList =  coolWeatherDB.loadCounties(selectedCity.getId());
        /*
        当省份信息变更时怎么做。
         */
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code,final  String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(coolWeatherDB,response,selectedProvice.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                                queryProvinces();
                            else if("city".equals(type))
                                queryCities();
                            else if("county".equals(type))
                                queryCounties();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActicity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog != null)
            progressDialog.dismiss();
    }
}

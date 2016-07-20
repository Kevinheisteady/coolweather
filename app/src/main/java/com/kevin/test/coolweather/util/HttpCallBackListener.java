package com.kevin.test.coolweather.util;

/**
 * Created by Kevin-He on 2016/7/11.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}

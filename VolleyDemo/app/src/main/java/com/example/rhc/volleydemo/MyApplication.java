package com.example.rhc.volleydemo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by rhc on 2018/8/10.
 */

public class MyApplication extends Application {
    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Fresco初始化
        Fresco.initialize(getApplicationContext());
    }
}
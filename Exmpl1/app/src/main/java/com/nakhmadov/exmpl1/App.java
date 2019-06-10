package com.nakhmadov.exmpl1;

import android.app.Application;

import com.vc.TrueConfSDK;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TrueConfSDK.getInstance().start(this, "ru10.trueconf.net", true);
    }
}

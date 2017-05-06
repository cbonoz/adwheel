package com.adwheel.www.wheel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.adwheel.www.wheel.injection.DaggerInjectionComponent;
import com.adwheel.www.wheel.injection.InjectionComponent;
import com.adwheel.www.wheel.injection.WheelModule;

public class WheelApplication extends Application {

    public static final String PREF_NAME = "prefs";

    private static WheelApplication app;
    private InjectionComponent mInjectionComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "onCreate");
        mInjectionComponent = DaggerInjectionComponent.builder()
                .wheelModule(new WheelModule(this))
                .build();

        app = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static InjectionComponent getInjectionComponent() {
        return app.mInjectionComponent;
    }
}

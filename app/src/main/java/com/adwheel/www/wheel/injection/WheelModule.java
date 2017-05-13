package com.adwheel.www.wheel.injection;


import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.managers.AdManager;
import com.adwheel.www.wheel.managers.DialogManager;
import com.adwheel.www.wheel.managers.PrefManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WheelModule {

    private final WheelApplication mApplication;

    public WheelModule(WheelApplication application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    WheelApplication providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    PrefManager providesPrefManager(WheelApplication app, Gson gson) {
        return new PrefManager(app, gson);
    }

    @Provides
    @Singleton
    AdManager providesAdTopicManager(PrefManager prefManager, Gson gson) {
        return new AdManager(prefManager, gson);
    }

    @Provides
    @Singleton
    DialogManager provideDialogManager(PrefManager prefManager, AdManager adManager) {
        return new DialogManager(prefManager, adManager);
    }

}

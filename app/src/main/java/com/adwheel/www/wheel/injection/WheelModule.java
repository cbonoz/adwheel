package com.adwheel.www.wheel.injection;


import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.managers.AdTopicManager;
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
    PrefManager providesPrefManager(WheelApplication app) {
        return new PrefManager(app);
    }

    @Provides
    @Singleton
    AdTopicManager providesAdTopicManager(PrefManager prefManager) {
        return new AdTopicManager(prefManager);
    }
    @Provides
    @Singleton
    DialogManager provideDialogManager(WheelApplication application) {
        return new DialogManager(application);
    }

}

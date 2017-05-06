package com.adwheel.www.wheel.injection;

import com.adwheel.www.wheel.activities.MainActivity;
import com.adwheel.www.wheel.activities.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = com.adwheel.www.wheel.injection.WheelModule.class)
public interface InjectionComponent {

    // Activities
    void inject(MainActivity activity);
    void inject(SplashActivity activity);

    // Fragments

    // Services

    // Other
}

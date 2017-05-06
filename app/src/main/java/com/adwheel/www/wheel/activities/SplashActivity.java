package com.adwheel.www.wheel.activities;

import android.content.Intent;
import android.util.Log;

import com.adwheel.www.wheel.BuildConfig;
import com.adwheel.www.wheel.R;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AwesomeSplash  {
    private static final String TAG = "SplashActivity";

    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!
    @Override
    public void initSplash(ConfigSplash configSplash) {
        /* you don't have to override every property */

        final int duration;
        if (BuildConfig.DEBUG) {
            duration = 500;
        } else {
            // Production duration for into splash screen animations.
            duration = 1000;
        }
        Log.d(TAG, "Splash duration: " + duration);

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.primary_dark); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(duration); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_LEFT);  //or Flags.REVEAL_RIGHT
        configSplash.setRevealFlagY(Flags.REVEAL_TOP); //or Flags.REVEAL_BOTTOM

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.wheel2); //or any other drawable
        configSplash.setAnimLogoSplashDuration(duration); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.setTitleSplash(getString(R.string.app_name));
        configSplash.setTitleTextColor(R.color.md_amber_700);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(duration);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        // configSplash.setTitleFont("fonts/myfont.ttf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {
        // Transit to another activity here or perform other actions.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

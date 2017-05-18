package com.adwheel.www.wheel.activities.intro;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.activities.MainActivity;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    private void prepareIntroSlides() {
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.title_1), getString(R.string.desc_1),
                R.drawable.ad_wheel_250, getResources().getColor(R.color.md_purple_500)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.title_2), getString(R.string.desc_2),
                R.drawable.search_screen, getResources().getColor(R.color.md_indigo_500)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.title_3), getString(R.string.desc_3),
                R.drawable.settings_screen, getResources().getColor(R.color.md_lime_500)));
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.title_4), getString(R.string.desc_4),
                R.drawable.spin_screen, getResources().getColor(R.color.md_grey_500)));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        setTitle(R.string.app_tutorial_name);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        // addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));
        prepareIntroSlides();

        // OPTIONAL METHODS
        // Override bar/separator color.
        // setBarColor(Color.parseColor("#3F51B5"));
        // setSeparatorColor(Color.parseColor("#2196F3"));
        setBarColor(getResources().getColor(R.color.md_deep_purple_500));
        setSeparatorColor(getResources().getColor(R.color.md_blue_grey_900));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        // setVibrate(true);
        // setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        startMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startMainActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

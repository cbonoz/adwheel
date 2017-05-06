package com.adwheel.www.wheel;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.adwheel.www.wheel.activities.MainActivity;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

/**
 * Created on 4/16/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScreenShotTest {

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

//    @Test
//    public void testTakeScreenshot() throws Exception {
//        Thread.sleep(1000);
//        Screengrab.screenshot("chapter_page");
//        onView(withId(R.id.viewpager)).perform(swipeLeft());
//        Screengrab.screenshot("quizzes_page");
//        onView(withId(R.id.viewpager)).perform(swipeLeft());
////        onView(withId(R.id.fab)).perform(click());
////        Thread.sleep(1000);
////        Screengrab.screenshot("about_page");
////        onView(withId(R.id.backButton)).perform(click());
//        onView(withId(R.id.viewpager)).perform(swipeLeft());
//        Screengrab.screenshot("support_page");
//        onView(withId(R.id.viewpager)).perform(swipeRight());
//        onView(withId(R.id.statsButton)).perform(click());
//        Screengrab.screenshot("stats");
//    }
}

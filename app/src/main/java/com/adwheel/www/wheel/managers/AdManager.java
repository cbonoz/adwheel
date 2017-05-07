package com.adwheel.www.wheel.managers;

import android.util.Log;

import com.google.android.gms.ads.AdRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();

    private final PrefManager prefManager;

    public static final String GENDER_LOC = "gender";
    public static final String BIRTH_YEAR_LOC = "birthyear";
    public static final String TOPIC_LOC = "topics";
    public static final String FAM_LOC = "family";

    public static final int DEFAULT_BIRTH_YEAR = 2000;

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String NEUTRAL = "Neutral";


    private static final List<String> EXAMPLE_TOPICS = Arrays.asList(
            "technology",
            "games",
            "clothing",
            "television",
            "sports",
            "hygiene",
            "food",
            "science",
            "apps",
            "politics"
    );

    public AdManager(PrefManager prefManager) {
        this.prefManager = prefManager;
    }

    public List<String> getExampleTopics() {
        return EXAMPLE_TOPICS;
    }

    public AdRequest.Builder createAdBuilderFromPrefs() {
        AdRequest.Builder builder = new AdRequest.Builder();

        try {
            Date birthday = new Date();
            // TODO: just using birth year as the dominant factor.
            birthday.setYear((int) prefManager.getLong(BIRTH_YEAR_LOC, 2000));
            builder = builder.setBirthday(birthday);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        boolean isMale = prefManager.getBoolean(GENDER_LOC, true);
        // use male as the default.
        builder = builder.setGender(isMale ? 1 : 2);

        builder = builder.setIsDesignedForFamilies(prefManager.getBoolean(FAM_LOC, false));

        return builder;
    }
}

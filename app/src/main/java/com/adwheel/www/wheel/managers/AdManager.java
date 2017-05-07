package com.adwheel.www.wheel.managers;

import android.util.Log;

import com.adwheel.www.wheel.models.HistoryItem;
import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();

    private final PrefManager prefManager;
    private final Gson gson;

    public static final String GENDER_LOC = "gender";
    public static final String BIRTH_YEAR_LOC = "birthyear";
    public static final String TOPIC_LOC = "topics";
    public static final String FAMILY_LOC = "family";

    public static final String HISTORY_LOC = "history";
    public static final String HISTORY_COUNT_LOC = "count";

    public static final int DEFAULT_BIRTH_YEAR = 2000;

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String NEUTRAL = "Neutral";

    private List<HistoryItem> historyItems;


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

    public AdManager(PrefManager prefManager, Gson gson) {
        this.prefManager = prefManager;
        this.gson = gson;
        this.historyItems = new ArrayList<>();
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

        builder = builder.setIsDesignedForFamilies(prefManager.getBoolean(FAMILY_LOC, false));

        return builder;
    }

    public List<HistoryItem> loadTopics() {
        final long count = prefManager.getLong(HISTORY_COUNT_LOC, 0);
        String saveLoc;
        for (int i = historyItems.size(); i < count; i++) {
            saveLoc = HISTORY_LOC + i;
            String itemString = prefManager.getString(saveLoc, null);
            try {
                historyItems.add(gson.fromJson(itemString, HistoryItem.class));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        return historyItems;
    }

    private void incrementHistoryCount(long count) {
        prefManager.saveLong(HISTORY_COUNT_LOC, count+1);
    }

    public void saveTopicString(String topicString) {
        final long timestamp = System.currentTimeMillis();
        final long count = prefManager.getLong(HISTORY_COUNT_LOC, 0);
        final String saveLoc = HISTORY_LOC + count;

        final HistoryItem item = new HistoryItem(timestamp, topicString);
        prefManager.saveString(saveLoc, gson.toJson(item));
        incrementHistoryCount(count);
    }
}

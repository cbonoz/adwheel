package com.adwheel.www.wheel.managers;

import android.util.Log;

import com.adwheel.www.wheel.models.HistoryItem;
import com.adwheel.www.wheel.models.TopicsHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();

    private final PrefManager prefManager;
    private final Gson gson;

    public static final String GENDER_LOC = "gender";
    public static final String BIRTH_YEAR_LOC = "birthyear";
    public static final String SEARCH_TOPIC_LOC = "search_topics";
    public static final String WHEEL_TOPIC_LOC = "search_topics";
    public static final String FAMILY_LOC = "family";

    public static final String HISTORY_LOC = "history";
    public static final String HISTORY_COUNT_LOC = "count";

    public static final int DEFAULT_BIRTH_YEAR = 2000;
//
//    public static final String MALE = "Male";
//    public static final String FEMALE = "Female";
//    public static final String NEUTRAL = "Neutral";

    private List<HistoryItem> historyItems;

    private static final List<String> EXAMPLE_TOPICS = Arrays.asList(
            "technology",
            "games",
            "clothing",
            "sports",
            "food",
            "politics"
    );

    public AdManager(PrefManager prefManager, Gson gson) {
        this.prefManager = prefManager;
        this.gson = gson;
        this.historyItems = new ArrayList<>();
    }

    private TopicsHolder getDefaultWheelTopics() {
        return new TopicsHolder(EXAMPLE_TOPICS);
    }

    public AdRequest.Builder createAdBuilderFromPrefs() {
        AdRequest.Builder builder = new AdRequest.Builder()
                .addTestDevice("24FC531E1163DBB1DF67674F1882463C");

        try {
            Date birthday = new Date();
            // TODO: just using birth year as the dominant factor.
            birthday.setYear(prefManager.getInt(BIRTH_YEAR_LOC, DEFAULT_BIRTH_YEAR));
            builder = builder.setBirthday(birthday);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        final int gender = prefManager.getInt(GENDER_LOC, -1);
        if (gender != -1) {
            // use male as the default.
            Log.d(TAG, "gender ordinal: " + gender);
            builder = builder.setGender(gender);
        }

        builder = builder.setIsDesignedForFamilies(prefManager.getBoolean(FAMILY_LOC, false));

        return builder;
    }

    public List<HistoryItem> getTopicHistory() {
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

        Log.d(TAG, "History Items and Count should be equal: " + historyItems + ", " + count);

        return historyItems;
    }

    private void incrementHistoryCount(long count) {
        prefManager.saveLong(HISTORY_COUNT_LOC, count + 1);
    }

    public void saveTopicStringToHistory(String topicString) {
        final long timestamp = System.currentTimeMillis();
        final long count = prefManager.getLong(HISTORY_COUNT_LOC, 0);
        final String saveLoc = HISTORY_LOC + count;

        final HistoryItem item = new HistoryItem(timestamp, topicString);
        prefManager.saveString(saveLoc, gson.toJson(item));
        incrementHistoryCount(count);
    }

    public void clearHistory() {
        final long timestamp = System.currentTimeMillis();
        prefManager.saveLong(HISTORY_COUNT_LOC, 0);
        historyItems.clear();
    }

    private static final String[] loadingStrings = new String[]{
            "Preparing Content",
            "Your Wish is my Command",
            "Checking ads on other planets",
            "Browsing the internet",
            "Getting popcorn"
    };

    public String getRandomLoadingText() {
        Random rand = new Random();
        final int index = rand.nextInt(loadingStrings.length);
        return loadingStrings[index] + "...";
    }

    // WHEEL Topic methods.

    public void saveTopicsHolder(String location, TopicsHolder topicsHolder) {
        prefManager.saveJsonPreference(location, topicsHolder);
    }

    public TopicsHolder getTopicsHolder(String location) {
        TopicsHolder defaultHolder = location.equals(WHEEL_TOPIC_LOC) ? getDefaultWheelTopics() : new TopicsHolder(Arrays.asList("politics"));
        final TopicsHolder topicsHolder = prefManager.getJsonPreference(location, TopicsHolder.class, defaultHolder);

        // Validation of returned topics.
        int numTopics = topicsHolder.topics.size();
        if (numTopics < 2 || numTopics > DialogManager.MAX_OPTIONS) {
            return getDefaultWheelTopics();
        } else {
            return topicsHolder;
        }
    }
}

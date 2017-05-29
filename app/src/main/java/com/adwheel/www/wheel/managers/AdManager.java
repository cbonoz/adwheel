package com.adwheel.www.wheel.managers;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.models.TopicsHolder;
import com.adwheel.www.wheel.services.WebService;

import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();

    private final WheelApplication app;
    private final PrefManager prefManager;
    private final Gson gson;

    public static final String GENDER_LOC = "gender";
    public static final String BIRTH_YEAR_LOC = "birthyear";
    public static final String SEARCH_TOPIC_LOC = "search_topics";
    public static final String WHEEL_TOPIC_LOC = "wheel_topics";
    public static final String FAMILY_LOC = "family";

    public static final String HISTORY_LOC = "history";
    public static final String HISTORY_COUNT_LOC = "count";

    public static final int DEFAULT_BIRTH_YEAR = 2000;

    private List<TopicsHolder> historyItems;

    public static final String[] EXAMPLE_TOPICS = new String[]{
            "apparel",
            "arts",
            "beauty",
            "business",
            "computers",
            "dining",
            "education",
            "electronics",
            "entertainment",
            "family",
            "finance",
            "fitness",
            "food",
            "garden",
            "gifts",
            "government",
            "health",
            "home",
            "internet",
            "jobs",
            "law",
            "leisure",
            "media",
            "news",
            "publications",
            "real estate",
            "sports",
            "travel",
            "vehicles",
            "dating", // begin sensitive topics
            "downloads",
            "drugs",
            "esoteric",
            "gambling",
            "games",
            "politics",
            "religion",
            "sex",
            "surgery",
            "weight loss"
    };

    private static final List<String> DEFAULT_TOPICS = Arrays.asList(EXAMPLE_TOPICS).subList(0, DialogManager.MAX_OPTIONS);

    public AdManager(WheelApplication app, PrefManager prefManager, Gson gson) {
        this.app = app;
        this.prefManager = prefManager;
        this.gson = gson;
        this.historyItems = new ArrayList<>();
    }

    public TopicsHolder getDefaultWheelTopics() {
        return new TopicsHolder(DEFAULT_TOPICS);
    }

    public AdRequest.Builder createAdBuilderFromPrefs() {
        AdRequest.Builder builder = new AdRequest.Builder()
                .addTestDevice("24FC531E1163DBB1DF67674F1882463C");

        Date birthday = new Date();
        try {
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

        final boolean isFamily = prefManager.getBoolean(FAMILY_LOC, false);

        builder = builder.setIsDesignedForFamilies(isFamily);

        return builder;
    }

    public List<TopicsHolder> getTopicHistory() {
        final long count = prefManager.getLong(HISTORY_COUNT_LOC, 0);
        String saveLoc;
        for (int i = historyItems.size(); i < count; i++) {
            saveLoc = HISTORY_LOC + i;
            String itemString = prefManager.getString(saveLoc, null);
            try {
                historyItems.add(gson.fromJson(itemString, TopicsHolder.class));
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
        final long count = prefManager.getLong(HISTORY_COUNT_LOC, 0);
        final String saveLoc = HISTORY_LOC + count;
        topicString = capitalizeFirstLetter(topicString);

        final String[] topics = topicString.split(",");
        final TopicsHolder item = new TopicsHolder(Arrays.asList(topics), System.currentTimeMillis());
        prefManager.saveString(saveLoc, gson.toJson(item));
        incrementHistoryCount(count);

        sendAdTopicStringToServer(topicString);
    }

    public void clearHistory() {
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
        if (numTopics < DialogManager.MIN_OPTIONS || numTopics > DialogManager.MAX_OPTIONS) {
            return getDefaultWheelTopics();
        } else {
            return topicsHolder;
        }
    }

    public String getMessageFromErrorCode(final int errorCode) {
        switch (errorCode) {
            case 0:
                return "Can\'t load ads right now, perhaps internet issue? Try again later";
            case 3:
            default:
                return "I could not find a video, perhaps try changing your settings below";
        }
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    private void sendAdTopicStringToServer(String topicString) {
        Log.d(TAG, "sendAdTopicString: " + topicString);
        JSONObject jsonObject = new JSONObject();
        try {
            // jsonObject.put("created", System.currentTimeMillis()); // (using server time).
            jsonObject.put("serial", Build.SERIAL);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("mfg", Build.MANUFACTURER);
            jsonObject.put("topics", topicString);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return;
        }

        Intent adIntent = new Intent(app, WebService.class);
        adIntent.putExtra("body", jsonObject.toString());
        adIntent.putExtra("url", WebService.AD_API);
        app.startService(adIntent);
    }
}

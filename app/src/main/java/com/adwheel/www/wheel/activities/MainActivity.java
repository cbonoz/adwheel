package com.adwheel.www.wheel.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.adwheel.www.wheel.BuildConfig;
import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.managers.AdManager;
import com.adwheel.www.wheel.managers.DialogManager;
import com.adwheel.www.wheel.managers.PrefManager;
import com.adwheel.www.wheel.models.TopicsHolder;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

import static com.adwheel.www.wheel.services.WheelHelper.getRandomIndex;
import static com.adwheel.www.wheel.services.WheelHelper.getRandomNumberOfRotations;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private static final String TAG = "MainActivity";

    private MaterialDialog dialog;
    private MaterialDialog loadingDialog = null;

    private RewardedVideoAd mAd;

    private List<LuckyItem> data = new ArrayList<>();

    private TopicsHolder currentTopicsHolder;

    private boolean isRunning = false;

    @Inject
    DialogManager dialogManager;
    @Inject
    PrefManager prefManager;
    @Inject
    AdManager adManager;

    @BindView(R.id.luckyWheel)
    LuckyWheelView luckyWheelView;

    @OnClick(R.id.spinButton)
    void onWheelClick() {
        if (!isRunning) {
            int index = getRandomIndex(data);
            String loadedTopic = data.get(index).topicString;
            loadVideoAdWithTopics(Arrays.asList(loadedTopic));
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
            isRunning = true;
        }
    }

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    // Open the info dialog
    @OnClick(R.id.logoButton)
    public void onLogoClick() {
        // Show info dialog when the header image is clicked.
        Log.d(TAG, "onLogoClick");
        MaterialDialog dialog = dialogManager.createAboutDialog(MainActivity.this);
        dialog.show();
    }

    // Change the options on the wheel dialog.
    @OnClick(R.id.changeWheelButton)
    void onWheelButtonClick() {
        Log.d(TAG, "onWheelButtonClick");
        MaterialDialog dialog = dialogManager.createWheelTopicsDialog(MainActivity.this);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            // Hide the top bar dynamically if present.
            bar.hide();
        }

        WheelApplication.getInjectionComponent().inject(this);
        ButterKnife.bind(this);

        currentTopicsHolder = adManager.getTopicsHolder(AdManager.WHEEL_TOPIC_LOC);

        // Use an activity context to get the rewarded video instance.
        MobileAds.initialize(this, getString(R.string.ad_app_id));
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        loadLuckyWheelData();

        luckyWheelView.setRound(getRandomNumberOfRotations());

        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                isRunning = false;
                String topicsString = data.get(index).topicString;
                showVideoAd(topicsString);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.d(TAG, "onNavSelected: " + item.getTitle());

                        switch (item.getItemId()) {
                            case R.id.action_search:
                                dialog = dialogManager.createSearchDialog(MainActivity.this);
                                break;
                            case R.id.action_settings:
                                dialog = dialogManager.createSettingsDialog(MainActivity.this);
                                break;
                            case R.id.action_history:
                                dialog = dialogManager.createHistoryDialog(MainActivity.this);
                                break;
                        }
                        dialog.show();
                        return true;
                    }
                });


        luckyWheelView.bringToFront();
    }

    public void loadVideoAdWithTopics(List<String> topics) {
        Log.d(TAG, "loadVideo with topics: " + topics.toString());
        AdRequest.Builder adRequestBuilder = adManager.createAdBuilderFromPrefs();
        for (String topic : topics) {
            adRequestBuilder = adRequestBuilder.addKeyword(topic);
        }
        final AdRequest adRequest = adRequestBuilder.build();
        String topicString = TextUtils.join(", ", adRequest.getKeywords());
        // Log.d(TAG, "Loading ad topics: " + topicString);
        mAd.loadAd(getString(R.string.test_ad_unit_id), adRequest);
    }

    private static final int[] wheelColors = new int[]{0xffEDE7F6, 0xffD1C4E9, 0xffB39DDB, 0xffD8BFD8};

    private void loadLuckyWheelData() {
        final int numTopics = currentTopicsHolder.topics.size();
        data.clear();
        final int numColors = wheelColors.length;
        for (int i = 0; i < numTopics; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.color = wheelColors[i % numColors];
            luckyItem.text = currentTopicsHolder.topics.get(i);
            luckyItem.topicString = currentTopicsHolder.topics.get(i);
            luckyItem.icon = R.drawable.star_24;

            data.add(luckyItem);
        }
        luckyWheelView.setData(data);
        Log.d(TAG, "data: " + data);
    }

    private void showVideoAd(String topicString) {
        makeToast("showVideoAd: " + topicString);
        if (BuildConfig.DEBUG || mAd.isLoaded()) {
            // Add back in for deployment.
            mAd.show();
            // TODO: save ad viewing to history.
            adManager.saveTopicStringToHistory(topicString);
        } else {
            final String message = "Video not loaded yet";
            makeToast(message);
            Log.e(TAG, message);
        }
    }

    public void updateWheelTopics(TopicsHolder topicsHolder) {
        Log.d(TAG, "updateWheelTopics");
        currentTopicsHolder = topicsHolder;
        loadLuckyWheelData();
    }

    public void showLoadingDialog() {
        loadingDialog = new MaterialDialog.Builder(this)
                .title(R.string.loading)
                .content(adManager.getRandomLoadingText())
                .progress(true, 0)
                .show();
    }

    private void attemptLoadingDialogDismiss() {
        try {
            loadingDialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "loadingDialog already dismissed");
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ** Ad Video player lifecycle callback methods. ** //

    @Override
    public void onRewarded(RewardItem reward) {
        final String rewardMessage = "onRewarded! currency: " + reward.getType() + "  amount: " + reward.getAmount();
        Log.d(TAG, "Reward: " + rewardMessage);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        // Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
        makeToast(getMessageFromErrorCode(errorCode));
        attemptLoadingDialogDismiss();
        Log.e(TAG, "Video load failed, errorCode: " + errorCode);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded");
        if (!isRunning) {
            Log.d(TAG, "show on load since spinner not rotating");
            attemptLoadingDialogDismiss();
            mAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoOpened");
    }

    @Override
    public void onRewardedVideoStarted() {
        // makeToast("Playing Video");
        Log.d(TAG, "onRewardedVideoStarted");
    }

    private String getMessageFromErrorCode(final int errorCode) {
        switch ( errorCode ) {
            case 0:
                return "Can\'t load ads right now, perhaps internet? Try again later";
            case 3:
            default:
               return "I could not find a video, perhaps change your settings?";
        }
    }
}

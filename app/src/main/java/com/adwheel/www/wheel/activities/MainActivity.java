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

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.managers.AdManager;
import com.adwheel.www.wheel.managers.DialogManager;
import com.adwheel.www.wheel.managers.PrefManager;
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

    private RewardedVideoAd mAd;

    private List<LuckyItem> data = null;
    private List<String> topics;

    private boolean isRunning = false;

    @Inject
    DialogManager dialogManager;
    @Inject
    PrefManager prefManager;
    @Inject
    AdManager adManager;

    @BindView(R.id.luckyWheel)
    LuckyWheelView luckyWheelView;

    @OnClick(R.id.luckyWheel)
    void onWheelClick() {
        if (!isRunning) {
            int index = getRandomIndex(data);
            String loadedTopic = data.get(index).topicString;
            loadVideoAdWithTopics(Arrays.asList(loadedTopic));
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
        }
    }

    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }


        WheelApplication.getInjectionComponent().inject(this);

        ButterKnife.bind(this);

        topics = adManager.getExampleTopics();

        // Use an activity context to get the rewarded video instance.
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        loadLuckyWheelData();

        luckyWheelView.setData(data);
        luckyWheelView.bringToFront();
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
                            case R.id.action_about:
                                dialog = dialogManager.createAboutDialog(MainActivity.this);
                                break;
                        }
                        dialog.show();
                        return true;
                    }
                });
    }

    public void loadVideoAdWithTopics(List<String> topics) {
        AdRequest.Builder adRequestBuilder = adManager.createAdBuilderFromPrefs();
        for (String topic : topics) {
            adRequestBuilder = adRequestBuilder.addKeyword(topic);
        }
        final AdRequest adRequest = adRequestBuilder.build();
        String topicString = TextUtils.join(", ", adRequest.getKeywords());
        Log.d(TAG, "Loading ad topics: " + topicString);
        // mAd.loadAd(getString(R.string.ad_unit_id), adRequest);
    }

    private void loadLuckyWheelData() {
        final int numTopics = topics.size();
        data = new ArrayList<>();
        for (int i = 0; i < numTopics; i++) {
            LuckyItem luckyItem = new LuckyItem();
            if (i % 2 == 0) {
                luckyItem.color = 0xffEDE7F6;
            } else if (i % 3 == 0) {
                luckyItem.color = 0xffD1C4E9;
            } else {
                luckyItem.color = 0xffB39DDB;
            }
            luckyItem.text = topics.get(i);
            luckyItem.icon = R.drawable.star_icon;
            data.add(luckyItem);
        }
        Log.d(TAG, "data: " + data);
    }

    // ** Ad Video player lifecycle callback methods. ** //

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    private void showVideoAd(String topicString) {
        makeToast("showVideoAd");
        if (mAd.isLoaded()) {
            // Add back in for deployment.
            // mAd.show();
            // TODO: save ad viewing to history.
            adManager.saveTopicString(topicString);
        } else {
            final String message = "Video not loaded yet";
            makeToast(message);
            Log.e(TAG, message);
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//    @Subscribe
//    public void onEvent(LoadResponseHistoryCompleteEvent event) {
//        Log.d(TAG, "onEvent - " + event.getClass().getSimpleName());
//        loadingSpinner.setVisibility(View.GONE);
//        responses = event.getResponses();
//        questionManager.setResponses(responses);
//        if (event.isSuccess()) {
//            Log.d(TAG, "Success: " + responses.size() + " responses: " + responses.toArray());
//            slimAdapter.updateData(responses);
//            // TODO: add response visualizations in app for each response type.
//            // renderChartViewForResponseList(responses, "Bar Chart 1");
//        } else {
//            Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
//        }
//    }

}

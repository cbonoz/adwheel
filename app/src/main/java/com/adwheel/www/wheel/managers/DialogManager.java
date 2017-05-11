package com.adwheel.www.wheel.managers;

import android.app.Application;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.activities.MainActivity;
import com.adwheel.www.wheel.adapters.MyTopicAdapter;
import com.adwheel.www.wheel.models.HistoryItem;
import com.adwheel.www.wheel.models.TopicsHolder;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.adwheel.www.wheel.managers.AdManager.BIRTH_YEAR_LOC;
import static com.adwheel.www.wheel.managers.AdManager.DEFAULT_BIRTH_YEAR;
import static com.adwheel.www.wheel.managers.AdManager.FAMILY_LOC;
import static com.adwheel.www.wheel.managers.AdManager.GENDER_LOC;
import static com.adwheel.www.wheel.managers.AdManager.SEARCH_TOPIC_LOC;
import static com.adwheel.www.wheel.managers.AdManager.WHEEL_TOPIC_LOC;
import static com.google.android.gms.internal.zzt.TAG;

public class DialogManager {

    public static final int MAX_OPTIONS = 10;
    public static final String DEFAULT_TOPIC = "technology";

    private final Application application;
    private final PrefManager prefManager;
    private final AdManager adManager;

    public DialogManager(WheelApplication application, PrefManager prefManager, AdManager adManager) {
        this.application = application;
        this.prefManager = prefManager;
        this.adManager = adManager;
    }

    public MaterialDialog createAboutDialog(Context context) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .title(R.string.about_title)
                .customView(R.layout.about_dialog, false)
                .positiveText(R.string.done)
                .build();

        final View view = dialog.getCustomView();
        final String url = context.getString(R.string.about_file);
        final WebView webView = (WebView) view.findViewById(R.id.theWebView);

        loadWebView(webView, url);

        return dialog;
    }

    /**
     * general helper function to load the given url in the webview on the activity layout
     * @param url
     */
    private void loadWebView(WebView view, String url) {
        view.loadUrl(url);
    }

    public MaterialDialog createSettingsDialog(final Context context) {
        // final boolean wrapInScrollView = true;

        final RadioGroup genderGroup;
        final DiscreteSeekBar yearSeekBar;
        final Switch familySwitch;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .title(R.string.settings_title)
                .customView(R.layout.settings_dialog, false)
                .positiveText(R.string.done)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(context, "preferences saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        final View view = dialog.getCustomView();

        // Set gender radio group.
        genderGroup = (RadioGroup) view.findViewById(R.id.genderRadioGroup);
        genderGroup.getCheckedRadioButtonId();
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                // TODO: update.
                Log.d(TAG, "gender onCheckedChanged: " + checkedId);
                final int gender;
                switch (checkedId) {
                    case R.id.femaleButton:
                        gender = AdRequest.GENDER_FEMALE;
                        break;
                    case R.id.maleButton:
                        gender = AdRequest.GENDER_MALE;
                        break;
                    default:
                        gender = AdRequest.GENDER_UNKNOWN;
                        break;
                }
                prefManager.saveInt(GENDER_LOC, gender);
            }
        });

        // TODO: set radio button listeners.
        int currentGender = prefManager.getInt(GENDER_LOC, -1);

        genderGroup.clearCheck();
        final RadioButton femaleButton = (RadioButton) view.findViewById(R.id.femaleButton);
        final RadioButton maleButton = (RadioButton) view.findViewById(R.id.maleButton);
        final RadioButton neutralButton = (RadioButton) view.findViewById(R.id.neutralButton);
        switch (currentGender) {
            case AdRequest.GENDER_FEMALE:
                femaleButton.setChecked(true);
                break;
            case AdRequest.GENDER_MALE:
                maleButton.setChecked(true);
                break;
            default:
                neutralButton.setChecked(true);
                break;
        }

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "selected female");
            }
        });
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "selected male");
            }
        });
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "selected neutral");
            }
        });

        // Set year progress bar.
        final int year = prefManager.getInt(BIRTH_YEAR_LOC, DEFAULT_BIRTH_YEAR);

        yearSeekBar = (DiscreteSeekBar) view.findViewById(R.id.yearSeekBar);
        yearSeekBar.setProgress((int) year);

        final TextView yearSelectedText = (TextView) view.findViewById(R.id.selectedBirthText);
        yearSelectedText.setText(year+"");

        yearSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                prefManager.saveInt(BIRTH_YEAR_LOC, value);
                Log.d(TAG, "onProgressChanged: " + value);
                yearSelectedText.setText(value+"");

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        // Set family progress bar.
        boolean family = prefManager.getBoolean(FAMILY_LOC);
        familySwitch = (Switch) view.findViewById(R.id.familySwitch);
        familySwitch.setChecked(family);
        familySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefManager.saveBoolean(FAMILY_LOC, isChecked);
                Log.d(TAG, "onCheckedChanged: " + isChecked);
            }
        });

        return dialog;
    }

    public MaterialDialog createSearchDialog(final MainActivity context) {
        final List<String> myTopics = adManager.getTopicsHolder(SEARCH_TOPIC_LOC).topics;

        MaterialDialog searchTopicsDialog = new MaterialDialog.Builder(context)
                .title(R.string.search_title)
                .customView(R.layout.select_topics_dialog, false)
                .positiveText(R.string.save_and_play)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adManager.saveTopicsHolder(SEARCH_TOPIC_LOC, new TopicsHolder(myTopics));
                        Toast.makeText(context, "Loading ad: " + myTopics, Toast.LENGTH_SHORT).show();
                        context.loadVideoAdWithTopics(myTopics);
                        context.showLoadingDialog();
                    }
                }).negativeText(R.string.save)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        adManager.saveTopicsHolder(SEARCH_TOPIC_LOC, new TopicsHolder(myTopics));
                        Toast.makeText(context, "saved topics", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        View optionView = searchTopicsDialog.getCustomView();
        LinearLayout container = (LinearLayout) optionView.findViewById(R.id.optionContainer);

        final ListView myList = (ListView) container.findViewById(R.id.optionList);
        myList.setItemsCanFocus(true);

        final MyTopicAdapter myTopicAdapter = new MyTopicAdapter(context, optionView, myTopics);


        final String exampleOption = "new topic";
        if (myTopics.size() < 1) {
            myTopics.add(exampleOption);
        }

        myList.setAdapter(myTopicAdapter);
        myTopicAdapter.notifyDataSetChanged();

        Button addButton = (Button) optionView.findViewById(R.id.addOptionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myTopics.size() < MAX_OPTIONS) {
                    myTopics.add(exampleOption);
                    myTopicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Max Topic Count Exceeded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return searchTopicsDialog;
    }

    public MaterialDialog createHistoryDialog(final MainActivity context) {

        final RecyclerView historyListView;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .title(R.string.history_title)
                .theme(Theme.LIGHT)
                .customView(R.layout.history_dialog, false)
                .positiveText(R.string.done)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d(TAG, "done viewing history");
                    }
                })
                .build();

        final View view = dialog.getCustomView();

        historyListView = (RecyclerView) view.findViewById(R.id.historyRecyclerView);
        historyListView.setLayoutManager(new LinearLayoutManager(context));

        SpinKitView loadingSpinner = (SpinKitView) view.findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        final SlimAdapter slimAdapter = SlimAdapter.create()
                .register(R.layout.history_item, new SlimInjector<HistoryItem>() {
                    @Override
                    public void onInject(final HistoryItem data, IViewInjector injector) {
                        final String metaData = String.format(Locale.US, "%s", new Date(data.timestamp));
                        injector.text(R.id.topics, data.topics)
                                .text(R.id.metadata, metaData)
                                .textColor(R.id.metadata, R.color.primary_light)
                                .textSize(R.id.metadata, 12)
                                .clicked(R.id.playButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        List<String> topicsList = Arrays.asList(data.topics.split(","));
                                        context.loadVideoAdWithTopics(topicsList);
                                        context.showLoadingDialog();
                                    }
                                });
                    }
                }).attachTo(historyListView);

        // TODO: make async.
        List<HistoryItem> historyItems = adManager.getTopicHistory();
        slimAdapter.updateData(historyItems);

        loadingSpinner.setVisibility(View.GONE);
        if (historyItems.isEmpty()) {
            TextView noHistoryText = (TextView) view.findViewById(R.id.noHistoryText);
            noHistoryText.setVisibility(View.VISIBLE);
        }


        return dialog;
    }

    public MaterialDialog createWheelTopicsDialog(final MainActivity context) {
        // Load the current wheel topics.
        final List<String> myTopics = new ArrayList<>(adManager.getTopicsHolder(WHEEL_TOPIC_LOC).topics);

        MaterialDialog wheelTopicsDialog = new MaterialDialog.Builder(context)
                .title(R.string.wheel_title)
                .customView(R.layout.select_topics_dialog, false)
                .positiveText(R.string.save)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (myTopics.size() < 2) {
                            Toast.makeText(context, "Must have at least 2 options", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        TopicsHolder topicsHolder = new TopicsHolder(myTopics);
                        adManager.saveTopicsHolder(WHEEL_TOPIC_LOC, topicsHolder);
                        context.updateWheelTopics(topicsHolder);
                        Log.d(TAG, "updated topics");
                        dialog.dismiss();
                    }
                }).negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d(TAG, "cancelled change of wheel topics");
                        dialog.dismiss();
                    }
                }).build();

        View optionView = wheelTopicsDialog.getCustomView();
        LinearLayout container = (LinearLayout) optionView.findViewById(R.id.optionContainer);

        final ListView myList = (ListView) container.findViewById(R.id.optionList);
        myList.setItemsCanFocus(true);

        final MyTopicAdapter myTopicAdapter = new MyTopicAdapter(context, optionView, myTopics);

        myList.setAdapter(myTopicAdapter);
        myTopicAdapter.notifyDataSetChanged();

        Button addButton = (Button) optionView.findViewById(R.id.addOptionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myTopics.size() < MAX_OPTIONS) {
                    myTopics.add(DEFAULT_TOPIC);
                    myTopicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Max Topic Count Exceeded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return wheelTopicsDialog;
    }
}

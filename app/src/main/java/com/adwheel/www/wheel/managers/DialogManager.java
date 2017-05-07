package com.adwheel.www.wheel.managers;

import android.app.Activity;
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
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.activities.MainActivity;
import com.adwheel.www.wheel.adapters.MyTopicAdapter;
import com.adwheel.www.wheel.models.HistoryItem;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.adwheel.www.wheel.managers.AdManager.BIRTH_YEAR_LOC;
import static com.adwheel.www.wheel.managers.AdManager.DEFAULT_BIRTH_YEAR;
import static com.adwheel.www.wheel.managers.AdManager.FAMILY_LOC;
import static com.adwheel.www.wheel.managers.AdManager.GENDER_LOC;
import static com.adwheel.www.wheel.managers.AdManager.MALE;
import static com.adwheel.www.wheel.managers.AdManager.TOPIC_LOC;
import static com.google.android.gms.internal.zzt.TAG;

public class DialogManager {

    private final int MAX_OPTIONS = 10;

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
                .positiveText(R.string.save)
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
                prefManager.saveString(GENDER_LOC, MALE);
            }
        });

        // Set year progress bar.
        long year = prefManager.getLong(BIRTH_YEAR_LOC, DEFAULT_BIRTH_YEAR);

        yearSeekBar = (DiscreteSeekBar) view.findViewById(R.id.yearSeekBar);
        yearSeekBar.setProgress((int) year);
        yearSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                prefManager.saveLong(BIRTH_YEAR_LOC, value);
                Log.d(TAG, "onProgressChanged: " + value);
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

    public MaterialDialog createSearchDialog(final Activity context) {
        final List<String> myTopics = new ArrayList<>();

        MaterialDialog optionDialog = new MaterialDialog.Builder(context)
                .title(R.string.search_title)
                .customView(R.layout.select_topics_dialog, false)
                .positiveText(R.string.save_and_play)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String topicString = TextUtils.join(",", myTopics);
                        prefManager.saveString(TOPIC_LOC, topicString);
                        Toast.makeText(context, "Loading ad: " + topicString, Toast.LENGTH_SHORT).show();
                        ((MainActivity) context).loadVideoAdWithTopics(myTopics);
                    }
                }).negativeText(R.string.save)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String topicString = TextUtils.join(",", myTopics);
                        prefManager.saveString(TOPIC_LOC, topicString);
                        Toast.makeText(context, "saved topics", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();

        View optionView = optionDialog.getCustomView();
        LinearLayout container = (LinearLayout) optionView.findViewById(R.id.optionContainer);

        final ListView myList = (ListView) container.findViewById(R.id.optionList);
        myList.setItemsCanFocus(true);

        final MyTopicAdapter myTopicAdapter = new MyTopicAdapter(context, optionView, myTopics);


        String topics = prefManager.getString(TOPIC_LOC, null);
        if (topics != null) {
            String[] topicsList = topics.split(",");
            for (String topic : topicsList) {
                myTopics.add(topic);
            }
        }

        final String exampleOption = "edit me";
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

        return optionDialog;
    }

    public MaterialDialog createHistoryDialog(final Activity context) {

        final RecyclerView historyListView;

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .title(R.string.history_title)
                .customView(R.layout.history_dialog, false)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(context, "preferences saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        final View view = dialog.getCustomView();

        historyListView = (RecyclerView) view.findViewById(R.id.historyRecyclerView);
        historyListView.setLayoutManager(new LinearLayoutManager(context));

        final SlimAdapter slimAdapter = SlimAdapter.create()
                .register(R.layout.history_item, new SlimInjector<HistoryItem>() {
                    @Override
                    public void onInject(HistoryItem data, IViewInjector injector) {
                        final String metaData = String.format(Locale.US, "%s", new Date(data.timestamp));
                        injector.text(R.id.topics, data.topics)
                                .text(R.id.metadata, metaData)
                                .textColor(R.id.metadata, R.color.primary_light)
                                .textSize(R.id.metadata, 12);
                    }
                }).attachTo(historyListView);


        // TODO: make async.

        List<HistoryItem> historyItems = topic
        slimAdapter.updateData(historyItems);
        return dialog;
    }

}

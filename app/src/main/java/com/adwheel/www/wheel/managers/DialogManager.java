package com.adwheel.www.wheel.managers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.WheelApplication;
import com.adwheel.www.wheel.activities.MainActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

import static com.adwheel.www.wheel.managers.AdManager.BIRTH_YEAR_LOC;
import static com.adwheel.www.wheel.managers.AdManager.DEFAULT_BIRTH_YEAR;
import static com.adwheel.www.wheel.managers.AdManager.FAM_LOC;
import static com.adwheel.www.wheel.managers.AdManager.GENDER_LOC;
import static com.adwheel.www.wheel.managers.AdManager.MALE;
import static com.adwheel.www.wheel.managers.AdManager.TOPIC_LOC;
import static com.google.android.gms.internal.zzt.TAG;

public class DialogManager {

    private final int MAX_OPTIONS = 10;

    private final Application application;
    private final PrefManager prefManager;

    public DialogManager(WheelApplication application, PrefManager prefManager) {
        this.application = application;
        this.prefManager = prefManager;
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
        boolean family = prefManager.getBoolean(FAM_LOC);
        familySwitch = (Switch) view.findViewById(R.id.familySwitch);
        familySwitch.setChecked(family);
        familySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefManager.saveBoolean(FAM_LOC, isChecked);
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
                .positiveText(R.string.done)
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String topicString = TextUtils.join(",", myTopics);
                        prefManager.saveString(TOPIC_LOC, topicString);
                        Toast.makeText(context, "loadVideoWithTopics: " + topicString, Toast.LENGTH_SHORT).show();
                        ((MainActivity) context).loadVideoAdWithTopics(myTopics);
                    }
                })
                .show();

        View optionView = optionDialog.getCustomView();
        LinearLayout container = (LinearLayout) optionView.findViewById(R.id.optionContainer);

        final ListView myList = (ListView) container.findViewById(R.id.optionList);
        myList.setItemsCanFocus(true);

        final MyAdapter myAdapter = new MyAdapter(context, optionView, myTopics);


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

        myList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        Button addButton = (Button) optionView.findViewById(R.id.addOptionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myTopics.size() < MAX_OPTIONS) {
                    myTopics.add(exampleOption);
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Max Topic Count Exceeded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return optionDialog;
    }

    private class MyAdapter extends BaseAdapter {
        private final Activity activity;
        private final View optionView;
        private final List<String> myOptions;

        public MyAdapter(Activity activity, View optionView, List<String> myOptions) {
            this.activity = activity;
            this.optionView = optionView;
            this.myOptions = myOptions;
        }

        public int getCount() {
            return myOptions.size();
        }

        public String getItem(int position) {
            return myOptions.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.option_item, parent, false);
            holder = new ViewHolder();
            holder.optionEditText = (EditText) convertView.findViewById(R.id.optionEditText);
            holder.deleteButton = (Button) convertView.findViewById(R.id.deleteButton);

            holder.optionEditText.setFocusable(true);
            holder.optionEditText.requestFocus();
            holder.optionEditText.setText(getItem(position));

            // / this updates tag of
            // the button view as we
            // scroll ///

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (myOptions.size() > 1) {
                        myOptions.remove(position);
                        Log.d("GCM", "Item removed from position: " + position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(activity, "You must have at least one option", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.optionEditText.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    // TODO Auto-generated method stub

                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    // TODO Auto-generated method stub

                }

                public void afterTextChanged(Editable s) {
                    if(position < myOptions.size()) {
                        myOptions.set(position, s.toString());
                    }
                }
            });

            holder.deleteButton.setBackgroundResource(R.drawable.zzz_delete);

            return convertView;
        }
    }

    private class ViewHolder {
        EditText optionEditText;
        Button deleteButton;
    }
}

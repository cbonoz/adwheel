package com.adwheel.www.wheel.adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.adwheel.www.wheel.R;
import com.adwheel.www.wheel.managers.AdManager;
import com.adwheel.www.wheel.managers.DialogManager;

import java.util.List;

public class MyTopicAdapter extends BaseAdapter {

    private final ArrayAdapter<String> adapter;

    private final Activity activity;
    private final View optionView;
    private final List<String> myOptions;

    public MyTopicAdapter(Activity activity, View optionView, List<String> myOptions) {
        this.activity = activity;
        this.optionView = optionView;
        this.myOptions = myOptions;
        adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, AdManager.EXAMPLE_TOPICS);
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
        holder.optionEditText = (AutoCompleteTextView) convertView.findViewById(R.id.optionEditText);


        holder.optionEditText.setAdapter(adapter);

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
                if (myOptions.size() > DialogManager.MIN_OPTIONS) {
                    myOptions.remove(position);
                    Log.d("GCM", "Item removed from position: " + position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, "You must have at least " + DialogManager.MIN_OPTIONS + " option.", Toast.LENGTH_SHORT).show();
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

    private class ViewHolder {
        AutoCompleteTextView optionEditText;
        Button deleteButton;
    }
}

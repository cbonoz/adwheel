package com.adwheel.www.wheel.managers;

import android.app.Application;
import android.content.Context;

import com.adwheel.www.wheel.WheelApplication;
import com.afollestad.materialdialogs.MaterialDialog;

public class DialogManager {

    private final Application application;

    public DialogManager(WheelApplication application) {
        this.application = application;
    }

    public MaterialDialog createAboutDialog(Context context) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .build();
        return dialog;
    }

    public MaterialDialog createSettingsDialog(Context context) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .build();
        return dialog;
    }

    public MaterialDialog createSearchDialog(Context context) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .build();
        return dialog;
    }
}

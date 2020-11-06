package com.example.wombatapp.minttihealth.health;

import android.content.Context;

import com.example.wombatapp.R;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by ccl on 2016/6/13.
 * AlertDialogBuilder
 */
public final class AlertDialogBuilder extends AlertDialog.Builder {

    public AlertDialogBuilder(Context context) {
        this(context, R.style.AppDialogStyle);
    }

    public AlertDialogBuilder(Context context, int theme) {
        super(context, theme);
    }
}

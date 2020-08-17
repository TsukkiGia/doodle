package com.example.oneinamillion.Models;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.oneinamillion.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;
    String signOrLog;

    public LoadingDialog(Activity activity, String signOrLog) {
        this.activity = activity;
        this.signOrLog = signOrLog;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        if (signOrLog.equals("login")) {
            builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null));
        }
        if (signOrLog.equals("edit")){
            builder.setView(inflater.inflate(R.layout.custom_edit_progress, null));
        }
        if (signOrLog.equals("save")){
            builder.setView(inflater.inflate(R.layout.custom_save_progress, null));
        }
        if (signOrLog.equals("signup")){
            builder.setView(inflater.inflate(R.layout.custom_signing_up_dialog, null));
        }
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}

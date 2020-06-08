package com.example.pruebavision;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;


public class DialogCargando {

    Activity activity;
    AlertDialog dialog;

    public DialogCargando(Activity mActivity) {
        activity = mActivity;
    }

    public void startCargando()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_cargando,null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void stopCargando()
    {
        dialog.dismiss();
    }
}

package com.example.pruebavision;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class AlertTxt {

    public AlertTxt() {
    }

    public void msg(String txt, Context contexto)
    {
        final AlertDialog.Builder mensaje = new AlertDialog.Builder(contexto);
        mensaje.setMessage(txt);
        mensaje.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        mensaje.create();
        mensaje.show();
    }
  /*  public void stadisticas(View view,Context contexto) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Name");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.estadisticas, null);
        customLayout = get
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/


}

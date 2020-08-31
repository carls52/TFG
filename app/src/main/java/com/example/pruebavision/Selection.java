package com.example.pruebavision;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.pruebavision.local.Local;
import com.example.pruebavision.online.Login;

public class Selection extends AppCompatActivity {

    private Button modoLocal,modoOnline,btn_pruebas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_selection);
        modoLocal = findViewById(R.id.modoLocal);
        modoOnline = findViewById(R.id.modoOnline);

        modoLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Selection.this, Local.class);
                startActivity(i);
            }
        });
        modoOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Selection.this, Login.class);
                startActivity(i);
                finish();
            }
        });
        btn_pruebas = findViewById(R.id.btn_pruebas);
        btn_pruebas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Selection.this,grupo.class);
                startActivity(i);
            }
        });
    /*    btn_pruebas = findViewById(R.id.btn_pruebas);
        btn_pruebas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Selection.this,grupo.class);
                startActivity(i);
        });*/

    }
}

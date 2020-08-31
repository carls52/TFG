package com.example.pruebavision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pruebavision.online.Registro;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class grupo extends AppCompatActivity {

    private TextInputEditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        Button add = findViewById(R.id.add_group);

        RecyclerView listGroup = findViewById(R.id.miembros);
        name = findViewById(R.id.add_name_group);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMember = name.getText().toString();
                Query uQuery = FirebaseDatabase.getInstance().getReference()
                        .child("Usuarios").orderByChild("nombre")
                        .equalTo(newMember);
                uQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            AlertTxt alert = new AlertTxt();
                            alert.msg("El nombre de usuario ya esta en uso."
                                    , grupo.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
package com.example.pruebavision.online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.pruebavision.AlertTxt;
import com.example.pruebavision.Datos;
import com.example.pruebavision.DialogCargando;
import com.example.pruebavision.R;
import com.example.pruebavision.Selection;
import com.example.pruebavision.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button Registrarse;
    Button Entrar;
    FirebaseAuth mAuth;
    EditText correo,contraseña;
    CheckBox recuerdame;
    ImageView back;
    private Usuario usuario;
    private SharedPreferences mPrefs;
    private SharedPreferences sharedPreferences;
    private String correoGuardado,contraseñaGuardada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        final DialogCargando cargando = new DialogCargando(Login.this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        correo = findViewById(R.id.correo);
        contraseña = findViewById(R.id.contraseña);
        recuerdame = findViewById(R.id.recuerdame);
        back = findViewById(R.id.backLogin);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Selection.class);
                startActivity(i);
                finish();
            }
        });
     /*   ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);*/

/*     Datos prueba = new Datos(this);
     String texto = null;
        String a = null;
        try {
            texto = prueba.encrypt("texto");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            a = prueba.decrypt(texto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            prueba.Encriptar("Id","Hello moto");
            String z = prueba.LeerEncriptado("Id");
            int aasd = 22;
        } catch (Exception e) {
            e.printStackTrace();
        }

*/


            Datos d = new Datos(this);
            try {
                correoGuardado = d.leerEncriptado("Correo");
                contraseñaGuardada = d.leerEncriptado("Contraseña");
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!correoGuardado.equals("") && !contraseñaGuardada.equals("")) {
                cargando.startCargando();
                mAuth.signInWithEmailAndPassword(correoGuardado, contraseñaGuardada)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Intent i = new Intent(Login.this, menu_main.class);
                                    startActivity(i);
                                    finish();
                                }
                                else
                                {
                                    cargando.stopCargando();
                                }
                            }
                        });

            }


        //Si pulsamos el boton Registrarse
        Registrarse = findViewById(R.id.guardar);
        Registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(Login.this, Registro.class);
                startActivity(t);
                finish();
            }

        });
        //Si pulsamos el boton Entrar
        Entrar = findViewById(R.id.entrar);
        Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargando.startCargando();
                if(correo.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty())
                {
                    AlertTxt alert = new AlertTxt();
                    alert.msg("Por favor rellene todos los campos.",Login.this);
                }
                else
                {
                    Acceder(correo.getText().toString().trim(), contraseña.getText().toString().trim(),recuerdame.isChecked());
                }
                cargando.stopCargando();
            }
        });
    }

    private void Acceder(final String correo, final String contraseña, final boolean isChecked) {
        mAuth.signInWithEmailAndPassword(correo,contraseña)
           .addOnCompleteListener(new OnCompleteListener<AuthResult>()
           {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task)
               {
                   if(task.isSuccessful())
                   {

                           DatabaseReference reference = FirebaseDatabase.getInstance()
                               .getReference("Usuarios")
                               .child(mAuth.getCurrentUser().getUid());
                       reference.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               usuario = dataSnapshot.getValue(Usuario.class);
                               //  SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                  /* prefsEditor.putString("Usuario", json);
                                   prefsEditor.commit();
                                   prefsEditor.putString("Login","hola");
                                   prefsEditor.commit();*/

                                Datos d = new Datos(Login.this);
                               try {
                                   d.setUsuario(usuario);
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                               if(isChecked) {
                                   try {
                                       d.encriptar("Contraseña", contraseña);
                                       d.encriptar("Correo", correo);
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }

                                   Intent i = new Intent(Login.this, menu_main.class);
                                   startActivity(i);
                                   finish();
                               }
                               else {
                                   Intent i = new Intent(Login.this, menu_main.class);
                                   startActivity(i);
                                   finish();
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }
                   else
                   {
                      AlertTxt alert = new AlertTxt();
                      alert.msg("Error al iniciar sesión, revise los datos"
                              ,Login.this);
                   }
               }
           });



    }
}

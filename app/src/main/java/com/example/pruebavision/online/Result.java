package com.example.pruebavision.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.pruebavision.AlertTxt;
import com.example.pruebavision.CodigoPrivado;
import com.example.pruebavision.CodigoPublico;
import com.example.pruebavision.Comentario;
import com.example.pruebavision.Datos;
import com.example.pruebavision.MainActivity;
import com.example.pruebavision.R;
import com.example.pruebavision.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Result extends AppCompatActivity {

    private EditText mText,titulo, descripcion;
    private ImageView flecha;
    private Button guardar;
    private FirebaseAuth mAuth;


    private Usuario usuario;
    private Spinner tipo,visible;
    private String codigo;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Usuarios");
    private DataSnapshot user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        descripcion = findViewById(R.id.desc);
        tipo = findViewById(R.id.tipo);
        visible = findViewById(R.id.visible);
        titulo = findViewById(R.id.titulo);
        guardar = findViewById(R.id.guardar);
        flecha = findViewById(R.id.flecha2);
        mText = findViewById(R.id.captured_text);

        mAuth = FirebaseAuth.getInstance();

        Datos datos = new Datos(this);
        try {
            usuario = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //recibimos el codigo de la actividad anterior
        codigo = getIntent().getStringExtra("ocrText");
        mText.setText(codigo);


        flecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Result.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //inicializamos los valores de los spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipos,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);

        //recibimos el valor del spinner de la actividad anterior
        String aux = getIntent().getStringExtra("tipo");
        tipo.setSelection(adapter.getPosition(aux));
        visible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(visible.getSelectedItem().toString().equals("PRIVADO"))
                {
                    descripcion.setVisibility(View.GONE);
                }
                else if (visible.getSelectedItem().toString().equals("PUBLICO"))
                {
                    descripcion.setVisibility(View.VISIBLE);
                }
                else
                {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.visibilidad,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visible.setAdapter(adapter2);

        //Toast.makeText(Result.this,tipo.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //comprobamos que no haya campos vacíos
                if(!titulo.getText().toString().isEmpty()
                        && !titulo.getText().toString().equals(" ")
                        && !mText.getText().toString().isEmpty())
                {
                    //creamos un nuevo anuncio y lo añadimos a la lista de la base de datos
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    if(visible.getSelectedItem().toString().equals("PRIVADO"))
                    {
                        CodigoPrivado nuevo = new CodigoPrivado(mText.getText().toString().trim(),
                                titulo.getText().toString().trim(),
                                usuario.getNombre().trim(),
                                formatter.format(date),
                                tipo.getSelectedItem().toString());

                        reference.child(mAuth.getCurrentUser().getUid()).removeValue();
                        usuario.addCodigoPrivado(nuevo);
                        reference.child(mAuth.getCurrentUser().getUid()).setValue(usuario);

                        Datos d = new Datos(Result.this);
                        try {
                            d.setUsuario(usuario);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        reference = database.getReference("Publico");
                        DatabaseReference newPostRef = reference.push();
                        ArrayList<Comentario> lista = new ArrayList<>();
                        Comentario c = new Comentario("x","x","x");
                        lista.add(c);

                        CodigoPublico nuevo = new CodigoPublico(newPostRef.getKey(),
                                titulo.getText().toString().trim(),
                                mText.getText().toString().trim(),
                                descripcion.getText().toString().trim(),
                                usuario.getNombre(),
                                formatter.format(date),
                                0,
                                0,
                                lista,
                                tipo.getSelectedItem().toString(),
                                mAuth.getCurrentUser().getUid());

                        newPostRef.setValue(nuevo);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(Result.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setCancelable(false);
                    builder.setMessage("Se ha guardado correctamente");
                    builder.show();
                }
                else
                {
                    AlertTxt a = new AlertTxt();
                    a.msg("Por favor rellene todos los campos",Result.this);
                }
            }
        });


    }

}

package com.example.pruebavision.local;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.pruebavision.R;

public class ResultLocal extends AppCompatActivity {
    private EditText mText,titulo, descripcion;
    private ImageView flecha;
    private Button guardar;
    private Spinner tipo;
    private String codigo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_local);

        descripcion = findViewById(R.id.desc);
        tipo = findViewById(R.id.tipo);

        titulo = findViewById(R.id.titulo);
        guardar = findViewById(R.id.guardar);
        flecha = findViewById(R.id.flecha2);
        mText = findViewById(R.id.captured_text);

        codigo = getIntent().getStringExtra("ocrText");
        mText.setText(codigo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipos,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);

        //recibimos el valor del spinner de la actividad anterior
        String aux = getIntent().getStringExtra("tipo");
        tipo.setSelection(adapter.getPosition(aux));
    }
}

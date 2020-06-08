package com.example.pruebavision;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodigoPublicoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodigoPublicoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String Id;
    private DatabaseReference reference;
    private CodigoPublico codigo;
    private RecyclerView recycler;
    private Button agregarComentario;
    private TextView comentarioNuevo,sinComentarios;
    private EditText titulo,codigoPublico,descripcion ;
    private FirebaseAuth auth;
    private Button guardar,stats;


    public CodigoPublicoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodigoPublicoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodigoPublicoFragment newInstance(String param1, String param2) {
        CodigoPublicoFragment fragment = new CodigoPublicoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Id = getArguments().getString("Id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        Id = getArguments().getString("Id");
        View root = inflater.inflate(R.layout.fragment_codigo_publico, container, false);
       // final TextView titulo,codigoPublico,descripcion;
        titulo = root.findViewById(R.id.TituloCodigoPublico);
        codigoPublico = root.findViewById(R.id.CodigoPublico);
        descripcion = root.findViewById(R.id.DescripcionCodigoPublico);
        agregarComentario = root.findViewById(R.id.comentarioNuevoBoton);
        comentarioNuevo = root.findViewById(R.id.comentarioNuevo);
        recycler = root.findViewById(R.id.comentariosPublicos);
        sinComentarios = root.findViewById(R.id.SinComentarios);
        guardar = root.findViewById(R.id.guardarPublico);
        stats = root.findViewById(R.id.stats);

        reference = FirebaseDatabase.getInstance().getReference("Publico").child(Id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                codigo = dataSnapshot.getValue(CodigoPublico.class);
                if(codigo!=null) {
                    if (codigo.getIdAutor().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        titulo.setClickable(true);
                        titulo.setFocusable(true);
                        titulo.setFocusableInTouchMode(true);
                        descripcion.setClickable(true);
                        descripcion.setFocusable(true);
                        descripcion.setFocusableInTouchMode(true);
                        codigoPublico.setClickable(true);
                        codigoPublico.setFocusable(true);
                        codigoPublico.setFocusableInTouchMode(true);
                        guardar.setVisibility(View.VISIBLE);
                        stats.setVisibility(View.VISIBLE);

                    }
                    else {
                        codigo.addVisualizacion();
                        reference.removeValue();
                        reference.setValue(codigo);
                    }
                    titulo.setText(codigo.getTitulo());
                    codigoPublico.setText(codigo.getCodigo());
                    descripcion.setText(codigo.getDescripcion());
                    if (!codigo.getComentarios().get(0).getComentario().equals("x")
                            && !codigo.getComentarios().get(0).getAutor().equals("x")
                            && !codigo.getComentarios().get(0).getAutorId().equals("x")) {
                        recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        AdapterComentarios adapter = new AdapterComentarios(codigo.getComentarios());
                        recycler.setAdapter(adapter);

                    } else {
                        sinComentarios.setVisibility(View.VISIBLE);
                    }
                }
                agregarComentario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (comentarioNuevo.getText().toString().length() != 0) {
                            Datos datos = new Datos(getContext());
                            Usuario u = null;
                            try {
                                u = datos.getUsuario();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            codigo.addComentario(new Comentario(u.getNombre(),
                                    auth.getCurrentUser().getUid(),
                                    comentarioNuevo.getText().toString().trim()));
                            reference.child(codigo.getID()).removeValue();
                            reference.setValue(codigo);
                            sinComentarios.setVisibility(View.GONE);
                            comentarioNuevo.setText("");
                            recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                            AdapterComentarios adapter = new AdapterComentarios(codigo.getComentarios());
                            recycler.setAdapter(adapter);
                            comentarioNuevo.clearFocus();
                        }
                    }

                });

                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(HayCambios(codigo))
                        {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            CodigoPublico nuevo = new CodigoPublico(codigo.getID(),
                                    titulo.getText().toString().trim(),
                                    codigoPublico.getText().toString().trim(),
                                    descripcion.getText().toString().trim(),
                                    codigo.getAutor(),
                                    formatter.format(date),
                                    codigo.getDescargas(),
                                    codigo.getVisualizaciones(),
                                    codigo.getComentarios(),
                                    codigo.getTipo(),
                                    codigo.getIdAutor());
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Publico").child(codigo.getID());
                            ref.removeValue();
                            ref.setValue(nuevo);

                            AlertTxt alert = new AlertTxt();
                            alert.msg("Cambios realizados",getContext());
                        }
                    }
                });
                stats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Estadisticas(getContext(),codigo);
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    public boolean HayCambios(CodigoPublico c)
    {
        return !(c.getCodigo().equals(codigoPublico.getText().toString().trim())
            && c.getTitulo().equals(titulo.getText().toString().trim())
            && c.getDescripcion().equals(descripcion.getText().toString().trim()));

    }
    public void Estadisticas(Context contexto,CodigoPublico codigo) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.estadisticas, null);
        TextView visualizaciones,descargas;
        visualizaciones = customLayout.findViewById(R.id.visualizaciones);
        descargas = customLayout.findViewById(R.id.descargas);
        visualizaciones.setText(String.valueOf(codigo.getVisualizaciones()));
        descargas.setText(String.valueOf(codigo.getDescargas()));
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
    }
}

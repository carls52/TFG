package com.example.pruebavision;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.pruebavision.ui.Favoritos;
import com.example.pruebavision.ui.RepPrivado.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Repositorio extends Fragment {

    private HomeViewModel viewModel;
    private String tipo;

    private FirebaseAuth auth ;
    private FirebaseDatabase bbdd = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private ArrayList<String> l;
    private RecyclerView recycler;
    private Usuario user;
    private String ID;
    private TextView error;
    private ArrayList<CodigoPrivado> listaAux;
    private Bundle bundle;
    private Usuario actual;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        auth = FirebaseAuth.getInstance();
        ID = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(ID);

        View root = inflater.inflate(R.layout.fragment_first2, container, false);

        tipo = getArguments().getString("tipo");
        recycler = root.findViewById(R.id.recycler);
        error = root.findViewById(R.id.sinResultados);



        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final DialogCargando cargando = new DialogCargando(getActivity());
        cargando.startCargando();

        listaAux = new ArrayList<>();
     /*   reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(Usuario.class);

                for(CodigoPrivado c : user.getCodigosPrivados())
                {
                    if(c.getTipo().equals(tipo) && c.getTipo()!=null)
                    {
                        listaAux.add(c);
                    }
                }
                recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
                AdapterDatosPrivados adapter = new AdapterDatosPrivados(listaAux);
                recycler.setAdapter(adapter);
                if(listaAux.isEmpty())
                {
                    error.setVisibility(View.VISIBLE);
                }
                cargando.stopCargando();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        final Datos datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(CodigoPrivado c : actual.getCodigosPrivados())
        {
            if(c.getTipo().equals(tipo) && c.getTipo()!=null)
            {
                listaAux.add(c);
            }
        }
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        final AdapterDatosPrivados adapter = new AdapterDatosPrivados(listaAux);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListerner(new AdapterDatosPrivados.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                bundle = new Bundle();
                bundle.putString("Id",String.valueOf(position));
                bundle.putString("Tipo",tipo);
                NavHostFragment.findNavController(Repositorio.this)
                        .navigate(R.id.action_first2Fragment_to_repositorioPrivado,bundle);
            }

            @Override
            public void onDeleteClick(final int position) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                mensaje.setMessage("¿De verdad quieres borrarlo?");
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CodigoPrivado c = listaAux.get(position);
                        Datos datos = new Datos(getContext());
                        actual.getCodigosPrivados().remove(c);
                        listaAux.remove(position);
                        adapter.notifyItemRemoved(position);
                        reference.removeValue();
                        reference.setValue(actual);
                        try {
                            datos.setUsuario(actual);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mensaje.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mensaje.create();
                mensaje.show();
            }

            @Override
            public void onDownloadClick(int position) throws FileNotFoundException {
                if(datos.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE,getContext())) {
                    File f1 = Environment.getExternalStoragePublicDirectory("/vision/"+actual.getNombre()+"/"+tipo);
                    String extension = null;
                    switch (listaAux.get(position).getTipo()) {

                        case "JAVA":
                            extension = ".java";
                            break;
                        case "PYTHON":
                            extension = ".py";
                            break;
                        case "C":
                            extension = ".c";
                            break;
                        case "C++":
                            extension = ".cpp";
                            break;
                        case "JAVASCRIPT":
                            extension = ".js";
                            break;
                        default:
                            extension = ".txt";
                            break;
                    }

                    File dir = new File(f1 , listaAux.get(position).getTitulo()+extension);
                    f1.mkdirs();
                    try {
                            FileWriter filewriter = new FileWriter(dir, false);
                            BufferedWriter out = new BufferedWriter(filewriter);
                            out.write(listaAux.get(position).getCodigo());
                            out.close();
                            AlertTxt alert = new AlertTxt();
                            alert.msg("Archivo guardado",getContext());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });
        if(listaAux.isEmpty())
        {
            error.setVisibility(View.VISIBLE);
        }
        cargando.stopCargando();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}

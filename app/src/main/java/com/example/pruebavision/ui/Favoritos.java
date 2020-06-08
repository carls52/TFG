package com.example.pruebavision.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebavision.AdapterDatosPublicos;
import com.example.pruebavision.AlertTxt;
import com.example.pruebavision.CodigoPublico;
import com.example.pruebavision.Datos;
import com.example.pruebavision.DialogCargando;
import com.example.pruebavision.R;
import com.example.pruebavision.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favoritos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favoritos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recycler;
    private DatabaseReference reference,referenceUser;
    private ArrayList<CodigoPublico> listaAux;
    private CodigoPublico codigo;
    private AdapterDatosPublicos adapter;
    private FirebaseAuth auth;
    private Bundle bundle;
    private TextView sinResultadosFav;
    private Usuario actual;
    private FirebaseAuth Auth;

    public Favoritos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favoritos.
     */
    // TODO: Rename and change types and number of parameters
    public static Favoritos newInstance(String param1, String param2) {
        Favoritos fragment = new Favoritos();
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
        }
        reference = FirebaseDatabase.getInstance().getReference("Publico");
        Auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);
        recycler = root.findViewById(R.id.recyclerExplorar);
        sinResultadosFav = root.findViewById(R.id.sinResultadosFav);





        final DialogCargando cargando = new DialogCargando(getActivity());
        cargando.startCargando();

        listaAux = new ArrayList<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                Gson gson = new Gson();
                Datos d = new Datos(getContext());
                String json = null;
                try {
                    json = d.leerEncriptado("Usuario");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                actual = gson.fromJson(json, Usuario.class);
                for (DataSnapshot c : dataSnapshot.getChildren()) {
                    codigo = c.getValue(CodigoPublico.class);
                    if (actual.getFavoritos().contains(codigo.getID()))
                    {
                        listaAux.add(codigo);
                    }
                }
                recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                adapter = new AdapterDatosPublicos(listaAux, actual);
                recycler.setAdapter(adapter);
                cargando.stopCargando();

                adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        bundle = new Bundle();
                        bundle.putString("Id",listaAux.get(position).getID());
                        NavHostFragment.findNavController(Favoritos.this)
                                .navigate(R.id.action_nav_favoritos_to_codigoPublicoFragment,bundle);
                    }

                    @Override
                    public void onDeleteClick(final int position) {

                        final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                        mensaje.setMessage("¿De verdad quieres borrarlo?");
                        mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CodigoPublico c = listaAux.get(position);
                                listaAux.remove(position);
                                adapter.notifyItemRemoved(position);
                                reference.child(c.getID()).removeValue();
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
                    public void onCheckChange(int position, boolean isChecked) {
                        Boolean actualizar = true;
                        if(isChecked)
                        {
                           actualizar = actual.addFavorito(listaAux.get(position).getID());
                        }
                        else
                        {
                            actualizar = actual.removeFavorito(listaAux.get(position).getID());
                            listaAux.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        if(actualizar)
                        {
                            referenceUser = FirebaseDatabase.getInstance()
                                    .getReference("Usuarios")
                                    .child(Auth.getCurrentUser().getUid());
                            referenceUser.removeValue();
                            referenceUser.setValue(actual);
                            Datos d = new Datos(getContext());
                            Gson gson = new Gson();
                            String json = gson.toJson(actual);
                            try {
                                d.encriptar("Usuario",json);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onDownloadClick(int position) {
                        Datos datos = new Datos(getContext());
                        if(datos.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE,getContext())) {
                            File f1;
                            if(actual.getNombre().equals(listaAux.get(position).getAutor()))
                            {
                                f1 = Environment.getExternalStoragePublicDirectory("/vision/"+actual.getNombre()+"/Públicos"+"/"+listaAux.get(position).getTipo());
                            }
                            else
                            {
                                f1 = Environment.getExternalStoragePublicDirectory("/vision/"+"Públicos"+"/"+listaAux.get(position).getTipo());
                            }

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

                if (listaAux.isEmpty()) {
                    sinResultadosFav.setVisibility(View.VISIBLE);
                }
                //cargando.stopCargando();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        reference = FirebaseDatabase.getInstance().getReference("Publico");

    }
}

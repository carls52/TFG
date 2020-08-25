package com.example.pruebavision.online.ui.RepPublico;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class MisCPublicosFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FirebaseAuth auth;
    private DatabaseReference reference, referenceUser;
    private CodigoPublico codigo;
    private String ID;
    private RecyclerView recycler;
    private TextView sinResultados;
    private ArrayList<CodigoPublico> listaAux = new ArrayList<>();
    private Usuario actual;
    private AdapterDatosPublicos adapter;
    private Bundle bundle;
    private FirebaseAuth Auth;
    private SwipeRefreshLayout swipe;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_repositorio_publico, container, false);


        auth = FirebaseAuth.getInstance();
        ID = auth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("Publico");
        recycler = root.findViewById(R.id.recyclerPublico);
        sinResultados = root.findViewById(R.id.sinResultadosPublico);
        Auth = FirebaseAuth.getInstance();
        swipe = root.findViewById(R.id.swipePublico);
        try {
            actual = new Datos(getContext()).getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        final DialogCargando cargando = new DialogCargando(getActivity());
        cargando.startCargando();
        final Datos d = new Datos(getContext());

        if (listaAux.isEmpty()) {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    for (DataSnapshot c : dataSnapshot.getChildren()) {
                        codigo = c.getValue(CodigoPublico.class);
                        if (codigo.getIdAutor().equals(auth.getCurrentUser().getUid())
                                && codigo.getIdAutor() != null) {
                            listaAux.add(codigo);
                        }
                    }
                    if (listaAux.size() == 0) {
                        sinResultados.setVisibility(View.VISIBLE);
                        sinResultados.setText("Sin resultados");
                    }
                    recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    adapter = new AdapterDatosPublicos(listaAux, actual);
                    recycler.setAdapter(adapter);
                    cargando.stopCargando();
                    adapterInterface(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    AlertTxt alert = new AlertTxt();
                    cargando.stopCargando();
                    alert.msg("Ha ocurrido un error inesperado inténtelo más tarde", getContext());
                }
            });
        }
        else {
            adapter = new AdapterDatosPublicos(listaAux, actual);
            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            cargando.stopCargando();
            recycler.setAdapter(adapter);
            adapterInterface(adapter);
        }
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            listaAux.clear();
                            for (DataSnapshot c : dataSnapshot.getChildren()) {
                                codigo = c.getValue(CodigoPublico.class);
                                if (codigo.getIdAutor().equals(auth.getCurrentUser().getUid())
                                        && codigo.getIdAutor() != null) {
                                    listaAux.add(codigo);
                                }
                            }
                            if (listaAux.size() == 0) {
                                sinResultados.setVisibility(View.VISIBLE);
                                sinResultados.setText("Sin resultados");
                            }
                            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            adapter = new AdapterDatosPublicos(listaAux, actual);
                            recycler.setAdapter(adapter);
                            cargando.stopCargando();
                            adapterInterface(adapter);
                            swipe.setRefreshing(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            });
        }
        private void adapterInterface(final AdapterDatosPublicos adapter)
        {
            adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    bundle = new Bundle();
                    bundle.putString("Id", listaAux.get(position).getID());
                    NavHostFragment.findNavController(MisCPublicosFragment.this)
                            .navigate(R.id.action_nav_gallery_to_codigoPublicoFragment, bundle);
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
                    if (isChecked) {
                        actualizar = actual.addFavorito(listaAux.get(position).getID());
                    } else {
                        actualizar = actual.removeFavorito(listaAux.get(position).getID());
                        listaAux.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                    if (actualizar) {
                        referenceUser = FirebaseDatabase.getInstance()
                                .getReference("Usuarios")
                                .child(Auth.getCurrentUser().getUid());
                        referenceUser.removeValue();
                        referenceUser.setValue(actual);
                        Datos d = new Datos(getContext());
                        Gson gson = new Gson();
                        String json = gson.toJson(actual);
                        try {
                            d.encriptar("Usuario", json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onDownloadClick(int position) {
                    Datos d = new Datos(getContext());
                    if (d.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
                        File f1 = Environment.getExternalStoragePublicDirectory("/vision/" + actual.getNombre() + "/Públicos/" + listaAux.get(position).getTipo());
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

                        File dir = new File(f1, listaAux.get(position).getTitulo() + extension);
                        f1.mkdirs();
                        try {
                            FileWriter filewriter = new FileWriter(dir, false);
                            BufferedWriter out = new BufferedWriter(filewriter);
                            out.write(listaAux.get(position).getCodigo());
                            out.close();
                            AlertTxt alert = new AlertTxt();
                            alert.msg("Archivo guardado", getContext());
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getActivity(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            });
        }
    }


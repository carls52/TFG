package com.example.pruebavision.online.ui.Explorar;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ExplorarFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private RecyclerView recycler;
    private DatabaseReference reference,referenceUser;
    private CodigoPublico codigo;
    private ArrayList<CodigoPublico> listaAux;
    private Usuario actual;
    private Bundle bundle;
    private AdapterDatosPublicos adapter;
    private SwipeRefreshLayout swipe;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_explorar, container, false);
        final TextView sinResultados = root.findViewById(R.id.sinResultadosExplorar);
        recycler = root.findViewById(R.id.recyclerExplorar);
        swipe = root.findViewById(R.id.swipeExplorar);

        setHasOptionsMenu(true);
        final DialogCargando cargando = new DialogCargando(getActivity());


        reference = FirebaseDatabase.getInstance().getReference("Publico");
        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listaAux = new ArrayList<>();

        Datos d = new Datos(getContext());
        try {
            actual = d.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(listaAux.isEmpty()) {
            cargando.startCargando();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot c : dataSnapshot.getChildren()) {
                        codigo = c.getValue(CodigoPublico.class);
                        if (!codigo.getAutor().equals(actual.getNombre())) {
                            if (!listaAux.contains(codigo))
                                listaAux.add(codigo);
                        }
                    }
                    recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    adapter = new AdapterDatosPublicos(listaAux, actual);
                    recycler.setAdapter(adapter);
                    adapterInterface(adapter);
                    cargando.stopCargando();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    AlertTxt alert = new AlertTxt();
                    cargando.stopCargando();
                    alert.msg("Ha ocurrido un error inesperado inténtelo más tarde", getContext());
                }
            });
        }
        else
        {
            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            adapter = new AdapterDatosPublicos(listaAux, actual);
            recycler.setAdapter(adapter);
           // cargando.stopCargando();
            adapterInterface(adapter);

        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaAux.clear();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot c : dataSnapshot.getChildren()) {
                            codigo = c.getValue(CodigoPublico.class);
                            if (!codigo.getAutor().equals(actual.getNombre())) {
                                if (!listaAux.contains(codigo))
                                    listaAux.add(codigo);
                            }
                        }
                        recycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        adapter = new AdapterDatosPublicos(listaAux, actual);
                        recycler.setAdapter(adapter);
                        swipe.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        swipe.setRefreshing(false);
                        AlertTxt alert = new AlertTxt();
                        alert.msg("Ha ocurrido un error inesperado inténtelo más tarde", getContext());

                    }
                });
            }
        });

        return root;
    }

    private void adapterInterface(AdapterDatosPublicos adapter)
    {
        adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                bundle = new Bundle();
                bundle.putString("Id", listaAux.get(position).getID());
                NavHostFragment.findNavController(ExplorarFragment.this)
                        .navigate(R.id.action_nav_slideshow_to_codigoPublicoFragment, bundle);
            }

            @Override
            public void onDeleteClick(int position) {

            }

            @Override
            public void onCheckChange(final int position, final boolean isChecked) {
                Boolean actualizar = true;
                if (isChecked) {
                    actualizar = actual.addFavorito(listaAux.get(position).getID());
                } else {
                    actualizar = actual.removeFavorito(listaAux.get(position).getID());
                }
                if (actualizar) {
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
                Datos datos = new Datos(getContext());
                if (datos.checkPer(Manifest.permission.WRITE_EXTERNAL_STORAGE, getContext())) {
                    File f1;
                    if (actual.getNombre().equals(listaAux.get(position).getAutor())) {
                        f1 = Environment.getExternalStoragePublicDirectory("/vision/" + actual.getNombre() + "/Públicos" + "/" + listaAux.get(position).getTipo());
                    } else {
                        f1 = Environment.getExternalStoragePublicDirectory("/vision/" + "Públicos" + "/" + listaAux.get(position).getTipo());
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

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        item.setVisible(true);

        SearchView search = (SearchView) item.getActionView();

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    NavHostFragment.findNavController(ExplorarFragment.this)
                            .navigate(R.id.action_nav_explorar_to_filtros);
                }
                else
                    recycler.setVisibility(View.VISIBLE);

            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recycler.setVisibility(View.VISIBLE);
                ArrayList<CodigoPublico> nuevaLista = new ArrayList<>();
                for(CodigoPublico c : listaAux)
                {
                    if(c.getTitulo().contains(query.toLowerCase()) || c.getTitulo().contains(query.toUpperCase()))
                    {
                        nuevaLista.add(c);
                    }
                }
                adapter = new AdapterDatosPublicos(nuevaLista,actual);
                recycler.setAdapter(adapter);
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recycler.setVisibility(View.GONE);
                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter = new AdapterDatosPublicos(listaAux,actual);

                recycler.setAdapter(adapter);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

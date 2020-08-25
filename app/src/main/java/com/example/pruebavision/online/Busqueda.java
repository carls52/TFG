package com.example.pruebavision.online;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Busqueda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Busqueda extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SearchView search;
    private RecyclerView recycler;
    private AdapterDatosPublicos adapter;
    private SwipeRefreshLayout swipe;
    private TextView sinResultados;
    private DatabaseReference reference,referenceUser;
    private Usuario actual;
    private ArrayList<CodigoPublico> listaAux = new ArrayList<>();
    private boolean java,c,cplus,javascript,python,otros;
    private String query;
    private DialogCargando cargando;
    private Bundle bundle;


    public Busqueda() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment busqueda.
     */
    // TODO: Rename and change types and number of parameters
    public static Busqueda newInstance(String param1, String param2) {
        Busqueda fragment = new Busqueda();
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
            java = getArguments().getBoolean("java");
            javascript = getArguments().getBoolean("javascript");
            c = getArguments().getBoolean("c");
            cplus = getArguments().getBoolean("cplus");
            otros = getArguments().getBoolean("otros");
            python = getArguments().getBoolean("python");
            query = getArguments().getString("texto");
            cargando = new DialogCargando(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_busqueda, container, false);
        setHasOptionsMenu(true);
        Datos datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recycler = root.findViewById(R.id.recyclerBusqueda);
        sinResultados = root.findViewById(R.id.sinResultadosBusqueda);
        reference = FirebaseDatabase.getInstance().getReference("Publico");
        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cargando.startCargando();
                CodigoPublico codigo;
                for(DataSnapshot s : dataSnapshot.getChildren())
                {
                    codigo = s.getValue(CodigoPublico.class);
                    if (!codigo.getAutor().equals(actual.getNombre()) &&
                            (codigo.getTitulo().contains(query.toUpperCase()) ||
                             codigo.getTitulo().contains(query.toLowerCase())))
                    {
                        if (!listaAux.contains(codigo)) {
                            if (java && codigo.getTipo().equals("JAVA"))
                                listaAux.add(codigo);
                            if (c && codigo.getTipo().equals("C"))
                                listaAux.add(codigo);
                            if (cplus && codigo.getTipo().equals("C++"))
                                listaAux.add(codigo);
                            if (python && codigo.getTipo().equals("PYTHON"))
                                listaAux.add(codigo);
                            if (javascript && codigo.getTipo().equals("JAVASCRIPT"))
                                listaAux.add(codigo);
                            if (javascript && codigo.getTipo().equals("OTROS"))
                                listaAux.add(codigo);
                        }
                    }
                }
                recycler.setLayoutManager(new GridLayoutManager(getContext(),1));
                adapter = new AdapterDatosPublicos(listaAux,actual);
                recycler.setAdapter(adapter);
                adapterInterface(adapter);
                cargando.stopCargando();
                if(listaAux.size()==0)
                {
                    sinResultados.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return root;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        item.setVisible(true);
        search = (SearchView) item.getActionView();
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cargando.startCargando();
                        CodigoPublico codigo;
                        for(DataSnapshot s : dataSnapshot.getChildren())
                        {
                            codigo = s.getValue(CodigoPublico.class);
                            if (!codigo.getAutor().equals(actual.getNombre()) &&
                                    (codigo.getTitulo().contains(query.toUpperCase()) ||
                                            codigo.getTitulo().contains(query.toLowerCase())))
                            {
                                if (!listaAux.contains(codigo)) {
                                    if (java && codigo.getTipo().equals("JAVA"))
                                        listaAux.add(codigo);
                                    if (c && codigo.getTipo().equals("C"))
                                        listaAux.add(codigo);
                                    if (cplus && codigo.getTipo().equals("C++"))
                                        listaAux.add(codigo);
                                    if (python && codigo.getTipo().equals("PYTHON"))
                                        listaAux.add(codigo);
                                    if (javascript && codigo.getTipo().equals("JAVASCRIPT"))
                                        listaAux.add(codigo);
                                    if (javascript && codigo.getTipo().equals("OTROS"))
                                        listaAux.add(codigo);
                                }
                            }
                        }
                        recycler.setLayoutManager(new GridLayoutManager(getContext(),1));
                        adapter = new AdapterDatosPublicos(listaAux,actual);
                        recycler.setAdapter(adapter);
                        adapterInterface(adapter);
                        cargando.stopCargando();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                NavHostFragment.findNavController(Busqueda.this)
                        .navigate(R.id.action_busqueda_to_nav_explorar);

                return true;
            }
        });

        search.setFocusable(true);
        search.setIconified(false);
        search.requestFocus();

    }
    private void adapterInterface(final AdapterDatosPublicos adapter)
    {
        adapter.setOnItemClickListerner(new AdapterDatosPublicos.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                bundle = new Bundle();
                bundle.putString("Id", listaAux.get(position).getID());
                NavHostFragment.findNavController(Busqueda.this)
                        .navigate(R.id.action_busqueda_to_codigoPublicoFragment, bundle);
            }

            @Override
            public void onDeleteClick(final int position) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                mensaje.setMessage("¿De verdad quieres borrarlo?");
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CodigoPublico aux = listaAux.get(position);
                        reference.child(aux.getID()).removeValue();
                        listaAux.remove(position);
                        adapter.notifyItemRemoved(position);
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
                    try {
                        d.setUsuario(actual);
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
}

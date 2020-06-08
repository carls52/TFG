package com.example.pruebavision;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Switch;

import com.example.pruebavision.ui.Explorar.ExplorarFragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Filtros#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Filtros extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SearchView search;
    private Switch java,c,cplus,javascript,python,otros;

    public Filtros() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment filtros.
     */
    // TODO: Rename and change types and number of parameters
    public static Filtros newInstance(String param1, String param2) {
        Filtros fragment = new Filtros();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_filtros, container, false);
        setHasOptionsMenu(true);
        java = root.findViewById(R.id.switchJava);
        javascript = root.findViewById(R.id.switchJavaScript);
        c = root.findViewById(R.id.switchC);
        cplus = root.findViewById(R.id.switchCplus);
        python = root.findViewById(R.id.switchPython);
        otros = root.findViewById(R.id.switchOtros);


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
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("java",java.isChecked());
                bundle.putBoolean("javascript",javascript.isChecked());
                bundle.putBoolean("c",c.isChecked());
                bundle.putBoolean("cplus",cplus.isChecked());
                bundle.putBoolean("python",python.isChecked());
                bundle.putBoolean("otros",otros.isChecked());
                bundle.putString("texto",query);
                NavHostFragment.findNavController(Filtros.this)
                        .navigate(R.id.action_filtros_to_busqueda,bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                NavHostFragment.findNavController(Filtros.this)
                        .navigate(R.id.action_filtros_to_nav_explorar);
                return true;
            }
        });

        search.setFocusable(true);
        search.setIconified(false);
        search.requestFocus();

    }
}

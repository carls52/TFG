package com.example.pruebavision.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pruebavision.Datos;
import com.example.pruebavision.Login;
import com.example.pruebavision.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CerrarSesion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CerrarSesion extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences sharedPreferences;

    public CerrarSesion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cerrar_sesion.
     */
    // TODO: Rename and change types and number of parameters
    public static CerrarSesion newInstance(String param1, String param2) {
        CerrarSesion fragment = new CerrarSesion();
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
        Datos datos = new Datos(getContext());
        try {
            datos.encriptar("Correo","");
            datos.encriptar("Contraseña","");
            datos.encriptar("Usuario","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getContext(), Login.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }
}

package com.example.pruebavision;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pruebavision.online.Login;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Cambios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Cambios extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String tipo;
    private Button guardar;
    private TextView nuevo,antiguo;
    private EditText nuevoTexto,antiguoTexto;
    private FirebaseAuth auth;

    public Cambios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cambios.
     */
    // TODO: Rename and change types and number of parameters
    public static Cambios newInstance(String param1, String param2) {
        Cambios fragment = new Cambios();
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
            tipo = getArguments().getString("tipo");
            auth = FirebaseAuth.getInstance();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cambios, container, false);
        antiguo  = root.findViewById(R.id.old);
        antiguoTexto  = root.findViewById(R.id.oldText);
        nuevo = root.findViewById(R.id.newTitulo);
        nuevoTexto = root.findViewById(R.id.newText);
        guardar = root.findViewById(R.id.guardarCambios);

        switch (tipo){
            case "contraseña":
                antiguo.setText("Contraseña antigua");
                nuevo.setText("Contraseña nueva");
                break;
            case "correo":
                antiguo.setText("Correo actual");
                nuevo.setText("Correo nuevo");
        }
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertTxt alert = new AlertTxt();
                //comprobamos campos vacíos

                if(antiguoTexto.getText().toString().length() == 0 || nuevoTexto.getText().toString().length() == 0)
                {

                    alert.msg("Por favor rellene todos los campos",getContext());
                }
                else {
                    String guardado = "";
                    Datos datos = new Datos(getContext());
                    switch (tipo) {
                        case "correo":
                            //comprobamos que el valor antiguo coincide con el valor guardado
                            if (auth.getCurrentUser().getEmail().contentEquals(antiguoTexto.getText())) {

                                //comprobamos si el nuevo correo es un correo válido
                                if(android.util.Patterns.EMAIL_ADDRESS
                                        .matcher(nuevo.getText().toString().trim()).matches()) {
                                    auth.getCurrentUser().updateEmail(nuevoTexto.getText().toString().trim());
                                    try {
                                        datos.encriptar("correo","");
                                        datos.encriptar("contraseña","");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(getContext(), Login.class);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                                else
                                {
                                    alert.msg("El correo nuevo no es válido",getContext());
                                }
                            }
                            else
                            {
                                alert.msg("El correo no coincide con el de su cuenta",getContext());
                            }

                            break;
                        case "contraseña":
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(auth.getCurrentUser().getEmail(),
                                            antiguoTexto.getText().toString().trim());
                            if(auth.getCurrentUser().reauthenticate(credential).isSuccessful())
                            {
                                if(nuevoTexto.getText().toString().length()>6)
                                {
                                    auth.getCurrentUser().updatePassword(nuevoTexto.getText().toString().trim());
                                    try {
                                        datos.encriptar("correo","");
                                        datos.encriptar("contraseña","");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(getContext(),Login.class);
                                    startActivity(i);
                                    getActivity().finish();

                                }
                                else
                                {
                                    alert.msg("La contraseña debe tener al menos 6 caracteres"
                                            ,getContext());
                                }
                            }
                            else
                            {
                                alert.msg("Contraseña actual incorrecta",getContext());
                            }
                            break;
                    }
                }
            }
        });
        return root; }
}

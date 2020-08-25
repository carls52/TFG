package com.example.pruebavision.online.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.pruebavision.CodigoPublico;
import com.example.pruebavision.Datos;
import com.example.pruebavision.online.Login;
import com.example.pruebavision.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Ajustes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ajustes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button cambiarPass,cambiarMail,borrarCuenta;
    private DatabaseReference reference,referenceUser;
    private CheckBox sangradoInteligente;


    public Ajustes() {
        // Required empty public constructor
    }

    public static Ajustes newInstance(String param1, String param2) {
        Ajustes fragment = new Ajustes();
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
        final View root = inflater.inflate(R.layout.fragment_ajustes, container, false);
        sangradoInteligente = root.findViewById(R.id.sangradoInteligente);

        cambiarPass = root.findViewById(R.id.cambiarPass);
        cambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tipo","contraseña");
                NavHostFragment.findNavController(Ajustes.this)
                        .navigate(R.id.action_ajustes2_to_cambios,bundle);
            }
        });
        cambiarMail = root.findViewById(R.id.cambiarMail);
        cambiarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tipo","correo");
                NavHostFragment.findNavController(Ajustes.this)
                        .navigate(R.id.action_ajustes2_to_cambios,bundle);
            }
        });
        borrarCuenta = root.findViewById(R.id.borrarCuenta);
        borrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final androidx.appcompat.app.AlertDialog.Builder mensaje = new AlertDialog.Builder(getContext());
                mensaje.setMessage("¿Seguro que quiere borrar su cuenta? Todos sus códigos guardados serán eliminados");
                mensaje.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        referenceUser = FirebaseDatabase.getInstance().getReference("Usuarios")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        referenceUser.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Publico");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren())
                                {
                                    CodigoPublico codigo = d.getValue(CodigoPublico.class);
                                    if(codigo.getIdAutor()
                                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        reference.child(codigo.getID()).removeValue();
                                    }
                                }
                                Intent i = new Intent(getContext(), Login.class);
                                getActivity().finish();
                                startActivity(i);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
        });
        final Datos datos = new Datos(getContext());
        String sangrado="";
        try {
            sangrado = datos.getAjustes("Sangrado");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!sangrado.equals("SI"))
        {
            sangradoInteligente.setChecked(false);
            if(sangrado.equals(""))
                datos.setAjustes("Sangrado","NO");
        }
        else
        {
            sangradoInteligente.setChecked(true);
        }
        sangradoInteligente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    try {
                        datos.setAjustes("Sangrado","SI");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        datos.setAjustes("Sangrado","NO");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return root;
    }
}

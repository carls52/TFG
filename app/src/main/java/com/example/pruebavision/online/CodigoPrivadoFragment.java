package com.example.pruebavision.online;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pruebavision.AlertTxt;
import com.example.pruebavision.CodigoPrivado;
import com.example.pruebavision.CodigoPublico;
import com.example.pruebavision.Comentario;
import com.example.pruebavision.Datos;
import com.example.pruebavision.R;
import com.example.pruebavision.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodigoPrivadoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodigoPrivadoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView titulo,descripcion,codigo;
    private String Id,tipoLista;
    private Spinner tipo,visible;
    private Button guardar;
    private Usuario actual;
    private CodigoPrivado codigoP;

    public CodigoPrivadoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepositorioPrivado.
     */
    // TODO: Rename and change types and number of parameters
    public static CodigoPrivadoFragment newInstance(String param1, String param2) {
        CodigoPrivadoFragment fragment = new CodigoPrivadoFragment();
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
            tipoLista = getArguments().getString("Tipo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repositorio_privado, container, false);
        // Inflate the layout for this fragment
       codigo = root.findViewById(R.id.codigoPrivado);
       titulo = root.findViewById(R.id.tituloPrivado);
       descripcion = root.findViewById(R.id.descPrivado);
       guardar = root.findViewById(R.id.guardarPrivado);
       tipo = root.findViewById(R.id.tipoPrivado);
       visible = root.findViewById(R.id.visiblePrivado);

       Datos datos = new Datos(getContext());
        try {
            actual = datos.getUsuario();
        } catch (Exception e) {
            e.printStackTrace();
        }
        codigoP = actual.getCodigoTipo(tipoLista).get(Integer.parseInt(Id));
       codigo.setText(codigoP.getCodigo());
       titulo.setText(codigoP.getTitulo());

        //inicializamos los valores de los spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.tipos,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);
        tipo.setSelection(adapter.getPosition(codigoP.getTipo()));


        visible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(visible.getSelectedItem().toString().equals("PRIVADO"))
                {
                    descripcion.setVisibility(View.GONE);
                }
                else
                {
                    descripcion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.visibilidad,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visible.setAdapter(adapter2);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HayCambios(codigoP))
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    FirebaseAuth Auth = FirebaseAuth.getInstance();
                    if(visible.getSelectedItem().toString().equals("PRIVADO"))
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(Auth.getCurrentUser().getUid());

                        CodigoPrivado nuevo = new CodigoPrivado(codigo.getText().toString().trim(),
                                titulo.getText().toString().trim(),
                                codigoP.getAutor(),
                                formatter.format(date),
                                tipo.getSelectedItem().toString());
                        actual.updateCodigoPrivado(nuevo,Integer.parseInt(Id));


                        reference.removeValue();
                        reference.setValue(actual);
                    }
                    else
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Publico");
                        DatabaseReference newPostRef = reference.push();
                        ArrayList<Comentario> lista = new ArrayList<>();
                        Comentario c = new Comentario("x","x","x");
                        lista.add(c);

                        CodigoPublico nuevo = new CodigoPublico(newPostRef.getKey(),
                                titulo.getText().toString().trim(),
                                codigo.getText().toString().trim(),
                                descripcion.getText().toString().trim(),
                                actual.getNombre(),
                                formatter.format(date),
                                0,
                                0,
                                lista,
                                tipo.getSelectedItem().toString(),
                                Auth.getCurrentUser().getUid());
                        newPostRef.setValue(nuevo);
                        actual.removeCodigoPrivado(Integer.parseInt(Id));
                    }
                    Datos d = new Datos(getContext());
                    try {
                        d.setUsuario(actual);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AlertTxt alert = new AlertTxt();
                alert.msg("Cambios guardados",getContext());
            }
        });

        return root;
    }
    //true si hay cambios
    public boolean HayCambios(CodigoPrivado c)
    {
        return !(c.getTitulo().equals(titulo.toString().trim())
                && c.getCodigo().equals(codigo.toString().trim())
                && c.getTipo().equals(tipo.getSelectedItem().toString()));
    }
}

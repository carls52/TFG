package com.example.pruebavision.online.ui.RepPrivado;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pruebavision.online.CloudVision;
import com.example.pruebavision.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Bundle bundle;
    private FloatingActionButton fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ActionBar actionbar;

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_online, container, false);


        root.findViewById(R.id.foto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Intent i = new Intent(getContext(), MainActivity.class);
            //    startActivity(i);
            //    NavHostFragment.findNavController(HomeFragment.this)
            //            .navigate(R.id.action_nav_home_to_mainActivity);
               Intent i = new Intent(getContext(), CloudVision.class);
               startActivity(i);
            }
        });
        bundle = new Bundle();
        root.findViewById(R.id.buttonJava).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","JAVA");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        root.findViewById(R.id.buttonPython).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","PYTHON");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        root.findViewById(R.id.buttonC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","C");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        root.findViewById(R.id.buttonCplus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","C++");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        root.findViewById(R.id.buttonJavaScript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","JAVASCRIPT");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });
        root.findViewById(R.id.buttonOtros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("tipo","OTRO");
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_nav_home_to_first2Fragment,bundle);
            }
        });

        return root;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}

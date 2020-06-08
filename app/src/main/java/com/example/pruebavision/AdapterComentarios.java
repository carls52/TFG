package com.example.pruebavision;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.ViewHolderDatos> {
    private ArrayList<Comentario> lista;
    public AdapterComentarios(ArrayList<Comentario> lista){this.lista = lista;}
    @NonNull
    @Override
    public AdapterComentarios.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario,null,false);
        return new AdapterComentarios.ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView comentario,autor;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            comentario = itemView.findViewById(R.id.comentario);
            autor = itemView.findViewById(R.id.autorComentario);
        }
        public void asignarDatos(Comentario s){
            comentario.setText(s.getComentario());
            autor.setText(s.getAutor());
        }
    }
}

package com.example.pruebavision;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterDatosPublicos extends RecyclerView.Adapter<AdapterDatosPublicos.ViewHolderDatos>
 {
    private ArrayList<CodigoPublico> lista;
    private OnItemClickListener mListener;
    private Usuario usuario;

    public void setOnItemClickListerner(OnItemClickListener listener){
        this.mListener = listener;
    }


    public AdapterDatosPublicos(ArrayList<CodigoPublico> lista, Usuario usuario) {
        this.lista = lista;
        this.usuario = usuario;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_publico,null,false);
        return new ViewHolderDatos(view,mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(lista.get(position));
        if(usuario.getFavoritos()!=null) {
            if (usuario.getFavoritos().contains(lista.get(position).getID())) {
                holder.cambiarEstrella();
            }
        }
        if (!lista.get(position).getAutor().equals(usuario.getNombre())) {
            holder.esconderBorrar();
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
     public interface OnItemClickListener {
         void onItemClick(int position);
         void onDeleteClick (int position);
         void onCheckChange(int position, boolean isChecked);
         void onDownloadClick(int position);
     }

    public static class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tituloLista,descripcion,fecha,tipo,autor;
        ImageView borrar,descargar;
        ToggleButton fav;
        public ViewHolderDatos(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tituloLista = itemView.findViewById(R.id.tituloListaPublica);
            descripcion = itemView.findViewById(R.id.descripcionPublica);
            fecha = itemView.findViewById(R.id.fechaPublica);
            tipo = itemView.findViewById(R.id.tipoListaPublica);
            borrar = itemView.findViewById(R.id.deletePublico);
            fav = itemView.findViewById(R.id.fab);
            autor = itemView.findViewById(R.id.autorPublico);
            descargar = itemView.findViewById(R.id.descargarPublico);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onCheckChange(position,isChecked);
                        }
                    }
                }
            });
            descargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDownloadClick(position);
                        }
                    }
                }
            });

        }

        public void asignarDatos(CodigoPublico s) {
            tituloLista.setText(s.getTitulo());
            descripcion.setText(s.getDescripcion());
            fecha.setText(s.getFecha().substring(0,11));
            tipo.setText(s.getTipo());
            autor.setText(s.getAutor());

        }
        public void esconderEstrella()
        {
            fav.setVisibility(View.GONE);
        }
        public void cambiarEstrella()
        {
            fav.setChecked(true);
        }
        public void esconderBorrar()
        {
            borrar.setVisibility(View.INVISIBLE);
        }
    }
}

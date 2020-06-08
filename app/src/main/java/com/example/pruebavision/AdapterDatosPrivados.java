package com.example.pruebavision;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AdapterDatosPrivados extends RecyclerView.Adapter<AdapterDatosPrivados.ViewHolderDatos> {

    private ArrayList<CodigoPrivado> lista = new ArrayList<>();
    private OnItemClickListener mListener;
    private Usuario usuario;

    public void setOnItemClickListerner(OnItemClickListener listener){
        this.mListener = listener;
    }

    public AdapterDatosPrivados(ArrayList<CodigoPrivado> lista) {
        this.lista = lista;

    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        return new ViewHolderDatos(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(lista.get(position));

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick (int position);
        void onDownloadClick(int position) throws FileNotFoundException;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView tituloLista,descripcion,fecha;
        ImageView borrar,descarga;
        public ViewHolderDatos(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tituloLista = itemView.findViewById(R.id.tituloLista);
            descripcion = itemView.findViewById(R.id.descripcion);
            fecha = itemView.findViewById(R.id.fecha);
            borrar = itemView.findViewById(R.id.deletePrivado);
            descarga = itemView.findViewById(R.id.descargarPrivado);

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
            descarga.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            try {
                                listener.onDownloadClick(position);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        public void asignarDatos(CodigoPrivado s) {
            tituloLista.setText(s.getTitulo());
            fecha.setText(s.getFechaCreacion().substring(0,11));
        }
    }
}

package com.example.pruebavision;

import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String id;
    private ArrayList<CodigoPrivado> codigosPrivados;
    private ArrayList<String> favoritos;

    public Usuario() {
    }

    public Usuario(String nombre, String id, ArrayList<CodigoPrivado> codigosPrivados, ArrayList<String> favoritos) {
        this.nombre = nombre;
        this.id = id;
        this.codigosPrivados = codigosPrivados;
        this.favoritos = favoritos;

    }



    public ArrayList<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(ArrayList<String> favoritos) {
        this.favoritos = favoritos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CodigoPrivado> getCodigosPrivados() {
        return codigosPrivados;
    }

    public void setCodigosPrivados(ArrayList<CodigoPrivado> codigosPrivados) {
        this.codigosPrivados = codigosPrivados;
    }
    public void addCodigoPrivado(CodigoPrivado codigo)
    {
        if(this.getCodigosPrivados()==null)
        {
            this.codigosPrivados = new ArrayList<>();
            codigosPrivados.add(codigo);
        }
        else if(this.getCodigosPrivados().isEmpty())
        {
            codigosPrivados.add(codigo);
        }
        else if(this.codigosPrivados.get(0).getAutor().equals("x") && this.codigosPrivados.get(0).getTitulo().equals("x"))
        {
            codigosPrivados.clear();
            codigosPrivados.add(codigo);
        }
        else
            codigosPrivados.add(codigo);
    }
    //true si hace algun cambio false si no cambia nada
    public boolean addFavorito(String Id)
    {
        if(this.favoritos==null)
        {
            this.favoritos = new ArrayList<>();
            this.favoritos.add(Id);
            return true;
        }
        else if(this.favoritos.isEmpty())
        {
            this.favoritos.add(Id);
            return true;
        }
        else if(!this.favoritos.contains(Id)) {
            if (this.favoritos.get(0).equals("x")) {
                this.favoritos.clear();
                this.favoritos.add(Id);
                return true;
            } else
                this.favoritos.add(Id);
            return true;
        }
        else
            return false;
    }
    //True si borra el favorito false si no lo hace
    public boolean removeFavorito(String Id)
    {
        if(this.favoritos.contains(Id))
        {
            favoritos.remove(Id);
            return true;
        }
        return false;
    }
    public void updateCodigoPrivado(CodigoPrivado c, int position)
    {
            codigosPrivados.set(position,c);
    }
    public void removeCodigoPrivado(int position)
    {
        codigosPrivados.remove(position);
    }

    public ArrayList<CodigoPrivado> getCodigoTipo(String tipo)
    {
        ArrayList<CodigoPrivado> result = new ArrayList<>();
        for(CodigoPrivado c : this.codigosPrivados)
        {
            if(c.getTipo().equals(tipo))
            {
                result.add(c);
            }
        }
        return result;
    }

}

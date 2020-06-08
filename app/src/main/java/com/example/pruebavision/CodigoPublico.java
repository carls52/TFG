package com.example.pruebavision;

import java.util.ArrayList;

public class CodigoPublico {
    private String ID;
    private String titulo;
    private String codigo;
    private String descripcion;
    private String autor;
    private String fecha;
    private int descargas;
    private int visualizaciones;
    private ArrayList<Comentario> comentarios;
    private String tipo;
    private String IdAutor;

    public CodigoPublico(String ID, String titulo, String codigo, String descripcion, String autor,
                         String fecha, int descargas, int visualizaciones, ArrayList<Comentario> comentarios,
                         String tipo,String IdAutor) {
        this.ID = ID;
        this.titulo = titulo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.fecha = fecha;
        this.descargas = descargas;
        this.visualizaciones = visualizaciones;
        this.comentarios = comentarios;
        this.tipo = tipo;
        this.IdAutor = IdAutor;
    }

    public CodigoPublico() {
    }

    public String getIdAutor() {
        return IdAutor;
    }

    public void setIdAutor(String idAutor) {
        IdAutor = idAutor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public int getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(int visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public ArrayList<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
    public void addComentario(Comentario comentario)
    {
        if(comentarios.get(0).getAutor().equals("x")
                && comentarios.get(0).getAutorId().equals("x")
                && comentarios.get(0).getComentario().equals("x"))
        {
            comentarios.clear();
            comentarios.add(comentario);
        }
        else
            comentarios.add(comentario);
    }
    public int addVisualizacion()
    {
        return this.visualizaciones++;
    }
}

package com.example.pruebavision;

public class Comentario {
    private String Autor;
    private String AutorId;
    private String comentario;

    public Comentario(String autor, String autorId, String comentario) {
        Autor = autor;
        AutorId = autorId;
        this.comentario = comentario;
    }

    public Comentario() { }

    public String getAutor() {
        return Autor;
    }

    public void setAutor(String autor) {
        Autor = autor;
    }

    public String getAutorId() {
        return AutorId;
    }

    public void setAutorId(String autorId) {
        AutorId = autorId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}

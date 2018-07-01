package taller2.ataller2.model;

public class Comentario {
    private String nombre = "";
    private String comentario = "";

    public Comentario(){

    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getComentario() {
        return comentario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

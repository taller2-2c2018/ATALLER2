package taller2.ataller2.model;
import android.graphics.Bitmap;

public class Perfil {

    private String mNombre = "";
    private String mDescription = "";
    private Bitmap mPicture;

    public Perfil(String nombre) {
        mNombre = nombre;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String nombre) {
        this.mNombre = nombre;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
    }
}
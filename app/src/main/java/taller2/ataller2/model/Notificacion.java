package taller2.ataller2.model;

import android.graphics.Bitmap;

public class Notificacion {

    private String mTitulo = "";
    private String mDescription = "";
    private Bitmap mPicture;

    public Notificacion(String titulo) {
        mTitulo = titulo;
    }

    public String getmTitulo() {
        return mTitulo;
    }

    public void setTitulo(String titulo) {
        this.mTitulo = titulo;
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
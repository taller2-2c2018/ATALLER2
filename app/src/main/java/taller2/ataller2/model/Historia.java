package taller2.ataller2.model;

import android.graphics.Bitmap;

import java.util.List;

public class Historia {

    private String id = "";
    private String mTitulo = "";
    private String mDescription = "";
    private String mFecha = "";

    private String mLatitud = "";
    private String mLongitud = "";
    private String mUbicacion = "";

    private String mUserID = "";
    private String nombre = "";

    private Bitmap mPicture;
    private Bitmap mPictureUser;

    private List<Comentario> mComentarios;

    public Historia(String titulo) {
        mTitulo = titulo;
    }

    public String getmTitulo() {
        return mTitulo;
    }

    public void setTitulo(String titulo) {
        this.mTitulo = titulo;
    }

    public void setPictureUsr(Bitmap picture){ this.mPictureUser = picture;}

    public Bitmap getPictureUsr() {return this.mPictureUser;}

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getFecha() {
        return mFecha;
    }

    public void setFecha(String fecha) {
        this.mFecha = fecha;
    }

    public String getLatitud() {
        return mLatitud;
    }

    public void setLatitud(String latitud) {
        this.mLatitud = latitud;
    }

    public String getLongitud() {
        return mLongitud;
    }

    public void setLongitud(String longitud) { this.mLongitud = longitud; }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
    }

    public String getID() {return id;}

    public void setID(String mID) {id = mID;}

    public String getUserID() {return mUserID;}

    public void setUserID(String mID) {mUserID = mID;}

    public String getUbicacion() {return mUbicacion;}

    public void setUbicacion(String ubicacion) {mUbicacion = ubicacion;}

    public void setComentarios (List<Comentario> comentarios) {mComentarios = comentarios;}

    public void setNombre (String nom) {nombre = nom;}

    public String getNombre () {return nombre;}

    public List <Comentario> getComentarios () {return mComentarios;}

}
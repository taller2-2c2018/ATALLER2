package taller2.ataller2.model;

import android.graphics.Bitmap;
public class Historia {

    private String id = "";
    private String mTitulo = "";
    private String mDescription = "";
    private String mFecha = "";
    private String mUbicacion = "";
    private String mUserID = "";

    private Bitmap mPicture;
    private Bitmap mPictureUser;

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

    public String getUbicacion() {
        return mUbicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.mUbicacion = ubicacion;
    }

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



}
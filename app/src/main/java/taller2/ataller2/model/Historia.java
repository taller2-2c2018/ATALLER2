package taller2.ataller2.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;

import taller2.ataller2.services.EmotionType;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

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

    private String mStringUri;

    private Uri mVideo;

    private List<Comentario> mComentarios;

    private List<Reaccion> mReacciones;

    private int cantMeGusta = 0;
    private int cantNoMeGusta = 0;
    private int cantMeAburre = 0;
    private int cantMeDivierte = 0;
    private Reaccion miReaccion = null;

    private Uri mUri;

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

    public void setReacciones (List<Reaccion> reacciones) {
        mReacciones = reacciones;
        for (Reaccion reaccion : reacciones){
            switch (reaccion.getEmocion().getValue()){
                case 0:
                    cantMeGusta += 1;
                    break;
                case 1:
                    cantNoMeGusta += 1;
                    break;
                case 2:
                    cantMeDivierte += 1;
                    break;
                case 3:
                    cantMeAburre += 1;
                    break;
                default:
                    break;
            }
            if (reaccion.getAutor().equals(ServiceLocator.get(FacebookService.class).getFacebookID())){
                miReaccion = reaccion;
            }
        }
    }

    public void setMiReaccion(EmotionType emocion){
        if (miReaccion != null && miReaccion.getEmocion().getValue() == emocion.getValue() ){
            miReaccion = null;
            switch (emocion.getValue()){
                case 0:
                    cantMeGusta -= 1;
                    break;
                case 1:
                    cantNoMeGusta -= 1;
                    break;
                case 2:
                    cantMeDivierte -= 1;
                    break;
                case 3:
                    cantMeAburre -= 1;
                    break;
                default:
                    break;
            }
        } else {
            if (miReaccion != null){
                switch (miReaccion.getEmocion().getValue()){
                    case 0:
                        cantMeGusta -= 1;
                        break;
                    case 1:
                        cantNoMeGusta -= 1;
                        break;
                    case 2:
                        cantMeDivierte -= 1;
                        break;
                    case 3:
                        cantMeAburre -= 1;
                        break;
                    default:
                        break;
                }
            }
            switch (emocion.getValue()){
                case 0:
                    cantMeGusta += 1;
                    break;
                case 1:
                    cantNoMeGusta += 1;
                    break;
                case 2:
                    cantMeDivierte += 1;
                    break;
                case 3:
                    cantMeAburre += 1;
                    break;
                default:
                    break;
            }
            miReaccion = new Reaccion(emocion, ServiceLocator.get(FacebookService.class).getFacebookID());
        }
    }

    public void setNombre (String nom) {nombre = nom;}

    public String getNombre () {return nombre;}

    public Uri getVideo(){return mVideo; }

    public void setVideo(Uri uri) {mVideo = uri;}

    public List <Comentario> getComentarios () {return mComentarios;}

    public List <Reaccion> getReacciones () {return mReacciones;}

    public Reaccion getMiReaccion() {
        return miReaccion;
    }

    public int getCantMeAburre() {
        return cantMeAburre;
    }

    public int getCantMeGusta() {
        return cantMeGusta;
    }

    public int getCantNoMeGusta() {
        return cantNoMeGusta;
    }

    public int getCantMeDivierte() {
        return cantMeDivierte;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setStringUri(String mStringUri) {
        this.mStringUri = mStringUri;
    }

    public String getStringUri() {
        return mStringUri;
    }
}
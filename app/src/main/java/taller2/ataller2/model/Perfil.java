package taller2.ataller2.model;
import android.graphics.Bitmap;

import java.util.List;

public class Perfil {

    private String id = "";
    private String mNombre = "";
    private String mDescription = "";
    private String mPicture;
    private String mFechaNacimiento = "";
    private String mMail = "";
    private String mSexo = "";
    private List<String> mAmigos = null;

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

    public String getPicture() {
        return mPicture;
    }

    public void setPicture(String picture) {
        this.mPicture = picture;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.mFechaNacimiento = fechaNacimiento;
    }

    public String getMail() {
        return mMail;
    }

    public void setMail(String mail) {
        this.mMail = mail;
    }

    public String getSexo() {
        return mSexo;
    }

    public void setSexo(String sexo) {
        this.mSexo = sexo;
    }

    public String getFechaNacimiento() {
        return mFechaNacimiento;
    }

    public List<String> getAmigos() {return mAmigos;}

    public void setAmigos(List<String> amigos) {mAmigos = amigos;}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
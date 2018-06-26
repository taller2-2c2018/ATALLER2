package taller2.ataller2.model;
import android.graphics.Bitmap;

import java.util.List;

public class Perfil {

    private String mNombre = "";
    private String mDescription = "";
    private Bitmap mPicture;
    private String mFechaNacimiento = "";
    private String mMail = "";
    private String mSexo = "";
    private List<List<String>> mAmigos = null;

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

    public List<List<String>> getAmigos() {return mAmigos;}

    public void setAmigos(List<List<String>> amigos) {mAmigos = amigos;}

}
package taller2.ataller2.model;

import java.util.ArrayList;
import java.util.List;

public class Conversacion {

    private String idDest;
    private String idMia;

    private String mNombreConver = "";
    private String mDescription = "";
    private int mCantMsjSinLeer = 0;
    private List<Mensaje> mMensajeria = new ArrayList();

    public Conversacion() {
    }

    public void setDestinoID(String destID){
        idDest = destID;
    }

    public void setOrigenID(String origID){
        idMia = origID;
    }

    public String getDestinoID() {return idDest;}

    public String getOrigenID() {return idMia;}

    public String getNombreConver() {
        return mNombreConver;
    }

    public void setNombreConver(String nombreConver) {
        this.mNombreConver = nombreConver;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setCantMsjSinLeer (int cant) { this.mCantMsjSinLeer = cant; }

    public int getCantMsjSinLeer () { return mCantMsjSinLeer; }

    public void setmMensajeria (List<Mensaje> msjs) { this.mMensajeria = msjs; }

    public List<Mensaje> getMensajeria () { return mMensajeria; }

}

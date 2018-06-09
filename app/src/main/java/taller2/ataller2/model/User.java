package taller2.ataller2.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String mName = "";
    private Bitmap mPictureUser;
    private List<Historia> mHistoriasUsuario = new ArrayList();

    public User(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }

    public Bitmap getFoto() { return mPictureUser; }
    public void setFoto(Bitmap picture){ this.mPictureUser = picture;}

}

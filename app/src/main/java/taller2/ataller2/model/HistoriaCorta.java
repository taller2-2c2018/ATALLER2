package taller2.ataller2.model;

import android.graphics.Bitmap;

public class HistoriaCorta {

    private Bitmap mPicture;
    private Bitmap mPictureUser;


    public HistoriaCorta() {

    }

    public void setPictureUsr(Bitmap picture){ this.mPictureUser = picture;}

    public Bitmap getPictureUsr() {return this.mPictureUser;}

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap picture) {
        this.mPicture = picture;
    }
}

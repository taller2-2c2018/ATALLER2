package taller2.ataller2.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class HistoriaCorta {

    private Bitmap mPicture;
    private Bitmap mPictureUser;
    private Uri mUri;
    private String mStringUri;
    private String mType;

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

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getType() {
        return mType;
    }
}

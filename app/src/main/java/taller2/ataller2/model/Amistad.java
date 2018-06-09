package taller2.ataller2.model;

import android.graphics.Bitmap;

public class Amistad {

    private String mName = "";
    private String mDescription = "";
    private Bitmap mPicture;

    public Amistad(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
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
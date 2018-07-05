package taller2.ataller2.model;

import android.graphics.Bitmap;

public class Amistad {

    private String mName = "";
    private String mDescription = "";
    private Bitmap mPicture;
    private String mId;
    private String mRequester;
    private String mTarget;
    private boolean activa = true;

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

    public void setTarget(String target) {
        this.mTarget = target;
    }

    public String getTarget() {
        return mTarget;
    }

    public void setRequester(String requester) {
        this.mRequester = requester;
    }

    public String getRequester() {
        return mRequester;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setActiva(boolean isActiva) {
        this.activa = isActiva;
    }
    public boolean getActiva (){
        return activa;
    }
}
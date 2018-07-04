package taller2.ataller2.services;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Perfil;

/**
 * Created by FernandoN on 18/04/2018.
 */

public class MockPerfilService implements PerfilService {

    //private static Context context;
    private List<Perfil> mPerfiles;
    private Context mContext;

    public MockPerfilService(Context context) {
        mContext = context;
    }

    @Override
    public void updatePerfilData(Activity activity, String id) {

        mPerfiles = new ArrayList<>();
        Perfil c1 = new Perfil("Emanuel");

        //c1.setDescription("Emanuel te ha enviado una solicitud de amistad.");

        //c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pizzaprueba2));
        //c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.images));
        mPerfiles.add(c1);
    }

    @Override
    public void updateFoto(Activity activity, Bitmap bitmap) {

    }

    @Override
    public Perfil getPerfil() {

        return mPerfiles.get(0);
    }

    @Override
    public Perfil getMiPerfil() {

        return mPerfiles.get(0);
    }

}

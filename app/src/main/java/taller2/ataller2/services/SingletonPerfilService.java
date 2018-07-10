package taller2.ataller2.services;

import android.app.Activity;
import android.media.FaceDetector;
import android.net.Uri;

import java.util.List;

import taller2.ataller2.model.Perfil;
import taller2.ataller2.services.facebook.FacebookService;

public class SingletonPerfilService implements MiPerfilService {

    private static final String PERFIL = "https://application-server-tdp2.herokuapp.com/user/profile/";
    private String url;

    public SingletonPerfilService(){
        url = PERFIL + ServiceLocator.get(FacebookService.class).getFacebookID();
    }

    @Override
    public void updatePerfilData(Activity activity) {

    }

    @Override
    public void updateFoto(Activity activity, Uri uri) {

    }

    @Override
    public List<String> getAmigos() {
        return null;
    }

    @Override
    public Perfil getMiPerfil() {
        return null;
    }
}

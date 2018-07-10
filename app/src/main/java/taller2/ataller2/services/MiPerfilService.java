package taller2.ataller2.services;

import android.app.Activity;
import android.net.Uri;

import java.util.List;

import taller2.ataller2.model.Perfil;

public interface MiPerfilService extends CustomService{

    void updatePerfilData(Activity activity);
    void updateFoto(Activity activity, Uri uri);

    List<String> getAmigos();
    Perfil getMiPerfil();

}
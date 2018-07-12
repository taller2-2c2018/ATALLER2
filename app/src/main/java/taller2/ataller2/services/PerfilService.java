package taller2.ataller2.services;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;

import taller2.ataller2.model.Perfil;

public interface PerfilService extends CustomService{
    void updatePerfilData(Activity activity, String id, OnCallback callback);
    void updateFoto(Activity activity, Uri uri);
    void solicitarAmistad(Activity activity, String id, OnCallback callback);

    List<String> getAmigos();
    Perfil getMiPerfil();
    Perfil getPerfil();
    void updatePerfil(Activity activity, Perfil perfil);
}

package taller2.ataller2.services;
import android.app.Activity;
import android.graphics.Bitmap;

import java.util.List;

import taller2.ataller2.model.Perfil;

public interface PerfilService extends CustomService{
    void updatePerfilData(Activity activity, String id);
    void updateFoto(Activity activity, Bitmap bitmap);

    Perfil getMiPerfil();
    Perfil getPerfil();
}

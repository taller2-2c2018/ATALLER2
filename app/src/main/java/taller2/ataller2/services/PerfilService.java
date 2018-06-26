package taller2.ataller2.services;
import android.app.Activity;

import java.util.List;

import taller2.ataller2.model.Perfil;

public interface PerfilService extends CustomService{
    void updatePerfilData(Activity activity, String id);

    Perfil getMiPerfil();
    Perfil getPerfil();
}

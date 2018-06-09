package taller2.ataller2.services;
import java.util.List;

import taller2.ataller2.model.Perfil;

public interface PerfilService extends CustomService{
    void updatePerfilData();
    List<Perfil> getPerfiles();
    Perfil getPerfil(int index);
}

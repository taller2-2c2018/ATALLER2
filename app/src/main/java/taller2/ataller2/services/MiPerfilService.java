package taller2.ataller2.services;

import android.app.Activity;
import android.net.Uri;

import org.json.JSONArray;

import java.util.List;

import taller2.ataller2.model.Perfil;

public interface MiPerfilService extends CustomService{

    //SOLO SE DEBE LLAMAR UNA VEZ EN EL PROGRAMA
    void updatePerfilData(Activity activity);

    //LOS UPDATES: SE LLAMAN MUCHAS VECES
    void updateFoto(String uriString);
    void updateFechaNacimiento(String fechaNacimiento);
    void updateSexo(String sexo);
    void updateMail(String mail);
    void agregarAmigo(String idAmigo);

    //SOLO TIENE UN GET
    Perfil getMiPerfil();

}
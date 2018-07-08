package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;

import java.util.Calendar;
import java.util.List;

import taller2.ataller2.model.Amistad;
import taller2.ataller2.model.Perfil;

public interface AmistadesService extends CustomService {

    void updateAmistadesData();
    List<Amistad> getAmistades();
    Amistad getAmistad(int index);
    void getAmistades(Activity activity);

    void rechazarAmistad(Activity activity, Amistad amistad);
    void aceptarAmistad(Activity activity, Amistad amistad);

    void getAllUsers(Activity activity, OnCallback callback);
    List<Perfil> processAllUsers();

}
package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;

import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.services.CustomService;

public interface HistoriasService extends CustomService {

    void updateHistoriasData(Activity activity);
    void updateHistoriasCortasData(Activity activity);

    List<Historia> getHistorias(Activity activity);
    List<Historia> getMisHistorias(Activity activity);


    List<HistoriaCorta> getHistoriasCortas(Activity activity);

    Historia getHistoria(int index);
    List<String> getUsers();

    boolean crearHistoria(FragmentManager fragmentManager, Historia historia);
    boolean crearHistoriaCorta(FragmentManager fragmentManager, HistoriaCorta historia);
    boolean actReaction(FragmentManager fragmentManager, Historia historia, EmotionType emotion);
    boolean actCommet(FragmentManager fragmentManager, Historia historia, String comment);
}
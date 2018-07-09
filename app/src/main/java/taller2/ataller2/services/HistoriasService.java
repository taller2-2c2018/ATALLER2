package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.widget.ImageView;

import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;

public interface HistoriasService extends CustomService {

    void updateHistoriasData(Activity activity, OnCallback callback);

    List<Historia> getHistorias(Activity activity);
    List<Historia> getMisHistorias(Activity activity);
    List<Historia> getMisHistorias(Activity activity, String id);

    List<HistoriaCorta> getHistoriasCortas(Activity activity);

    Historia getHistoria(int index);
    List<String> getUsers();

    boolean crearHistoria(FragmentManager fragmentManager, Historia historia, OnCallback callback);
    boolean crearHistoriaCorta(FragmentManager fragmentManager, HistoriaCorta historia, OnCallback callback);
    boolean actReaction(FragmentManager fragmentManager, Historia historia, EmotionType emotion);
    boolean actCommet(FragmentManager fragmentManager, Historia historia, String comment);

    void uploadImageFromMemory(ImageView imageView, OnCallbackImageUpload callback);

}
package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.R;

public class MockHistoriasService implements HistoriasService {

    //private static Context context;
    private List<Historia> mHistorias = null;
    private List<HistoriaCorta> mHistoriasCortas = null;

    private Context mContext;
    public MockHistoriasService(Context context){
        mContext = context;
    }

    @Override
    public void updateHistoriasData(Activity activity, OnCallback callback) {
        if (mHistorias == null){
            mHistorias = new ArrayList<>();
            Historia c1 = new Historia("Increible lo que sucedio...");
            Historia c2 = new Historia("Android funciona perfecto...");

            c1.setDescription("wow no te la puedo creer");
            c2.setDescription("Esto es un lujo");

            c1.setFecha("Facultad de Ing. Capital Federal.");
            c2.setFecha("La Rural. Capital Federal");

            mHistorias.add(c1);
            mHistorias.add(c2);
        }
    }

    @Override
    public List<Historia> getHistorias(Activity activity) {
        return null;
    }

    public void updateHistoriasCortasData(Activity activity) {
        mHistoriasCortas = new ArrayList<>();
        HistoriaCorta c1 = new HistoriaCorta();
        HistoriaCorta c2 = new HistoriaCorta();


        mHistoriasCortas.add(c1);
        mHistoriasCortas.add(c2);
    }

    @Override
    public List<Historia> getMisHistorias(Activity activity) {
        return null;
    }

    @Override
    public List<Historia> getMisHistorias(Activity activity, String id) {
        return null;
    }

    @Override
    public List<HistoriaCorta> getHistoriasCortas(Activity activity) {
        if (mHistoriasCortas == null) {
            updateHistoriasCortasData(activity);
        }
        return mHistoriasCortas;
    }

    @Override
    public Historia getHistoria(int index) {
        return mHistorias.get(index);
    }

    @Override
    public List<String> getUsers() {
        List<String> usuarios = new ArrayList<>();
        usuarios.add("Fernando Nitz");
        usuarios.add("Diego Maradona");
        usuarios.add("Lionel Messi");
        return usuarios;
    }

    @Override
    public boolean crearHistoria(FragmentManager fragmentManager, Historia historia, OnCallback callback) {
        mHistorias.add(historia);
        return true;
    }

    @Override
    public boolean crearHistoriaCorta(FragmentManager fragmentManager, HistoriaCorta historia, OnCallback callback) {
        return false;
    }

    @Override
    public boolean actReaction(FragmentManager fragmentManager, Historia historia, EmotionType emotion) {
        return false;
    }

    @Override
    public boolean actCommet(FragmentManager fragmentManager, Historia historia, String comment) {
        return false;
    }

    @Override
    public void uploadImageFromMemory(ImageView imageView, OnCallbackImageUpload callback) {

    }
}
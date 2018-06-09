package taller2.ataller2.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.services.facebook.HistoriasService;
import taller2.ataller2.R;

public class MockHistoriasService implements HistoriasService {

    //private static Context context;
    private List<Historia> mHistorias;
    private Context mContext;
    public MockHistoriasService(Context context){
        mContext = context;
    }

    @Override
    public void updateHistoriasData() {

        mHistorias = new ArrayList<>();
        Historia c1 = new Historia("Increible lo que sucedio...");
        Historia c2 = new Historia("Android funciona perfecto...");

        c1.setDescription("wow no te la puedo creer");
        c2.setDescription("Esto es un lujo");

        c1.setFecha("Facultad de Ing. Capital Federal.");
        c2.setFecha("La Rural. Capital Federal");

        c1.setUbicacion("12 de Abril de 2016");
        c2.setUbicacion("14 de Marzo de 2018");

        c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.river4));
        c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.river4));

        c1.setPictureUsr(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diego));
        c2.setPictureUsr(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.elche));

        mHistorias.add(c1);
        mHistorias.add(c2);
    }

    @Override
    public List<Historia> getHistorias() {
        if (mHistorias == null) {
            updateHistoriasData();
        }
        return mHistorias;
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
}
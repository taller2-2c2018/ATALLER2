package taller2.ataller2.services;

import android.app.Activity;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Conversacion;
import taller2.ataller2.model.Mensaje;

public class MockConversacionService implements ConversacionService {

    //private static Context context;
    private List<Conversacion> mConversaciones;
    private Context mContext;

    public MockConversacionService(Context context){
        mContext = context;
    }

    @Override
    public void updateConversacionData(Activity activity) {

        mConversaciones = new ArrayList<>();
        Conversacion c1 = new Conversacion();
        Conversacion c2 = new Conversacion();

        c1.setCantMsjSinLeer(3);

        List <Mensaje> l1 = new ArrayList<>();
        l1.add(new Mensaje("Hola" , 1));
        l1.add(new Mensaje("Como andas?" , 1));
        l1.add(new Mensaje("Bien" , 0));
        l1.add(new Mensaje("vos?" , 0));
        l1.add(new Mensaje("Todo bien por suerte" , 1));
        l1.add(new Mensaje("Me alegro" , 0));
        l1.add(new Mensaje("un gusto" , 1));
        l1.add(new Mensaje("bueno, chau" , 1));
        l1.add(new Mensaje("chau" , 0));

        List <Mensaje> l2 = new ArrayList<>();
        l2.add(new Mensaje("Hola" , 1));
        l2.add(new Mensaje("Hola.." , 1));
        l2.add(new Mensaje("Hola!!!!" , 1));
        l2.add(new Mensaje("Estas?" , 1));

        c1.setDescription("chau");
        c2.setDescription("Estas?");

        c1.setmMensajeria(l1);
        c2.setmMensajeria(l2);

        mConversaciones.add(c1);
        mConversaciones.add(c2);
    }

    @Override
    public List<Conversacion> getConversaciones(Activity activity) {
        if (mConversaciones == null) {
            updateConversacionData(activity);
        }
        return mConversaciones;
    }

    @Override
    public Conversacion getConversacion(int index) {
        return mConversaciones.get(index);
    }
}
package taller2.ataller2.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.List;
import taller2.ataller2.R;
import taller2.ataller2.model.Notificacion;

public class MockNotificacionService implements NotificacionesService {

    private List<Notificacion> mNotificaciones;
    private Context mContext;

    public MockNotificacionService(Context context) {
        mContext = context;
    }

    @Override
    public void updateNotificacionesData() {

        mNotificaciones = new ArrayList<>();
        Notificacion c1 = new Notificacion("El Diegote");
        Notificacion c2 = new Notificacion("Ringo");

        c1.setDescription("Emanuel te ha enviado una solicitud de amistad.");
        c2.setDescription("Alejandro ha comentado una foto en la que apareces.");

        c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diego));
        c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ringo));
        mNotificaciones.add(c1);
        mNotificaciones.add(c2);
    }

    @Override
    public List<Notificacion> getNotificaciones() {
        if (mNotificaciones == null) {
            updateNotificacionesData();
        }
        return mNotificaciones;
    }

    @Override
    public Notificacion getNotificacion(int index) {
        return mNotificaciones.get(index);
    }

}
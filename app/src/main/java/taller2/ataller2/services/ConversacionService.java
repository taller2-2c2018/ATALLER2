package taller2.ataller2.services;

import android.app.Activity;

import java.util.List;

import taller2.ataller2.model.Amigo;
import taller2.ataller2.model.Conversacion;

public interface ConversacionService extends CustomService {
    void updateConversacionData(Activity activity);
    List<Conversacion> getConversaciones(Activity activity);
    Conversacion getConversacion(int index);

    void getAmigosData(Activity activity, final OnCallback callback);
    List<Amigo> getAmigos();
}
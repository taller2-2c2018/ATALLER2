package taller2.ataller2.services;

import java.util.List;

import taller2.ataller2.model.Conversacion;

public interface ConversacionService extends CustomService {
    void updateConversacionData();
    List<Conversacion> getConversaciones();
    Conversacion getConversacion(int index);
}
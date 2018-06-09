package taller2.ataller2.services;

import java.util.List;

import taller2.ataller2.model.Notificacion;

public interface NotificacionesService extends CustomService {
    void updateNotificacionesData();
    List<Notificacion> getNotificaciones();
    Notificacion getNotificacion(int index);
}
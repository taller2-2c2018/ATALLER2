package taller2.ataller2.services.facebook;

import java.util.List;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.services.CustomService;

public interface HistoriasService extends CustomService {

    void updateHistoriasData();
    void updateHistoriasCortasData();

    List<Historia> getHistorias();
    List<HistoriaCorta> getHistoriasCortas();

    Historia getHistoria(int index);
    List<String> getUsers();
}
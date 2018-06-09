package taller2.ataller2.services;

import java.util.List;

import taller2.ataller2.model.Amistad;

public interface AmistadesService extends CustomService {

    void updateAmistadesData();
    List<Amistad> getAmistades();
    Amistad getAmistad(int index);
}
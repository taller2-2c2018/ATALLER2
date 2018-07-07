package taller2.ataller2.model;

import taller2.ataller2.services.EmotionType;

public class Reaccion {

    private EmotionType emocion;
    private String autor;

    public Reaccion(EmotionType mEmcion, String mAutor) {
        emocion = mEmcion;
        autor = mAutor;
    }

    public String getAutor() {
        return autor;
    }

    public EmotionType getEmocion() {
        return emocion;
    }
}

package taller2.ataller2.model;

public class Mensaje {

    private int esEmisor;
    private String mMensaje;

    public Mensaje(String mensaje, int emisor) {
        mMensaje = mensaje;
        esEmisor = emisor;
    }

    public int getEsEmisor () { return esEmisor; }
    public String getMensaje () { return mMensaje; }

    public void setEsEmisor ( int emisor ) { esEmisor = emisor; }
    public void setMensaje (String mensaje) { mMensaje = mensaje; }
}

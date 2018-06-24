package taller2.ataller2.model;

public class LocationO {

    private double mLongitud;
    private double mLatitud;

    public LocationO(double lat, double lon) {
        mLatitud = lat;
        mLongitud = lon;
    }

    public double getLongitud() {
        return mLongitud;
    }

    public void setLongitud(double longitud) {
        this.mLongitud = longitud;
    }

    public double getLatitud() {
        return mLatitud;
    }

    public void setLatitud(double latitud) {
        this.mLatitud = latitud;
    }

    public double getLongitude() {
        return mLongitud;
    }

    public void setLongitude(double longitud) {
        this.mLongitud = longitud;
    }

    public double getLatitude() {
        return mLatitud;
    }

    public void setLatitude(double latitud) {
        this.mLatitud = latitud;
    }
}

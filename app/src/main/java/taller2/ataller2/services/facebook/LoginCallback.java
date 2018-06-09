package taller2.ataller2.services.facebook;


public interface LoginCallback {
    void onSuccess();
    void onCancel();
    void onError(String reason);
}


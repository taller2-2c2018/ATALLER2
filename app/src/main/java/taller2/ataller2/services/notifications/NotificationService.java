package taller2.ataller2.services.notifications;

import taller2.ataller2.services.CustomService;

public interface NotificationService extends CustomService {
    void sendInstanceIdToken(String token, SendInstanceIdCallback callback);
    void scheduleSendInstanceId();
    String getToken();
    void refreshToken();
}

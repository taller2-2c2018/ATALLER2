package taller2.ataller2;

import android.app.Application;
import android.content.Context;

import taller2.ataller2.model.Perfil;
import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.ConversacionService;
import taller2.ataller2.services.HerokuAmistadesService;
import taller2.ataller2.services.HerokuConversacionService;
import taller2.ataller2.services.HerokuHistoriasService;
import taller2.ataller2.services.HerokuPerfilService;
import taller2.ataller2.services.MockAmistadesService;
import taller2.ataller2.services.MockConversacionService;
import taller2.ataller2.services.MockNotificacionService;
import taller2.ataller2.services.NotificacionesService;
import taller2.ataller2.services.PerfilService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.BaseFacebookService;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.HistoriasService;
import taller2.ataller2.services.location.LocationService;
import taller2.ataller2.services.location.MapsLocationService;
import taller2.ataller2.services.notifications.FirebaseNotificationService;
import taller2.ataller2.services.notifications.NotificationService;

public class AppMain extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // this method fires once as well as constructor
        // but also application has context here
        bindServices(getApplicationContext());
    }

    private void bindServices(Context applicationContext) {
        ServiceLocator.init(applicationContext);
        //ServiceLocator.bindCustomServiceImplementation(NotificacionesService.class, MockNotificacionService.class);
        ServiceLocator.bindCustomServiceImplementation(ConversacionService.class, HerokuConversacionService.class);
        ServiceLocator.bindCustomServiceImplementation(AmistadesService.class, HerokuAmistadesService.class);
        ServiceLocator.bindCustomServiceImplementation(FacebookService.class, BaseFacebookService.class);
        ServiceLocator.bindCustomServiceImplementation(PerfilService.class, HerokuPerfilService.class);
        ServiceLocator.bindCustomServiceImplementation(HistoriasService.class, HerokuHistoriasService.class);
        ServiceLocator.bindCustomServiceImplementation(NotificationService.class, FirebaseNotificationService.class);
        ServiceLocator.bindCustomServiceImplementation(LocationService.class, MapsLocationService.class);
    }

}


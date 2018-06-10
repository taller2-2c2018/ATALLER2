package taller2.ataller2;

import android.app.Application;
import android.content.Context;

import taller2.ataller2.services.AmistadesService;
import taller2.ataller2.services.ConversacionService;
import taller2.ataller2.services.MockAmistadesService;
import taller2.ataller2.services.MockConversacionService;
import taller2.ataller2.services.MockHistoriasService;
import taller2.ataller2.services.MockNotificacionService;
import taller2.ataller2.services.NotificacionesService;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.BaseFacebookService;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.facebook.HistoriasService;

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
        ServiceLocator.bindCustomServiceImplementation(FacebookService.class, BaseFacebookService.class);
        ServiceLocator.bindCustomServiceImplementation(AmistadesService.class, MockAmistadesService.class);
        ServiceLocator.bindCustomServiceImplementation(HistoriasService.class, MockHistoriasService.class);
        ServiceLocator.bindCustomServiceImplementation(ConversacionService.class, MockConversacionService.class);
        ServiceLocator.bindCustomServiceImplementation(NotificacionesService.class, MockNotificacionService.class);
    }

}


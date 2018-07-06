package taller2.ataller2.services;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Conversacion;
import taller2.ataller2.model.Mensaje;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.notifications.NotificationService;

public class HerokuConversacionService implements ConversacionService {

    private static final String PERFIL = "https://application-server-tdp2.herokuapp.com/user/profile/";

    //private static Context context;
    private List<Conversacion> mConversaciones;
    private Context mContext;
    private boolean mDownloading;

    public HerokuConversacionService(Context context){
        mContext = context;
    }

    @Override
    public void updateConversacionData(Activity activity) {

        mConversaciones = new ArrayList<>();
        List<String> ids =  ServiceLocator.get(PerfilService.class).getAmigos();
        if (ids != null){
            for (String id : ids){
                Conversacion conversacion = new Conversacion();
                updatePerfilData(activity, conversacion, id);
                mConversaciones.add(conversacion);
            }
        }
    }

    @Override
    public List<Conversacion> getConversaciones(Activity activity) {
        if (mConversaciones == null) {
            updateConversacionData(activity);
        }
        return mConversaciones;
    }

    @Override
    public Conversacion getConversacion(int index) {
        return mConversaciones.get(index);
    }


    public void updatePerfilData(final Activity activity, final Conversacion conversacion, String id) {
        final NetworkObject requestTokenObject = getPerfilNetworkObject(id);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                JSONObject data = resultToken.getJSONObject("data");
                                String nom = data.getString("mFirstName");
                                String ap =  data.getString("mLastName");
                                conversacion.setNombreConver(nom + " " + ap);
                            }
                        }
                        catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + result.mResultValue + "\"");
                        }

                    }
                    mDownloading = false;
                }

                @Override
                public NetworkInfo getActiveNetworkInfo(Context context) {
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    return networkInfo;
                }

                @Override
                public void onProgressUpdate(int progressCode, int percentComplete) {}

                @Override
                public void onFinishDownloading() {
                    mDownloading = false;
                }
            });
        }
    }

    private NetworkObject getPerfilNetworkObject(String id) {
        String url = PERFIL + id;
        NetworkObject networkObject = new NetworkObject(url, HttpMethodType.GET);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        return networkObject;
    }



}
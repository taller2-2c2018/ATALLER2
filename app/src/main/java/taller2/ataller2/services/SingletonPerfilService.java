package taller2.ataller2.services;

import android.app.Activity;
import android.content.Context;
import android.media.FaceDetector;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.model.Perfil;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.notifications.NotificationService;

public class SingletonPerfilService implements MiPerfilService {

    private static final String PERFIL = "https://application-server-tdp2.herokuapp.com/user/profile/";
    private boolean mDownloading;
    private JSONObject resultadoPerfil;
    private Perfil mMiPerfil;

    private String nombre = "";
    private String apellido = "";
    private String fechaNacimiento = "";
    private String mail = "";
    private String sexo = "";
    private String fileType = "";
    private String picture = "";
    private JSONArray amigos = null;
    private List<String> idAmigos = null;

    public SingletonPerfilService(){

    }

    @Override
    public void updateFoto(String uriString) {
        this.picture = uriString;
        mMiPerfil.setPicture(uriString);
    }

    @Override
    public void updateFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        mMiPerfil.setFechaNacimiento(fechaNacimiento);
    }

    @Override
    public void updateSexo(String sexo) {
        this.sexo = sexo;
        mMiPerfil.setSexo(sexo);
    }

    @Override
    public void updateMail(String mail) {
        this.mail = mail;
        mMiPerfil.setMail(mail);
    }

    @Override
    public void agregarAmigo(String idAmigo) {
        this.idAmigos.add(idAmigo);
        mMiPerfil.setAmigos(this.idAmigos);
    }

    @Override
    public Perfil getMiPerfil() {
        return mMiPerfil;
    }


    // TODO LO DE ABAJO SOLO SE LLAMA UNA VEZ!!!, No actualizar con estas funciones!!!

    @Override
    public void updatePerfilData(final Activity activity) {
        String id = ServiceLocator.get(FacebookService.class).getFacebookID();
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
                                resultadoPerfil = resultToken.getJSONObject("data");
                                loadPerfil();
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

    private void loadPerfil(){
        if (resultadoPerfil != null) {
            try {

                nombre = resultadoPerfil.getString("mFirstName");
                apellido = resultadoPerfil.getString("mLastName");
                if (resultadoPerfil.getString("mBirthDate") != null) {
                    fechaNacimiento = resultadoPerfil.getString("mBirthDate");
                }
                if (resultadoPerfil.getString("mEmail") != null) {
                    mail = resultadoPerfil.getString("mEmail");
                }
                if (resultadoPerfil.getString("mSex") == null) {
                    sexo = resultadoPerfil.getString("mSex");
                }

                fileType = resultadoPerfil.getString("mFileTypeProfilePicture");
                picture = resultadoPerfil.getString("mProfilePicture");
                amigos = resultadoPerfil.getJSONArray("mFriendshipList");

                idAmigos = new ArrayList();
                for (int i = 0 ; i < amigos.length(); i++) {
                    String obj = null;
                    try {
                        obj = amigos.getString(i);
                        if (!estaAdentro(idAmigos,obj)){
                            idAmigos.add(obj);
                        }
                    }
                    catch (Exception ex){ }
                }

                mMiPerfil = new Perfil(nombre + " " + apellido );
                mMiPerfil.setPicture(picture);
                mMiPerfil.setFechaNacimiento(fechaNacimiento);
                mMiPerfil.setMail(mail);
                mMiPerfil.setSexo(sexo);
                mMiPerfil.setAmigos(idAmigos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean estaAdentro(List<String> lista,String  buscado){
        String id = ServiceLocator.get(FacebookService.class).getFacebookID();
        if (id.equals(buscado)){
            return true;
        }
        for (String elem : lista){
            if (elem.equals(buscado)){
                return true;
            }
        }
        return false;
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

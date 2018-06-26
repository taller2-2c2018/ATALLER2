package taller2.ataller2.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;

public class HerokuPerfilService implements PerfilService {

    private static final String PERFIL = "https://application-server-tdp2.herokuapp.com/user/profile/";

    private boolean mDownloading = false;
    private List<Perfil> mPerfiles;
    private Context mContext;
    private Perfil mPerfil;
    private Perfil mMiPerfil;
    JSONObject resultadoPerfil = null;

    public HerokuPerfilService(Context context) {
        mContext = context;
    }


    @Override
    public Perfil getPerfil() {
        return mPerfil;
    }

    @Override
    public Perfil getMiPerfil() {
        return mMiPerfil;
    }

    public void updatePerfil() {
        //JSONArray historias = getHistoriasJSON(activity.getFragmentManager());

        if (resultadoPerfil != null) {
            try {

                String nombre = resultadoPerfil.getString("mFirstName");
                String apellido = resultadoPerfil.getString("mLastName");
                String fechaNacimiento = resultadoPerfil.getString("mBirthDate");
                String mail = resultadoPerfil.getString("mEmail");
                String sexo = resultadoPerfil.getString("mSex");
                int fotoID = resultadoPerfil.getInt("mProfilePictureId");
                String fileType = resultadoPerfil.getString("mFileTypeProfilePicture");
                JSONArray amigos = resultadoPerfil.getJSONArray("mFriendshipList");

                mMiPerfil = new Perfil(nombre + " " + apellido );
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                mMiPerfil.setPicture(icon);
                mMiPerfil.setFechaNacimiento(fechaNacimiento);
                mMiPerfil.setMail(mail);
                mMiPerfil.setSexo(sexo);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updatePerfilData( Activity activity, String id) {
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
                                updatePerfil();
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
        //networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        return networkObject;
    }
}

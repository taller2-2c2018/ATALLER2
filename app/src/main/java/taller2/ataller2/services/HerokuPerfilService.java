package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.location.LocationService;
import taller2.ataller2.services.notifications.NotificationService;

public class HerokuPerfilService implements PerfilService {

    private static final String PERFIL = "https://application-server-tdp2.herokuapp.com/user/profile/";
    private static final String PERFIL_FOTO = "https://application-server-tdp2.herokuapp.com/user/profilePicture";
    private static final String REQUEST_AMISTAD = "https://application-server-tdp2.herokuapp.com/user/friendship";
    private static final String FILES = "https://application-server-tdp2.herokuapp.com/file/";
    private static final String AUTH_RESULT = "status";
    private static final String AUTH_DATA = "data";

    private boolean mDownloading = false;
    private List<Perfil> mPerfiles;
    private Context mContext;
    private Perfil mPerfil;
    private Perfil mMiPerfil;
    private List<String> idAmigos;

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

    public void updatePerfil(Activity activity) {
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
                Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                mMiPerfil.setPicture(icon);

                //getPerfilFile(activity, mMiPerfil, fotoID);

                mMiPerfil.setFechaNacimiento(fechaNacimiento);
                mMiPerfil.setMail(mail);
                mMiPerfil.setSexo(sexo);

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

    @Override
    public void updatePerfilData(final Activity activity, String id, final OnCallback callback) {
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
                                updatePerfil(activity);
                                callback.onFinish();
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

    @Override
    public void updateFoto(final Activity activity, Bitmap bitmap) {
        final NetworkObject requestTokenObject = updateFotoNetworkObject(bitmap);
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
                                updatePerfil(activity);
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

    @Override
    public void solicitarAmistad(final Activity activity, String id) {
        final NetworkObject requestTokenObject = solicitarAmistadNetworkObject(id);
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
                                updatePerfil(activity);
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

    @Override
    public List<String> getAmigos() {
        return idAmigos;
    }

    private NetworkObject solicitarAmistadNetworkObject(String id) {
        String requestBody = solicitarAmistadObject(id).toString();
        NetworkObject networkObject = new NetworkObject(REQUEST_AMISTAD, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject solicitarAmistadObject(String id) {
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            final String target = "mTargetUsername";
            requestHistoriaJsonObject.put(target,id);
            final String desc = "mDescription";
            String nombre = ServiceLocator.get(FacebookService.class).getName();
            requestHistoriaJsonObject.put(desc,nombre + " te ha agregado a su lista de contactos.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
    }

    private NetworkObject updateFotoNetworkObject(Bitmap bitmap) {
        String requestBody = updateFotoObject(bitmap).toString();
        NetworkObject networkObject = new NetworkObject(PERFIL_FOTO, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject updateFotoObject(Bitmap bitmap) {
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            final String file = "file";
            requestHistoriaJsonObject.put(file, BitMapToString(bitmap));
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,"jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
    }

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void getPerfilFile (Activity activity, final Perfil perfil, int id) {
        final NetworkObject requestTokenObject = getPerfilFileNetworkObject(id);
        final NetworkFragment networkFragment =  NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
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
                                //String foto = data.getString("mFile");
                                perfil.setPicture(StringToBitMap(data.getString("mFile")));

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

    private NetworkObject getPerfilFileNetworkObject (int id){
        String url = FILES + String.valueOf(id);
        NetworkObject networkObject = new NetworkObject(url, HttpMethodType.GET);
        //networkObject.setContentType("application/json");
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        //networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        return networkObject;
    }




}

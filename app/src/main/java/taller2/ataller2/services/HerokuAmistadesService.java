package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

//import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Amistad;
import taller2.ataller2.model.Comentario;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.model.Perfil;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.facebook.LoginCallback;
import taller2.ataller2.services.notifications.NotificationService;

public class HerokuAmistadesService implements AmistadesService {

    private static final String GET_FRIENDSHIP = "https://application-server-tdp2.herokuapp.com/user/friendship";
    private static final String GET_ALL_USERS = "https://application-server-tdp2.herokuapp.com/user/list";
    private static final String FILES = "https://application-server-tdp2.herokuapp.com/file/";
    private static final String ACEPTAR_AMISTAD = "https://application-server-tdp2.herokuapp.com/user/friendship/accept/";
    private static final String RECHAZAR_AMISTAD = "https://application-server-tdp2.herokuapp.com/user/friendship/reject/";

    private static final String AUTH_DATA = "data";
    private static final String AUTH_RESULT = "result";
    private boolean mDownloading = false;
    private String mAuthToken = null;
    private JSONObject resultado;
    private JSONArray amistades_pendientes = null;
    private JSONArray allUsers;
    private Activity contextActivity;

    //private static Context context;
    private List<Amistad> mAmistades;
    private Context mContext;
    public HerokuAmistadesService(Context context){
        mContext = context;
    }

    @Override
    public void updateAmistadesData() {

        mAmistades = new ArrayList<>();

        if (amistades_pendientes != null) {
            for (int i = 0 ; i < amistades_pendientes.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = amistades_pendientes.getJSONObject(i);

                    String id = obj.getString("_id");
                    String requester = obj.getString("requester");
                    String target = obj.getString("target");
                    String message = obj.getString("message");
                    int mProfilePictureId = -1;
                    try{
                        mProfilePictureId = obj.getInt("mProfilePictureId");
                    }
                    catch (Exception ex){
                    }

                    String mFirstName = obj.getString("mFirstName");
                    String mLastName = obj.getString("mLastName");

                    Amistad amistad = new Amistad(mFirstName + " " + mLastName);
                    amistad.setDescription(message);
                    Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                    amistad.setPicture(icon);

                    if (mProfilePictureId != -1) {
                        getHistoriaFile(amistad,mProfilePictureId);
                    }

                    amistad.setId(id);
                    amistad.setRequester(requester);
                    amistad.setTarget(target);

                    mAmistades.add(amistad);
                }
                catch (Exception ex){

                }
            }
        }

    }

    @Override
    public List<Amistad> getAmistades() {
        if (mAmistades == null) {
            updateAmistadesData();
        }
        return mAmistades;
    }

    @Override
    public Amistad getAmistad(int index) {
        return mAmistades.get(index);
    }

    @Override
    public void getAmistades( final Activity activity) {
        contextActivity = activity;
        final NetworkObject requestTokenObject = createGetRequestsNetworkObject();
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        resultado = null;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                amistades_pendientes = resultToken.getJSONArray("data");
                                updateAmistadesData();
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
    public void rechazarAmistad(Activity activity, Amistad amistad) {
        final NetworkObject requestTokenObject = rechazarAmistadNetworkObject(amistad);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
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

    private NetworkObject rechazarAmistadNetworkObject(Amistad amistad) {
        String url = RECHAZAR_AMISTAD + amistad.getRequester();
        NetworkObject networkObject = new NetworkObject(url, HttpMethodType.POST);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    @Override
    public void aceptarAmistad(Activity activity, Amistad amistad) {
        final NetworkObject requestTokenObject = aceptarAmistadNetworkObject(amistad);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
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

    private NetworkObject aceptarAmistadNetworkObject(Amistad amistad) {
        String url = ACEPTAR_AMISTAD + amistad.getRequester();
        NetworkObject networkObject = new NetworkObject(url, HttpMethodType.POST);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private NetworkObject createGetRequestsNetworkObject() {
        NetworkObject networkObject = new NetworkObject(GET_FRIENDSHIP, HttpMethodType.GET);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(AUTH_RESULT);
        responseHeaders.add(AUTH_DATA);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    @Override
    public List<Perfil> processAllUsers(){
        List<Perfil> perfiles = new ArrayList();
        if (allUsers != null) {
            for (int i = 0 ; i < allUsers.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = allUsers.getJSONObject(i);

                    String mFacebookUserId = obj.getString("mFacebookUserId");
                    String mFirebaseId = obj.getString("mFirebaseId");
                    String mFirstName = obj.getString("mFirstName");
                    String mLastName = obj.getString("mLastName");
                    int mProfilePictureId = -1;
                    try {
                        mProfilePictureId = obj.getInt("mProfilePictureId");
                    }catch (Exception ex )
                    {
                        mProfilePictureId = -1;
                    }
                    Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);

                    Perfil perfil = new Perfil(mFirstName + " " + mLastName);
                    perfil.setId(mFacebookUserId);
                    perfil.setPicture(icon);
                    if (mProfilePictureId != -1){
                        //getHistoriaFile(perfil, mProfilePictureId);
                    }

                    perfiles.add(perfil);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return perfiles;
    }

    @Override
    public void getAllUsers(final Activity activity, final OnCallback callback) {
        final NetworkObject requestTokenObject = getAllUsersNetworkObject();
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                allUsers = resultToken.getJSONArray("data");
                                processAllUsers();
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

    private NetworkObject getAllUsersNetworkObject() {
        NetworkObject networkObject = new NetworkObject(GET_ALL_USERS, HttpMethodType.GET);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(AUTH_RESULT);
        responseHeaders.add(AUTH_DATA);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private void getHistoriaFile (final Amistad amistad, int id) {
        final NetworkObject requestTokenObject = getHistoriaFileNetworkObject(id);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(contextActivity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        resultado = null;
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
                                amistad.setPicture(StringToBitMap(data.getString("mFile")));

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

    private void getHistoriaFile (final Perfil perfil, int id) {
        final NetworkObject requestTokenObject = getHistoriaFileNetworkObject(id);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(contextActivity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        resultado = null;
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


    private NetworkObject getHistoriaFileNetworkObject (int id){
        String url = FILES + String.valueOf(id);
        NetworkObject networkObject = new NetworkObject(url, HttpMethodType.GET);
        //networkObject.setContentType("application/json");
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        //networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        return networkObject;
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

    private String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}
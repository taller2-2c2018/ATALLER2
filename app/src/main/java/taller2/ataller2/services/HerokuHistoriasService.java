package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taller2.ataller2.R;
import taller2.ataller2.model.Comentario;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.model.Reaccion;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.location.LocationService;
import taller2.ataller2.services.notifications.NotificationService;

public class HerokuHistoriasService implements HistoriasService {

    private static final String REACTION = "https://application-server-tdp2.herokuapp.com/story/%s/reaction";
    private static final String COMMENT = "https://application-server-tdp2.herokuapp.com/story/%s/comment";
    private static final String STORYS = "https://application-server-tdp2.herokuapp.com/story";
    private static final String FILES = "https://application-server-tdp2.herokuapp.com/file/";
    private static final String AUTH_RESULT = "status";
    private static final String AUTH_DATA = "data";
    private boolean mDownloading = false;
    private String mAuthToken = null;
    JSONArray resultado = null;
    JSONArray resultadoHistorias = null;
    JSONObject resultado2;
    //private static Context context;
    private List<Historia> mHistorias;
    private List<HistoriaCorta> mHistoriasCortas;
    private Activity contextActivity;

    private Context mContext;
    public HerokuHistoriasService(Context context){
        mContext = context;
    }

    @Override
    public void updateHistoriasData(Activity activity, final OnCallback callback) {
        contextActivity = activity;
        final NetworkObject requestTokenObject = getHistoriasNetworkObject();
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
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
                                resultadoHistorias = resultToken.getJSONArray("data");
                                updateHistorias();
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
                public void onProgressUpdate(int progressCode, int percentComplete) {

                }

                @Override
                public void onFinishDownloading() {
                    mDownloading = false;
                }
            });
        }
    }

    public void updateHistorias() {
        mHistorias = new ArrayList();
        mHistoriasCortas = new ArrayList();
        //JSONArray historias = getHistoriasJSON(activity.getFragmentManager());

        if (resultadoHistorias != null) {
            for (int i = 0 ; i < resultadoHistorias.length(); i++) {
                JSONObject obj = null;
                try {
                    obj = resultadoHistorias.getJSONObject(i);

                    String historiaID = obj.getString("mStoryId");
                    String title = obj.getString("mTitle");
                    String desc = obj.getString("mDescription");
                    String userID = obj.getString("mFacebookUserId");
                    String lat = obj.getString("mLatitude");
                    String lng = obj.getString("mLongitude");
                    int fileID = obj.getInt("mFileId");
                    int fileProfileID = -1;
                    try {
                       fileProfileID = obj.getInt("mProfilePictureId");
                    }
                    catch (Exception ex){
                    }
                    String fileType = obj.getString("mFileType");
                    boolean isFlash = obj.getBoolean("mFlash");
                    String location = obj.getString("mLocation");
                    JSONArray reactions = obj.getJSONArray("mReactions");
                    JSONArray comentarios = obj.getJSONArray("mComments");

                    String nom = obj.getString("mFirstName");
                    String ape = obj.getString("mLastName");

                    if (isFlash){
                        HistoriaCorta historia = new HistoriaCorta();

                        getHistoriaCortaFile(historia,fileID,1);
                        getHistoriaCortaFile(historia,fileProfileID,2);

                        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                        historia.setPicture(icon);
                        historia.setPictureUsr(icon);

                        mHistoriasCortas.add(historia);
                    }
                    else{
                        Historia historia = new Historia(title);

                        getHistoriaFile(historia,fileID,1);
                        getHistoriaFile(historia,fileProfileID,2);
                        historia.setNombre(nom + " " + ape );
                        historia.setUserID(userID);
                        historia.setID(historiaID);
                        historia.setDescription(desc);
                        historia.setUbicacion(location);
                        historia.setLatitud(lat);
                        historia.setLongitud(lng);
                        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                        historia.setPicture(icon);
                        historia.setPictureUsr(icon);

                        List<Comentario> lista = new ArrayList();
                        List<Reaccion> listaReacciones = new ArrayList();

                        if (comentarios != null) {
                            for (int j = 0 ; j < comentarios.length(); j++) {
                                JSONObject obj2 = null;
                                try {
                                    Comentario comentario = new Comentario();
                                    obj2 = comentarios.getJSONObject(j);
                                    comentario.setComentario(obj2.getString("mComment"));
                                    comentario.setHorario(obj2.getString("mDate"));
                                    comentario.setNombre(obj2.getString("mFacebookUserId"));
                                    lista.add(comentario);
                                }
                                catch (Exception ex){ }
                            }
                        }
                        if (reactions != null) {
                            for (int j = 0 ; j < reactions.length(); j++) {
                                JSONObject obj2 = null;
                                try {
                                    obj2 = reactions.getJSONObject(j);
                                    String emocion = obj2.getString("mReaction");
                                    String user = obj2.getString("mFacebookUserId");
                                    EmotionType emotionType = EmotionType.LIKE;
                                    emotionType.setEmotionServer(emocion);
                                    Reaccion reaccion = new Reaccion(emotionType,user);
                                    listaReacciones.add(reaccion);
                                }
                                catch (Exception ex){ }
                            }
                        }


                        historia.setComentarios(lista);
                        historia.setReacciones(listaReacciones);
                        mHistorias.add(historia);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public List<Historia> getHistorias(Activity activity) {
        return mHistorias;
    }

    @Override
    public List<Historia> getMisHistorias(Activity activity) {
        List<Historia> misHistorias = new ArrayList();
        String myID = ServiceLocator.get(FacebookService.class).getFacebookID();
        for (Historia historia:mHistorias){
            if (historia.getUserID().equals(myID)){
                misHistorias.add(historia);
            }
        }
        return misHistorias;
    }

    @Override
    public List<Historia> getMisHistorias(Activity activity, String id) {
        List<Historia> misHistorias = new ArrayList();
        for (Historia historia:mHistorias){
            if (historia.getUserID().equals(id)){
                misHistorias.add(historia);
            }
        }
        return misHistorias;
    }

    @Override
    public List<HistoriaCorta> getHistoriasCortas(Activity activity) {
        return mHistoriasCortas;
    }

    @Override
    public Historia getHistoria(int index) {
        return mHistorias.get(index);
    }

    @Override
    public List<String> getUsers() {
        List<String> usuarios = new ArrayList();
        usuarios.add("Fernando Nitz");
        usuarios.add("Diego Maradona");
        usuarios.add("Lionel Messi");
        return usuarios;
    }

    @Override
    public boolean crearHistoria( FragmentManager fragmentManager,Historia historia) {
        mHistorias.add(historia);
        JSONObject result = postHistoriasJSON(fragmentManager,historia);
        //TODO: chequear resultado de la creacion
        return true;
    }

    @Override
    public boolean crearHistoriaCorta(FragmentManager fragmentManager, HistoriaCorta historia) {
        mHistoriasCortas.add(historia);
        JSONObject result = postHistoriasCortaJSON(fragmentManager,historia);
        //TODO: chequear resultado de la creacion
        return true;
    }

    @Override
    public boolean actReaction(FragmentManager fragmentManager, Historia historia, EmotionType emotion) {
        JSONObject result = postReactionJSON(fragmentManager,historia,emotion);
        //TODO: chequear resultado
        return true;
    }

    @Override
    public boolean actCommet(FragmentManager fragmentManager, Historia historia, String comment) {
        JSONObject result = postCommentJSON(fragmentManager,historia,comment);
        //TODO: chequear resultado
        return true;
    }

    private void getHistoriaCortaFile (final HistoriaCorta historia, int id,final int pos) {
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
                                if (pos == 1) {
                                    historia.setPicture(StringToBitMap(data.getString("mFile")));
                                }
                                else{
                                    historia.setPictureUsr(StringToBitMap(data.getString("mFile")));
                                }
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

    private void getHistoriaFile (final Historia historia, int id, final int pos) {
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
                                if (pos == 1){
                                    historia.setPicture(StringToBitMap(data.getString("mFile")));
                                }
                                else{
                                    historia.setPictureUsr(StringToBitMap(data.getString("mFile")));
                                }
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

    private JSONObject postHistoriasCortaJSON( final FragmentManager fragmentManager, HistoriaCorta historia) {
        final NetworkObject requestTokenObject = createHistoriaCorta(historia);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        resultado2 = null;
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    String asd = result.mResultValue;
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                resultado2 = resultToken.getJSONObject("data");
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
        return resultado2;
    }

    private JSONObject postHistoriasJSON( final FragmentManager fragmentManager, Historia historia) {
        final NetworkObject requestTokenObject = createHistoria(historia);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        resultado2 = null;
        mDownloading = false;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    String asd = result.mResultValue;
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                resultado2 = resultToken.getJSONObject("data");
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
        return resultado2;
    }

    private NetworkObject createHistoria(Historia historia) {
        String requestBody = createHistoriaObject(historia).toString();
        NetworkObject networkObject = new NetworkObject(STORYS, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        //networkObject.setMultipart();
        return networkObject;
    }

    private NetworkObject createHistoriaCorta(HistoriaCorta historia) {
        String requestBody = createHistoriaCortaObject(historia).toString();
        NetworkObject networkObject = new NetworkObject(STORYS, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        //networkObject.setMultipart();
        return networkObject;
    }

    private JSONObject createHistoriaObject(Historia historia) {
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            final String file = "file";
            requestHistoriaJsonObject.put(file, BitMapToString(historia.getPicture()));
            //requestHistoriaJsonObject.put(file,"hola");
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,"jpg");
            final String flash = "mFlash";
            requestHistoriaJsonObject.put(flash, false);
            final String privado = "mPrivate";
            requestHistoriaJsonObject.put(privado,false);
            final String latitude = "mLatitude";
            requestHistoriaJsonObject.put(latitude, String.valueOf(ServiceLocator.get(LocationService.class).getLocation(mContext).getLatitude()));
            final String logitude = "mLongitude";
            requestHistoriaJsonObject.put(logitude,String.valueOf(ServiceLocator.get(LocationService.class).getLocation(mContext).getLongitude()));
            final String title = "mTitle";
            requestHistoriaJsonObject.put(title,historia.getmTitulo());
            final String description = "mDescription";
            requestHistoriaJsonObject.put(description,historia.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
    }

    private JSONObject createHistoriaCortaObject(HistoriaCorta historia) {
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            final String file = "file";
            requestHistoriaJsonObject.put(file, BitMapToString(historia.getPicture()));
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,"jpg");
            final String flash = "mFlash";
            requestHistoriaJsonObject.put(flash, true);
            final String privado = "mPrivate";
            requestHistoriaJsonObject.put(privado,false);
            final String latitude = "mLatitude";
            requestHistoriaJsonObject.put(latitude, String.valueOf(ServiceLocator.get(LocationService.class).getLocation(mContext).getLatitude()));
            final String logitude = "mLongitude";
            requestHistoriaJsonObject.put(logitude,String.valueOf(ServiceLocator.get(LocationService.class).getLocation(mContext).getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
    }

    private NetworkObject getHistoriasNetworkObject() {
        NetworkObject networkObject = new NetworkObject(STORYS, HttpMethodType.GET);
        //networkObject.setContentType("application/json");
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        //networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        return networkObject;
    }

    private JSONObject postReactionJSON( final FragmentManager fragmentManager, Historia historia, EmotionType emotion) {
        final NetworkObject requestTokenObject = getReactionNetworkObject(historia,emotion);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        resultado2 = null;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    String asd = result.mResultValue;
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("201")) {
                                resultado2 = resultToken.getJSONObject("data");
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
        return resultado2;
    }

    private NetworkObject getReactionNetworkObject(Historia historia,EmotionType emotion) {
        String requestBody = crearReactionObject(emotion).toString();
        String requestUri = String.format(REACTION, historia.getID());
        NetworkObject networkObject = new NetworkObject(requestUri, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject crearReactionObject( EmotionType emotion){
        JSONObject requestHistoriaJsonObject = new JSONObject();

        try {
            final String reaction = "mReaction";
            requestHistoriaJsonObject.put(reaction,emotion.getEmotionServer());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
    }

    private JSONObject postCommentJSON( final FragmentManager fragmentManager, Historia historia, String comment) {
        final NetworkObject requestTokenObject = getCommentNetworkObject(historia, comment);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        resultado2 = null;
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    String asd = result.mResultValue;
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            String status = resultToken.getString("status");
                            if (status.equals("200")) {
                                resultado2 = resultToken.getJSONObject("data");
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
        return resultado2;
    }

    private NetworkObject getCommentNetworkObject(Historia historia, String comment) {
        String requestBody = crearCommentObject(comment).toString();
        String requestUri = String.format(COMMENT, historia.getID());
        NetworkObject networkObject = new NetworkObject(requestUri, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        networkObject.setFirebaseToken(ServiceLocator.get(NotificationService.class).getToken());
        List<String> responseHeaders = new ArrayList();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject crearCommentObject( String comment){
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            final String reaction = "mComment";
            requestHistoriaJsonObject.put(reaction,comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestHistoriaJsonObject;
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

    private String BitMapToString2(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
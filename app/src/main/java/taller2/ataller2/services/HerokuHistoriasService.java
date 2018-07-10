package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private FirebaseStorage storage;
    private StorageReference storageReference;



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
                    String file = obj.getString("mFile");
                    String userPicture = obj.getString("mProfilePicture");
                    String fileType = obj.getString("mFileType");
                    boolean isFlash = obj.getBoolean("mFlash");
                    String location = obj.getString("mLocation");
                    JSONArray reactions = obj.getJSONArray("mReactions");
                    JSONArray comentarios = obj.getJSONArray("mComments");

                    String nom = obj.getString("mFirstName");
                    String ape = obj.getString("mLastName");

                    if (isFlash){
                        HistoriaCorta historia = new HistoriaCorta();
                        historia.setStringUri(file);
                        mHistoriasCortas.add(historia);
                        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                        historia.setPicture(icon);
                        historia.setPictureUsr(icon);
                        historia.setType(fileType);
                    }
                    else{
                        Historia historia = new Historia(title);
                        historia.setStringUri(file);
                        historia.setNombre(nom + " " + ape );
                        historia.setUserID(userID);
                        historia.setID(historiaID);
                        historia.setDescription(desc);
                        historia.setUbicacion(location);
                        historia.setLatitud(lat);
                        historia.setLongitud(lng);
                        historia.setPictureUsr(userPicture);
                        historia.setType(fileType);

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
                                    String nombre = obj2.getString("mFirstName");
                                    String apellido = obj2.getString("mLastName");
                                    comentario.setNombre(nombre + " " + apellido);
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
                                    EmotionType emotionType  = EmotionType.getEmotionValueByString(emocion);
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
    public boolean crearHistoria( FragmentManager fragmentManager,Historia historia, OnCallback callback) {
        mHistorias.add(historia);
        JSONObject result = postHistoriasJSON(fragmentManager,historia,callback);
        //TODO: chequear resultado de la creacion
        return true;
    }

    @Override
    public boolean crearHistoriaCorta(FragmentManager fragmentManager, HistoriaCorta historia, OnCallback callback) {
        mHistoriasCortas.add(historia);
        JSONObject result = postHistoriasCortaJSON(fragmentManager,historia,callback);
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

    private JSONObject postHistoriasCortaJSON( final FragmentManager fragmentManager, HistoriaCorta historia, final OnCallback callback) {
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
                    callback.onFinish();
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

    private JSONObject postHistoriasJSON( final FragmentManager fragmentManager, Historia historia,final OnCallback callback) {
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
                    callback.onFinish();
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
            //requestHistoriaJsonObject.put(file, BitMapToString(historia.getPicture()));
            String asd = historia.getUri().toString();
            requestHistoriaJsonObject.put(file, historia.getUri().toString());
            //requestHistoriaJsonObject.put(file,"hola");
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,historia.getType());
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
            //requestHistoriaJsonObject.put(file, BitMapToString(historia.getPicture()));
            requestHistoriaJsonObject.put(file, historia.getUri().toString());
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,historia.getType());
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

    @Override
    public void uploadImageFromMemory(ImageView imageView, final OnCallbackImageUpload callback){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference storageRef = storage.getReference();
        String uniqueID = UUID.randomUUID().toString();
        uniqueID = uniqueID + ".jpg";
        StorageReference mountainsRef = storageRef.child(uniqueID);

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // TODO: mensaje de error de creacion de historia
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                callback.onFinish(downloadUri);
            }
        });
    }

    @Override
    public void uploadVideoFromMemory(VideoView videoView, final OnCallbackImageUpload callback){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference storageRef = storage.getReference();
        String uniqueID = UUID.randomUUID().toString();
        uniqueID = uniqueID + ".mp3";
        StorageReference mountainRef = storageRef.child(uniqueID);

        Uri mUri = null;
        try {
            Field mUriField = VideoView.class.getDeclaredField("mUri");
            mUriField.setAccessible(true);
            mUri = (Uri)mUriField.get(videoView);
        } catch(Exception e) {}

        UploadTask uploadTask = mountainRef.putFile(mUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    int b = 2;
            }
        }).addOnSuccessListener(
            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    callback.onFinish(downloadUri);
                }
            }).addOnProgressListener(
            new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
    }

}

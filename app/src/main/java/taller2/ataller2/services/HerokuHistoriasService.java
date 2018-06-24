package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;

public class HerokuHistoriasService implements HistoriasService {

    private static final String REACTION = "https://application-server-tdp2.herokuapp.com/story/%s/reaction";
    private static final String COMMENT = "https://application-server-tdp2.herokuapp.com/story/%s/comment";
    private static final String STORYS = "https://application-server-tdp2.herokuapp.com/story";
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

    private Context mContext;
    public HerokuHistoriasService(Context context){
        mContext = context;
    }

    @Override
    public void updateHistoriasData( Activity activity) {
        final NetworkObject requestTokenObject = getHistoriasNetworkObject();
        final NetworkFragment networkFragment = NetworkFragment.getInstance(activity.getFragmentManager(), requestTokenObject);
        mDownloading = false;
        resultado = null;
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
                                resultadoHistorias = resultToken.getJSONArray("data");
                                updateHistorias();
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

    public void updateHistorias() {
        mHistorias = new ArrayList<>();
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
                    String fileType = obj.getString("mFileType");
                    boolean hasFlash = obj.getBoolean("mFlash");
                    String location = obj.getString("mLocation");

                    Historia historia = new Historia(title);

                    historia.setUserID(userID);
                    historia.setID(historiaID);
                    historia.setDescription(desc);
                    historia.setUbicacion(location);
                    Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                    historia.setPicture(icon);
                    historia.setPictureUsr(icon);

                    mHistorias.add(historia);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateHistoriasCortasData(Activity activity) {

        mHistoriasCortas = new ArrayList<>();
        HistoriaCorta c1 = new HistoriaCorta();
        HistoriaCorta c2 = new HistoriaCorta();

        c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.river4));
        c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.river4));

        c1.setPictureUsr(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.diego));
        c2.setPictureUsr(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.elche));

        mHistoriasCortas.add(c1);
        mHistoriasCortas.add(c2);
    }

    @Override
    public List<Historia> getHistorias(Activity activity) {
        if (mHistorias == null) {
            updateHistoriasData(activity);
        }
        return mHistorias;
    }

    @Override
    public List<HistoriaCorta> getHistoriasCortas(Activity activity) {
        if (mHistoriasCortas == null) {
            updateHistoriasCortasData(activity);
        }
        return mHistoriasCortas;
    }

    @Override
    public Historia getHistoria(int index) {
        return mHistorias.get(index);
    }

    @Override
    public List<String> getUsers() {
        List<String> usuarios = new ArrayList<>();
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

    private JSONObject postHistoriasJSON( final FragmentManager fragmentManager, Historia historia) {
        final NetworkObject requestTokenObject = createHistoria(historia);
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

    private NetworkObject createHistoria(Historia historia) {
        String requestBody = createHistoriaObject(historia).toString();
        NetworkObject networkObject = new NetworkObject(STORYS, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject createHistoriaObject(Historia historia) {
        JSONObject requestHistoriaJsonObject = new JSONObject();
        try {
            //File f = new File(mContext.getCacheDir(), "hola");
//            try{
//                f.createNewFile();
//                Bitmap bitmap = historia.getPicture();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//                byte[] bitmapdata = bos.toByteArray();
//                FileOutputStream fos = new FileOutputStream(f);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            FileOutputStream fo = new FileOutputStream(f);

            Bitmap bmp   = historia.getPicture();
            int size     = bmp.getRowBytes() * bmp.getHeight();
            ByteBuffer b = ByteBuffer.allocate(size);

            bmp.copyPixelsToBuffer(b);

            byte[] bytes = new byte[size];

            try {
                b.get(bytes, 0, bytes.length);
            } catch (BufferUnderflowException e) {
                // always happens
            }

            // do something with byte[]
            final String file = "file";
            requestHistoriaJsonObject.put(file, b.array().toString());
            final String fileType = "mFileType";
            requestHistoriaJsonObject.put(fileType,"jpg");
            final String flash = "mFlash";
            requestHistoriaJsonObject.put(flash, false);
            final String privado = "mPrivate";
            requestHistoriaJsonObject.put(privado,false);
            final String latitude = "mLatitude";
            requestHistoriaJsonObject.put(latitude, "20.00");
            final String logitude = "mLongitude";
            requestHistoriaJsonObject.put(logitude,"21.00");
            final String title = "mTitle";
            requestHistoriaJsonObject.put(title,historia.getmTitulo());
            final String description = "mDescription";
            requestHistoriaJsonObject.put(description,historia.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }// catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}
        return requestHistoriaJsonObject;
    }

    private NetworkObject getHistoriasNetworkObject() {
        NetworkObject networkObject = new NetworkObject(STORYS, HttpMethodType.GET);
        //networkObject.setContentType("application/json");
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
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

    private NetworkObject getReactionNetworkObject(Historia historia,EmotionType emotion) {
        String requestBody = crearReactionObject(emotion).toString();
        String requestUri = String.format(REACTION, historia.getID());
        NetworkObject networkObject = new NetworkObject(requestUri, HttpMethodType.POST, requestBody);
        networkObject.setFacebookID(ServiceLocator.get(FacebookService.class).getFacebookID());
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        List<String> responseHeaders = new ArrayList<>();
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
        List<String> responseHeaders = new ArrayList<>();
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


}
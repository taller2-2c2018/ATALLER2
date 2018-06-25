package taller2.ataller2.services;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

//import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.R;
import taller2.ataller2.model.Amistad;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.facebook.LoginCallback;

public class HerokuAmistadesService implements AmistadesService {

    private static final String GET_FRIENDSHIP = "https://application-server-tdp2.herokuapp.com/user/friendship";
    private static final String AUTH_DATA = "data";
    private static final String AUTH_RESULT = "result";
    private boolean mDownloading = false;
    private String mAuthToken = null;
    JSONObject resultado;

    //private static Context context;
    private List<Amistad> mAmistades;
    private Context mContext;
    public HerokuAmistadesService(Context context){
        mContext = context;
    }

    @Override
    public void updateAmistadesData() {

        try {
            Activity activity = (Activity) mContext;
            JSONObject amistades = getAmistades(activity.getFragmentManager());

        }
        catch (Exception ex){

        }
        mAmistades = new ArrayList<>();
        Amistad c1 = new Amistad("Fernando Nitz");
        Amistad c2 = new Amistad("Manuel Ortiz");
        c1.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ringo));
        c2.setPicture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.markz));
        mAmistades.add(c1);
        mAmistades.add(c2);
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

    private JSONObject getAmistades( final FragmentManager fragmentManager) {
        final NetworkObject requestTokenObject = createGetRequestsNetworkObject();
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
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
                                resultado = resultToken.getJSONObject("data");
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
        return resultado;
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

}
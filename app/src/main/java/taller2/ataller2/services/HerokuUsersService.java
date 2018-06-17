package taller2.ataller2.services;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import taller2.ataller2.model.User;
import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.facebook.LoginCallback;

public class HerokuUsersService implements UsersService {

    List<User> mUsers = new ArrayList<>();
    private Context mContext;

    private static final String POST_AUTHENTICATE = "https://application-server-tdp2.herokuapp.com/profile/username";
    private static final String AUTH_RESULT = "status";
    private static final String AUTH_DATA = "data";
    private static final String AUTH_TOKEN = "token";

    private boolean mDownloading = false;
    private String mAuthToken = null;

    public HerokuUsersService(Context context){
        armarUsers();
        mContext = context;
    }

    @Override
    public List<User> getUsers() {
        armarUsers();
        return mUsers;
    }

    @Override
    public List<String> getUsersName() {
        armarUsers();
        List<String> names = new ArrayList<>();
        for (User user: mUsers) {
            names.add(user.getName());
        }
        return names;
    }

    private void armarUsers () {
        User user1 = new User("Fernando Nitz");
        User user2 = new User("Lionel Messi");
        User user3 = new User("Ringo Starr");
        User user4 = new User("Ramiro Funes Mori");
        User user5 = new User("Luca Proda");

        mUsers.add(user1);
        mUsers.add(user2);
        mUsers.add(user3);
        mUsers.add(user4);
        mUsers.add(user5);
    }

    // requestAuthToken(loginCalback, activity.getFragmentManager(), accessToken.getUserId());

    private void requestAuthToken(final LoginCallback loginCallback, final FragmentManager fragmentManager, String userId) {
        final NetworkObject requestTokenObject = createRequestTokenObject(userId);
        final NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        JSONObject resultToken = null;
                        try{
                            resultToken = new JSONObject(result.mResultValue);
                            JSONObject data = resultToken.getJSONObject("data");
                            mAuthToken = data.getString("token");
                        }
                        catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + result.mResultValue + "\"");
                        }
                        if (mAuthToken == null) {
                            loginCallback.onError("El usuario falló al ser autenticado");
                            mAuthToken = "";
                        } else {
                            loginCallback.onSuccess();
                        }
                    } else {
                        loginCallback.onError("El usuario falló al ser autenticado");
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

    private NetworkObject createRequestTokenObject(String userId) {
        String requestBody = createRequestTokenJson(userId).toString();
        NetworkObject networkObject = new NetworkObject(POST_AUTHENTICATE, HttpMethodType.POST, requestBody);
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
    }

    private JSONObject createRequestTokenJson(String userId) {
        JSONObject requestTokenJsonObject = new JSONObject();
        try {
            String asd = AccessToken.getCurrentAccessToken().getUserId();
            String accessToken = AccessToken.getCurrentAccessToken().getToken();
            String userToken = accessToken;
            final String userIdField = "facebookUserId";
            requestTokenJsonObject.put(userIdField, userId);
            final String userTokenField = "facebookAuthToken";
            requestTokenJsonObject.put(userTokenField,userToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestTokenJsonObject;
    }
}
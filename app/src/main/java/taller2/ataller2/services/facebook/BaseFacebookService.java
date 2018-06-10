package taller2.ataller2.services.facebook;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkFragment;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.R;
public class BaseFacebookService implements FacebookService {

    private static final String POST_REGISTER = "https://application-server-tdp2.herokuapp.com/user/register";
    private static final String POST_AUTHENTICATE = "https://application-server-tdp2.herokuapp.com/user/authenticate";
    private static final String AUTH_RESULT = "status";
    private static final String AUTH_DATA = "data";
    private static final String AUTH_TOKEN = "token";

    private CallbackManager mCallbackManager;
    private boolean mDownloading = false;
    private String mAuthToken = null;

    public BaseFacebookService() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public String getAuthToken() {
        if (mAuthToken == null) {
            Log.e("FacebookService", "Auth token is null");
        }
        return mAuthToken;
    }

    @Override
    public boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    public void loginWithAccesToken(Activity activity, LoginCallback loginCalback) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        registerToken(loginCalback, activity.getFragmentManager(), accessToken.getUserId());
        requestAuthToken(loginCalback, activity.getFragmentManager(), accessToken.getUserId());
    }

    @Override
    public void initializeLoginButton(Activity activity, LoginCallback loginCallback) {
        LoginButton loginButton = (LoginButton) activity.findViewById(R.id.login_button_facebook);
        setLoginButtonPermissions(loginButton);

        FacebookCallback<LoginResult> facebookCallback = createFacebookLoginCallback(loginCallback, activity.getFragmentManager());
        registerLoginButtonCallbacks(loginButton, facebookCallback);

    }

    @Override
    public void initializeLoginButton(Fragment fragment, LoginCallback loginCallback) {
        LoginButton loginButton = (LoginButton) fragment.getView().findViewById(R.id.login_button_facebook);
        setLoginButtonPermissions(loginButton);
        loginButton.setFragment(fragment);

        FacebookCallback<LoginResult> facebookCallback = createFacebookLoginCallback(loginCallback, fragment.getFragmentManager());
        registerLoginButtonCallbacks(loginButton, facebookCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackManager callbackManager = getCallbackManager();
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setLoginButtonPermissions(LoginButton loginButton) {
        loginButton.setReadPermissions("email");
    }

    private void registerLoginButtonCallbacks(LoginButton loginButton, FacebookCallback<LoginResult> loginCallback) {
        CallbackManager callbackManager = getCallbackManager();
        loginButton.registerCallback(callbackManager, loginCallback);
    }

    private FacebookCallback<LoginResult> createFacebookLoginCallback(final LoginCallback loginCallback, final FragmentManager fragmentManager) {
        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //login result tiene access token y otros valores relevantes a la sesion
                Log.i("LoginActivity", "Login to facebook");

                String userId = loginResult.getAccessToken().getUserId();
                requestAuthToken(loginCallback, fragmentManager, userId);
            }

            @Override
            public void onCancel() {
                Log.i("LoginActivity", "Cancel login");
                loginCallback.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("LoginActivity", "error logging in");
                loginCallback.onError("No se pudo conectar a Facebook. Verifique su conección a internet");
            }
        };
        return facebookCallback;
    }

    private void registerToken(final LoginCallback loginCallback, final FragmentManager fragmentManager, String userId) {
        NetworkObject requestTokenObject = createRegistrationTokenObject(userId);
        NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        mAuthToken = result.mResponseHeaders.get(AUTH_RESULT);
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

    private void requestAuthToken(final LoginCallback loginCallback, final FragmentManager fragmentManager, String userId) {
        NetworkObject requestTokenObject = createRequestTokenObject(userId);
        NetworkFragment networkFragment = NetworkFragment.getInstance(fragmentManager, requestTokenObject);
        if (!mDownloading) {
            mDownloading = true;
            networkFragment.startDownload(new DownloadCallback<NetworkResult>() {
                @Override
                public void onResponseReceived(NetworkResult result) {
                    if (result.mException == null) {
                        mAuthToken = result.mResponseHeaders.get(AUTH_DATA);
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

    private NetworkObject createRegistrationTokenObject(String userId) {
        String requestBody = createRequestTokenJson(userId).toString();
        NetworkObject networkObject = new NetworkObject(POST_REGISTER, HttpMethodType.POST, requestBody);
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(AUTH_RESULT);
        networkObject.setResponseHeaders(responseHeaders);
        return networkObject;
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

    private CallbackManager getCallbackManager() {
        if (mCallbackManager == null) {
            mCallbackManager = CallbackManager.Factory.create();
        }
        return mCallbackManager;
    }
}

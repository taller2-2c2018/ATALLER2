package taller2.ataller2.services.facebook;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import taller2.ataller2.services.CustomService;

public interface FacebookService extends CustomService {
    String getAuthToken();
    String getFacebookID();
    boolean isLoggedIn();
    void loginWithAccesToken (Activity activity, LoginCallback loginCalback);
    void initializeLoginButton(Activity activity, LoginCallback loginCallback);
    void initializeLoginButton(Fragment fragment, LoginCallback loginCallback);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}

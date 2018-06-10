package taller2.ataller2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;
import taller2.ataller2.services.facebook.LoginCallback;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private EditText mPasswordView, mUserName;
    private View mProgressView;
    private View mLoginFormView;
    private CallbackManager callbackManager; //PARA FACEBOOK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // declaring obejct of EditText control
        mUserName = (EditText) findViewById(R.id.txtUserName);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserName = mUserName.getText().toString();
                String Pwd = mPasswordView.getText().toString();
/*                if(UserName.equalsIgnoreCase("prueba") && Pwd.equals("prueba")){
                    Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(MainIntent);
                    Toast.makeText(LoginActivity.this,"You are Sign in Successfuly.", Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(LoginActivity.this,"Usuario o contraseña incorrecta", Toast.LENGTH_LONG).show();
                }*/
                Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(MainIntent);
                Toast.makeText(LoginActivity.this,"You are Sign in Successfuly.", Toast.LENGTH_LONG).show();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        callbackManager = CallbackManager.Factory.create();

        Button loginButton = (Button) findViewById(R.id.login_button_facebook);
        initializeLoginButton(loginButton);
        FacebookService facebookService = getFacebookService();
        if (facebookService.isLoggedIn()) {
            showProgress(true);
            facebookService.loginWithAccesToken(this, createLoginCallback());
        }
    }



    private void initializeLoginButton(Button mLoginButton) {
        final FacebookService facebookService = getFacebookService();
        facebookService.initializeLoginButton(this, createLoginCallback());

        Button loginButton = (Button) mLoginButton;
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!facebookService.isLoggedIn()) {
                    showProgress(true);
                }
            }
        });
    }

    private LoginCallback createLoginCallback() {
        return new LoginCallback() {
            public void onSuccess() {
                showProgress(false);
                Intent goToMainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(goToMainActivityIntent);
                finish();
            }

            @Override
            public void onCancel() {
                showProgress(false);
            }

            public void onError(String reason) {
                showProgress(false);
                showErrorToast(reason);
            }
        };
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FacebookService facebookService = getFacebookService();
        facebookService.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookService getFacebookService() {
        return ServiceLocator.get(FacebookService.class);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
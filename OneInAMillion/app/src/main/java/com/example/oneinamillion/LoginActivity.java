package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oneinamillion.Models.LoadingDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    TextInputLayout tfUsername;
    TextInputLayout tfPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private LoginButton facebook_login;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkIfLoggedIn();
        initializeViews();
        initializeFacebookSDK();
        setListeners();
    }

    private void initializeFacebookSDK() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG,loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"what",error);
            }
        });
    }

    private void setListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(LoginActivity.this,"No internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Logging in");
                if (isConnected()) {
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    loginUser(username, password);
                }
                else {
                    Toast.makeText(LoginActivity.this,"No internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfUsername.setErrorEnabled(false);
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfPassword.setErrorEnabled(false);
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void checkIfLoggedIn() {
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        facebook_login = findViewById(R.id.facebook_login);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        tfUsername = findViewById(R.id.tfUsername);
        tfPassword = findViewById(R.id.tfPassword);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String userID = accessToken.getUserId();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereMatches("Facebook_ID",userID);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                Log.i(TAG,users.toString());
                if (e != null) {
                    Log.e(TAG, "problem!", e);
                }
                else {
                    if (users.isEmpty()) {
                        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        ParseUser user = users.get(0);
                        String username = user.getUsername();
                        loginUser(username,"gianna");
                    }
                }
            }
        });
    }

    private void loginUser(final String username, String password) {
        if (username.matches("")) {
            tfUsername.setErrorEnabled(true);
            tfUsername.setError("Enter a username");
        }
        if (password.matches("")) {
            tfPassword.setErrorEnabled(true);
            tfPassword.setError("Enter a password");
        } else {
            final LoadingDialog dialog = new LoadingDialog(LoginActivity.this, "login");
            dialog.startLoadingDialog();
            Log.i(TAG, "Attempt to login");
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error logging in", e);
                        return;
                    }
                    dialog.dismissDialog();
                    goMainActivity();
                }
            });
        }
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
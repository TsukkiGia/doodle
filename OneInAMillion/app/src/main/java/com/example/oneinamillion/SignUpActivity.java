package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    EditText etFirstName;
    EditText etLastName;
    EditText etUsername;
    EditText etPassword;
    EditText etEmail;
    TextInputLayout tfFirstName;
    TextInputLayout tfLastName;
    TextInputLayout tfUsername;
    TextInputLayout tfPassword;
    TextInputLayout tfEmail;
    Button btnSignUp;
    public static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        setListeners();
        setUpIfLoggedIn();
    }

    private void setListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser(etUsername.getText().toString(),etPassword.getText().toString(),
                        etFirstName.getText().toString(),etLastName.getText().toString(),etEmail.getText().toString());
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
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfEmail.setErrorEnabled(false);
            }
        });
        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfFirstName.setErrorEnabled(false);
            }
        });
        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tfLastName.setErrorEnabled(false);
            }
        });
    }

    private void setUpIfLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        etFirstName.setText(first_name);
                        etLastName.setText(last_name);
                        etUsername.setText(String.format("%s%s", first_name.charAt(0), last_name));
                        Log.i(TAG,first_name+" "+last_name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields","first_name,last_name,id,picture");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }
    }

    private void initializeViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        tfFirstName = findViewById(R.id.tfFirstName);
        tfLastName = findViewById(R.id.tfLastName);
        tfUsername = findViewById(R.id.tfUsername);
        tfPassword = findViewById(R.id.tfPassword);
        tfEmail = findViewById(R.id.tfEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    private void signupUser(final String username, String password, String FirstName, String LastName, String Email) {
        Log.i(TAG, "Attempt to sign up");
        if (username.matches("")) {
            tfUsername.setErrorEnabled(true);
            tfUsername.setError("Enter a username");
        }
        if (password.matches("")) {
            tfPassword.setErrorEnabled(true);
            tfPassword.setError("Enter a password");
        }
        if (FirstName.matches("")) {
            tfFirstName.setErrorEnabled(true);
            tfFirstName.setError("Enter your first name");
        }
        if (LastName.matches("")) {
            tfLastName.setErrorEnabled(true);
            tfLastName.setError("Enter your last name");
        }
        if (Email.matches("")) {
            tfEmail.setErrorEnabled(true);
            tfEmail.setError("Enter an email");
        } else {
            ParseUser user = new ParseUser();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            if (isLoggedIn) {
                user.put("Facebook_ID", accessToken.getUserId());
            }
            user.setUsername(username);
            user.setPassword(password);
            user.put("FirstName", FirstName);
            user.put("LastName", LastName);
            user.put("AltEmail", Email);
            final LoadingDialog dialog = new LoadingDialog(SignUpActivity.this, "signup");
            dialog.startLoadingDialog();
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error signing up", e);
                        ParseUser.logOut();
                        Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        dialog.dismissDialog();
                        return;
                    }
                    goMainActivity();
                }
            });
        }
    }
    private void goMainActivity() {
        Intent i = new Intent(this, InterestActivity.class);
        i.putExtra("activity","signup");
        startActivity(i);
        finish();
    }
}
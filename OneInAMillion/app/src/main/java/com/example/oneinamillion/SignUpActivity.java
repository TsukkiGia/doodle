package com.example.oneinamillion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oneinamillion.Models.LoadingDialog;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
    Button btnSignUp;
    public static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser(etUsername.getText().toString(),etPassword.getText().toString(),
                        etFirstName.getText().toString(),etLastName.getText().toString(),etEmail.getText().toString());
            }
        });
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
    private void signupUser(final String username, String password, String FirstName, String LastName, String Email) {
        Log.i(TAG,"Attempt to sign up");
        ParseUser user = new ParseUser();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            user.put("Facebook_ID",accessToken.getUserId());
        }
        user.setUsername(username);
        user.setPassword(password);
        user.put("FirstName",FirstName);
        user.put("LastName",LastName);
        user.put("AltEmail",Email);
        final LoadingDialog dialog = new LoadingDialog(SignUpActivity.this,"signup");
        dialog.startLoadingDialog();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error signing up",e);
                    ParseUser.logOut();
                    return;
                }
                goMainActivity();
            }
        });
    }
    private void goMainActivity() {

        Intent i = new Intent(this, InterestActivity.class);
        i.putExtra("activity","signup");
        startActivity(i);
        finish();
    }
}
package com.example.exploreyourcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static String username;
    static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameField = (EditText) findViewById(R.id.loginUsernameField);
        final EditText passwordField = (EditText) findViewById(R.id.loginPasswordField);

        Button loginButton = (Button) findViewById(R.id.loginLoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that username is valid
                if (!Utilities.usernameValid(getApplicationContext(),
                        usernameField.getText().toString())) { return; }

                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                // If entered values are valid, send request to API
                // TODO: Replace with HTTP basic auth
                JSONObject requestParameters = new JSONObject();
//                try {
//                    requestParameters.put("username", usernameField.getText().toString());
//                    requestParameters.put("password", passwordField.getText().toString());
//                } catch (JSONException e) {
//                    Log.e("JSONException", e.getMessage());
//                }

                JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.GET,
                        "https://exploreyourcity.xyz/api/players/myself/", // TODO: Replace hardcoded api root
                        requestParameters,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("RequestResponse", response.toString());

                                // Store credentials on disk
                                SharedPreferences.Editor sp_editor = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE).edit();
                                if (response.has("username")) {
                                    sp_editor.putString("USERNAME", username);
                                    sp_editor.putString("PASSWORD", password);
                                    sp_editor.apply();
                                }
                                // TODO: Pass user ID to intent
                                Intent mapIntent = new Intent(getApplicationContext(),
                                        MapActivity.class);
                                startActivity(mapIntent);
                                finish();
                            }

                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Request Error", new String(error.networkResponse.data));
                                Utilities.makeToast(getApplicationContext(), "There was an error with your request");
                            }

                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                String credentials = LoginActivity.username + ":" + LoginActivity.password;
                                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                                return headers;
                            }
                        };
                RequestQueueSingleton.getInstance(getApplicationContext()).
                        addToRequestQueue(registerRequest);
            }
        });

        Button loginRegisterRedirectButton = (Button) findViewById(R.id.loginRegisterRedirectButton);
        loginRegisterRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }

}

package com.example.exploreyourcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // TODO: Make generic between LoginActivity and RegisterActivity
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 18;

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
                if (!usernameValid(usernameField.getText().toString())) { return; }

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

                                // TODO: Store credentials on disk
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
                                Toast.makeText(getApplicationContext(),
                                        "There was an error with your request",
                                        Toast.LENGTH_SHORT).show();
                            }

                        });
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

    // TODO: Make generic between LoginActivity and RegisterActivity
    private boolean usernameValid(String username) {
        if (username.length() > MAX_USERNAME_LENGTH) {
            Toast.makeText(getApplicationContext(),
                    "Username is too long",
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        if (username.length() < MIN_USERNAME_LENGTH) {
            Toast.makeText(getApplicationContext(),
                    "Username is too short",
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}

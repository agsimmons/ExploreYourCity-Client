package com.example.exploreyourcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameField = (EditText) findViewById(R.id.registerUsernameField);
        final EditText passwordField = (EditText) findViewById(R.id.registerPasswordField);
        final EditText passwordConfirmationField = (EditText) findViewById(R.id.registerPasswordConfirmationField);

        // Register Button
        Button registerButton = (Button) findViewById(R.id.registerRegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check that username is valid
                if (!Utilities.usernameValid(getApplicationContext(),
                        usernameField.getText().toString())) { return; }

                // Check that password matches confirmation
                if (!passwordsEqual(passwordField.getText().toString(),
                        passwordConfirmationField.getText().toString())) { return; }

                // If entered values are valid, send request to API
                JSONObject requestParameters = new JSONObject();
                try {
                    requestParameters.put("username", usernameField.getText().toString());
                    requestParameters.put("password", passwordField.getText().toString());
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                }

                JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST,
                        "https://exploreyourcity.xyz/api/users/", // TODO: Replace hardcoded api root
                        requestParameters,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("RequestResponse", response.toString());

                                // TODO: Store credentials on disk
                                // TODO: Pass new user ID to intent
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

                        });
                RequestQueueSingleton.getInstance(getApplicationContext()).
                        addToRequestQueue(registerRequest);
            }
        });

        Button registerLoginRedirectButton = (Button) findViewById(R.id.registerLoginRedirectButton);
        registerLoginRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private boolean passwordsEqual(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            Utilities.makeToast(getApplicationContext(), "Password does not match confirmation");
            return false;
        }
        return true;
    }
}

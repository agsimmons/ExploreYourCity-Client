package com.example.exploreyourcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SendFriendRequestActivity extends AppCompatActivity {

    private static final String TAG = "SendFriendRequest";

    EditText activity_send_friend_request_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friend_request);

        activity_send_friend_request_username= (EditText) findViewById(R.id.activity_send_friend_request_username);

        Button activity_send_friend_request_send_button = (Button) findViewById(R.id.activity_send_friend_request_send_button);
        activity_send_friend_request_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequest(activity_send_friend_request_username.getText().toString());
            }
        });

    }

    // TODO
    private void sendFriendRequest(final String recipient) {

        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("username", recipient);

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST,
                "https://exploreyourcity.xyz/api/requests/send/", // TODO: Replace hardcoded api root
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Friend request sent to " + recipient);
                        Utilities.makeToast(getApplicationContext(), "Friend request sent");

                        finish();
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error with sending friend request");
                        Utilities.makeToast(getApplicationContext(), "Error sending request");
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

                String creds = String.format("%s:%s", sp.getString("USERNAME", ""), sp.getString("PASSWORD", ""));
                String base64EncodedCredentials = Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }

            // Make it not error on an empty response
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(registerRequest);

    }
}

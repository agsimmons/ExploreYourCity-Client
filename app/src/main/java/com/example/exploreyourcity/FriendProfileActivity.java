package com.example.exploreyourcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.exploreyourcity.models.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FriendProfileActivity extends AppCompatActivity {

    private static final String TAG = "FriendProfileActivity";

    private int friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        friendId = getIntent().getIntExtra("FRIEND_ID", -1);
        if (friendId == -1) {
            Log.e(TAG, "FRIEND_ID preference was not passed");
            Utilities.makeToast(getApplicationContext(), "Invalid Friend!");
            finish();
        }

        Button unfriendButton = (Button) findViewById(R.id.friend_profile_activity_unfriend_button);
        unfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfriend();
            }
        });

        getFriendUsername();
        getFriendScore();
        getFriendNumCompletedMissions();
    }

    private void unfriend() {

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + friendId + "/remove_friend/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Unfriended");

                        finish();
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Issue with unfriending");
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

    private void getFriendUsername() {
        JsonObjectRequest friendUsernameRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + friendId + "/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());

                        Friend friend = new Friend(response);

                        TextView friendUsername = (TextView) findViewById(R.id.friend_profile_activity_username);
                        friendUsername.setText(friend.getUsername());
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
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
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(friendUsernameRequest);
    }

    private void getFriendScore() {
        JsonObjectRequest friendScoreRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + friendId + "/score/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int score = 0;

                        try {
                            score = response.getInt("score");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        TextView scoreView = (TextView) findViewById(R.id.friend_profile_activity_points);
                        scoreView.setText(score + " points");
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
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
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(friendScoreRequest);
    }

    private void getFriendNumCompletedMissions() {
        JsonArrayRequest friendNumCompletedMissionsRequest = new JsonArrayRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + friendId + "/completed_missions/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        TextView numObjectivesView = (TextView) findViewById(R.id.friend_profile_activity_nummissionscompleted);
                        numObjectivesView.setText("Missions Completed: " + response.length());
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
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
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(friendNumCompletedMissionsRequest);
    }

}

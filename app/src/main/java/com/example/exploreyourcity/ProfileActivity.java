package com.example.exploreyourcity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView pointsTextView;
    TextView missionsCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        // TODO: what are we going to do with this?
        ImageView profileImage = (ImageView) findViewById(R.id.profile_activity_profile_imageview);

        // Populate Username TextView
        TextView usernameTextView = (TextView) findViewById(R.id.profile_activity_username_textview);
        usernameTextView.setText(sp.getString("USERNAME", "Username"));

        // Populate Points TextView
        pointsTextView = (TextView) findViewById(R.id.profile_activity_points_textview);
        getPoints();

        // Populate Missions Comeplete Number textView
        missionsCompleteTextView = (TextView) findViewById(R.id.profile_activity_num_missions_complete_textview);
        getNumCompletedMissions();

        // TODO: make these do something
        Button completedMissionsButton = (Button) findViewById(R.id.profile_activity_completed_missions_button);
        Button friendsListButton = (Button) findViewById(R.id.profile_activity_friends_list_button);
        Button deleteAccountButton = (Button) findViewById(R.id.profile_activity_delete_account_button);

        Button logoutButton = (Button) findViewById(R.id.profile_activity_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear credentials from SharedPreferences
                SharedPreferences.Editor sp_editor = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE).edit();
                sp_editor.clear();
                sp_editor.apply();

                // Start RegisterActivity while clearing all other running activities
                Intent registerIntent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerIntent);
                finish();
            }
        });

    }

    private void getNumCompletedMissions() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        JsonArrayRequest missionListRequest = new JsonArrayRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/"+sp.getString("USER_ID", "0")+"/completed_missions/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", "getNumCompletedMissions(): completed missions: " +response.length());
                        missionsCompleteTextView.setText("Missions Completed: " +response.length());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Request Error", new String(error.networkResponse.data));
                        Utilities.makeToast(getApplicationContext(), "There was an error with your request");
                    }
                }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", sp.getString("USERNAME", ""), sp.getString("PASSWORD", ""));
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }

        };

        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(missionListRequest);
    }

    private void getPoints() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        JsonObjectRequest missionListRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + sp.getString("USER_ID", "-1") + "/score/",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RequestResponse", response.toString());

                        try {
                            pointsTextView.setText("Score: "+response.getString("score"));
                        } catch (JSONException e) {
                            Log.i("RequestResponse", " getScore(): couldn't parse score");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Request Error", new String(error.networkResponse.data));
                        Utilities.makeToast(getApplicationContext(), "There was an error with your request");
                    }
                }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", sp.getString("USERNAME", ""), sp.getString("PASSWORD", ""));
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }

        };

        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(missionListRequest);
    }
}
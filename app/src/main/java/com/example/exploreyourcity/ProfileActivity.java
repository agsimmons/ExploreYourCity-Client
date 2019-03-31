package com.example.exploreyourcity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
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

        // TODO: make this do something
        Button completedMissionsButton = (Button) findViewById(R.id.profile_activity_completed_missions_button);

        completedMissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completedMissionListIntent = new Intent(getApplicationContext(),
                        MissionListActivity.class);
                completedMissionListIntent.putExtra("MODE", "COMPLETED");
                startActivity(completedMissionListIntent);
            }
        });

        Button friendsListButton = (Button) findViewById(R.id.profile_activity_friends_list_button);

        // Setup the Delete Account Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete account?");
        builder.setMessage("Are you sure you want to delete your account? This is irreversible!");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
                logout();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        final AlertDialog deleteDialog = builder.create();

        Button deleteAccountButton = (Button) findViewById(R.id.profile_activity_delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteDialog.show();
            }
        });

        Button logoutButton = (Button) findViewById(R.id.profile_activity_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void logout() {
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

    private void deleteAccount() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        // Save credentials as the logout ends up happening first due to asyncronous volley
        final String username = sp.getString("USERNAME", "");
        final String password = sp.getString("PASSWORD", "");

        Log.i("ProfileActivity: ", "deleteAccount()");

        JsonObjectRequest missionListRequest = new JsonObjectRequest(Request.Method.DELETE,
                "https://exploreyourcity.xyz/api/users/remove_account/",
                null,
                null,
                null) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }

        };

        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(missionListRequest);
    }

    private void getNumCompletedMissions() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        JsonArrayRequest missionListRequest = new JsonArrayRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/" + sp.getString("PLAYER_ID", "0") + "/completed_missions/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("ProfileActivity: ", "getNumCompletedMissions(): completed missions: " +response.length());
                        missionsCompleteTextView.setText("Missions Completed: " +response.length());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProfileActivity: ", new String(error.networkResponse.data));
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
                "https://exploreyourcity.xyz/api/players/" + sp.getString("PLAYER_ID", "-1") + "/score/",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ProfileActivity: ", "getPoints()"+response.toString());

                        try {
                            pointsTextView.setText("Score: "+response.getString("score"));
                        } catch (JSONException e) {
                            Log.i("ProfileActivity: ", "getPoints(): couldn't parse score");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProfileActivity: ", new String(error.networkResponse.data));
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

package com.example.exploreyourcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.exploreyourcity.adapters.MissionAdapter;
import com.example.exploreyourcity.models.Mission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MissionDetailActivity extends AppCompatActivity {

    private int missionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);

        missionID = getIntent().getIntExtra("MISSION_ID", -1);
        // TODO: Error if missionID is -1, indicating that a mission ID was not passed

        // TODO: Attach functionality to add mission button

        // Fill out mission details
        getMissionDetails(missionID);
    }

    private void getMissionDetails(int missionID) {
        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/missions/" + Integer.toString(missionID) + "/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RequestResponse", response.toString());

                        Mission mission = new Mission(response);

                        TextView missionDetailTitle = (TextView) findViewById(R.id.mission_detail_title);
                        missionDetailTitle.setText(mission.getName());
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
                SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

                String creds = String.format("%s:%s", sp.getString("USERNAME", ""), sp.getString("PASSWORD", ""));
                String base64EncodedCredentials = Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(registerRequest);
    }
}

package com.example.exploreyourcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.exploreyourcity.adapters.MissionAdapter;
import com.example.exploreyourcity.models.Mission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MissionListActivity extends AppCompatActivity implements MissionAdapter.OnMissionListener {

    private static final String TAG = "MissionListActivity";

    private String mode;
    private String player_id;
    private int categoryId = -1;

    private ArrayList<Mission> missions;
    private RecyclerView recyclerView;
    private MissionAdapter missionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);

        mode = getIntent().getStringExtra("MODE");

        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);
        player_id = sp.getString("PLAYER_ID", "");

        getMissionList();
    }

    private void getMissionList() {

        String apiRoot = "https://exploreyourcity.xyz/api/"; // TODO: Replace hardcoded api root

        String endpoint = "";
        int method = 0;
        if (mode.equals("AVAILABLE")) {
            endpoint = apiRoot + "/missions/available/";
            method = Request.Method.POST;
            categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
            if (categoryId == -1) {
                finish();
            }
            TextView mission_list_available_mission_note = findViewById(R.id.mission_list_available_mission_note);
            mission_list_available_mission_note.setText("Available missions are sorted by proximity to you");
        } else if (mode.equals("CURRENT")) {
            endpoint = apiRoot + "/players/" + player_id + "/active_missions/";
            method = Request.Method.GET;
        } else if (mode.equals("COMPLETED")) {
            endpoint = apiRoot + "/players/" + player_id + "/completed_missions/";
            method = Request.Method.GET;
        } else {
            Log.e(TAG, "Invalid Mode");
            finish();
        }

        StringRequest missionListRequest = new StringRequest(method,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stringResponse) {
                        JSONArray response = null;
                        try {
                            response = new JSONArray(stringResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("RequestResponse", response.toString());

                        missions = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject missionData = null;

                            try {
                                missionData = (JSONObject) response.get(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Mission mission = new Mission(missionData);
                            if (categoryId != -1) {
                                if (mission.getCategory().getId() == categoryId) {
                                    missions.add(mission);
                                }
                            } else {
                                missions.add(mission);
                            }


                        }

                        Log.i("Deserialization", missions.toString());

                        initRecyclerView();

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
            public Map<String, String> getParams() {
                SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

                Map<String, String>  params = new HashMap<String, String>();

                params.put("latitude", "" + sp.getString("CURRENT_LATITUDE", "90.0"));
                params.put("longitude", "" + sp.getString("CURRENT_LONGITUDE", "90.0"));

                Log.d("CurrentLocation", "(" + sp.getString("CURRENT_LATITUDE", "0.0") + ", " + sp.getString("CURRENT_LONGITUDE", "0.0") + ")");

                return params;

            }

            @Override
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

    private void initRecyclerView() {
        // Add list elements to RecyclerView
        recyclerView = findViewById(R.id.mission_list_recycler_view);
        missionAdapter = new MissionAdapter(missions, this);
        recyclerView.setAdapter(missionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onMissionClick(int position) {
        // Switch to intent here
        Mission mission = missions.get(position);
        Log.d("Recycler", "Clicked on mission " + mission.toString());

        Intent missionDetailIntent = new Intent(getApplicationContext(), MissionDetailActivity.class);
        missionDetailIntent.putExtra("MISSION_ID", mission.getId());
        missionDetailIntent.putExtra("MODE", mode);
        startActivity(missionDetailIntent);
    }
}

package com.example.exploreyourcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.exploreyourcity.adapters.MissionAdapter;
import com.example.exploreyourcity.models.Mission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableMissionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_mission_list);

        getMissionList();
    }

    private void getMissionList() {

        JsonArrayRequest missionListRequest = new JsonArrayRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/missions/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", response.toString());

                        ArrayList<Mission> missions = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject missionData = null;

                            try {
                                missionData = (JSONObject) response.get(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Mission mission = new Mission(missionData);
                            missions.add(mission);

                        }

                        Log.i("Deserialization", missions.toString());

                        // Add list elements to RecyclerView
                        RecyclerView recyclerView = findViewById(R.id.available_mission_list_recycler_view);
                        MissionAdapter missionAdapter = new MissionAdapter(getApplicationContext(), missions);
                        recyclerView.setAdapter(missionAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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

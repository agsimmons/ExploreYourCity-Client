package com.example.exploreyourcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.exploreyourcity.adapters.MissionAdapter;
import com.example.exploreyourcity.adapters.ObjectiveAdapter;
import com.example.exploreyourcity.models.Mission;
import com.example.exploreyourcity.models.Objective;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MissionDetailActivity extends AppCompatActivity {

    private int missionID;

    private ArrayList<Objective> objectives;
    private RecyclerView recyclerView;
    private ObjectiveAdapter objectiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_detail);

        missionID = getIntent().getIntExtra("MISSION_ID", -1);
        // TODO: Error if missionID is -1, indicating that a mission ID was not passed

        // TODO: Make this function as either an add mission button or a drop mission button depending on where this intent was created from
        // Attach functionality to add mission button
        Button addMissionButton = (Button) findViewById(R.id.mission_detail_add_mission_button);
        addMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMission();
            }
        });

        // Fill out mission details
        getMissionDetails(missionID);

        // Get list of objectives belonging to selected mission, and add them to a recycler view
        getObjectives(missionID);

        // Fill out objective recycler view
//        initObjectiveRecyclerView();
    }

    private void getObjectives(int missionID) {
        JsonArrayRequest objectiveListRequest = new JsonArrayRequest("https://exploreyourcity.xyz/api/missions/" + Integer.toString(missionID) + "/objectives/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", response.toString());

                        objectives = new ArrayList<>();

                        // Add list of objectives to "objectives"
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                objectives.add(new Objective(response.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i("Deserialization", objectives.toString());

                        // TODO: Fill in the recycler view from here
                        recyclerView = findViewById(R.id.mission_detail_objective_recycler);
                        ObjectiveAdapter objectiveAdapter = new ObjectiveAdapter(objectives);
                        recyclerView.setAdapter(objectiveAdapter);
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
                addToRequestQueue(objectiveListRequest);
    }

    private void addMission() {

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/missions/" + Integer.toString(missionID) + "/start/", // TODO: Replace hardcoded api root
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RequestResponse", "Made a call to https://exploreyourcity.xyz/api/missions/" + Integer.toString(missionID) + "/start/");

                        terminateActivity(); // TODO: Refresh available mission list so user can't get back to this page
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Request Error", "Made a call to https://exploreyourcity.xyz/api/missions/" + Integer.toString(missionID) + "/start/");
                        Log.e("Request Error", error.getMessage());

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 409) {
                            Utilities.makeToast(getApplicationContext(), "This mission is already started");
                        } else {
                            Utilities.makeToast(getApplicationContext(), "There was an error with your request");
                        }


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

    public void terminateActivity() {
        finish();
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

                        // Set title
                        TextView missionDetailTitle = (TextView) findViewById(R.id.mission_detail_title);
                        missionDetailTitle.setText(mission.getName());

                        // Set point value
                        TextView missionDetailPoints = (TextView) findViewById(R.id.mission_detail_value);
                        missionDetailPoints.setText(Integer.toString(mission.getValue()) + " points");
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

//    private void initObjectiveRecyclerView() {
//        // Add list elements to RecyclerView
//        recyclerView = findViewById(R.id.available_mission_list_recycler_view);
//        objectiveAdapter = new ObjectiveAdapter(objectives, this);
//        recyclerView.setAdapter(objectiveAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//    }

//    @Override
//    public void onMissionClick(int position) {
//        // Switch to intent here
//        Mission mission = missions.get(position);
//        Log.d("Recycler", "Clicked on mission " + mission.toString());
//
//        Intent missionDetailIntent = new Intent(getApplicationContext(), MissionDetailActivity.class);
//        missionDetailIntent.putExtra("MISSION_ID", mission.getId());
//        startActivity(missionDetailIntent);
//    }
}

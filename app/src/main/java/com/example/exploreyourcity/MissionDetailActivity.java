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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.exploreyourcity.models.Mission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
}

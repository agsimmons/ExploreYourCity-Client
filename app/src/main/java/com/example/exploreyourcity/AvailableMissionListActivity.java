package com.example.exploreyourcity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.exploreyourcity.models.Mission;
import com.example.exploreyourcity.models.Category;

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

        ArrayList<Mission> missions = getMissionList();
        Log.d("Ayy", missions.toString());
    }

    private ArrayList<Mission> getMissionList() {

        // TODO: Specify Credentials

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

                        // TODO: Show list of elements

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
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "andrew", "3:&%bH+=tg6_u~8R'f@A-q*[gC{~k2+q");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }

        };

        RequestQueueSingleton.getInstance(getApplicationContext()).
                addToRequestQueue(missionListRequest);

        return new ArrayList<>(); // TODO
    }
}

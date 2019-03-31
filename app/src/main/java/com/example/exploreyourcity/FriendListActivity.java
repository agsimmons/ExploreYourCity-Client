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

import com.example.exploreyourcity.adapters.FriendListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity {

    private ArrayList<String> mFriendUsernames = new ArrayList<>();
    private ArrayList<String> mFriendPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        initAR();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.friendListRecyclerView);
        FriendListAdapter adapter = new FriendListAdapter(mFriendUsernames, mFriendPoints, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initAR() {

        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        JsonArrayRequest missionListRequest = new JsonArrayRequest(Request.Method.GET,
                "https://exploreyourcity.xyz/api/players/friends/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("FriendListActivity: ", "getting friend list");

                        try {
                            for(int i=0;i<response.length();i++) {
                                Log.i("FriendListActivity: ", "adding: "+response.getJSONObject(i).getString("username"));
                                mFriendUsernames.add(response.getJSONObject(i).getString("username"));
                                // TODO: actually get the points
                                mFriendPoints.add("X points");
                            }
                        } catch (JSONException e) {
                            Log.i("FriendListActivity: ", "JSONException!!"+ e.toString());
                        }

                        initRecyclerView();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FriendListActivity: ", new String(error.networkResponse.data));
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

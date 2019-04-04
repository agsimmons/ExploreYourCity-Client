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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.exploreyourcity.adapters.FriendRequestAdapter;
import com.example.exploreyourcity.models.FriendRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestsListActivity extends AppCompatActivity implements FriendRequestAdapter.OnFriendRequestListener {

    private static final String TAG = "RequestsListActivity";

    private String player_id;

    private ArrayList<FriendRequest> friendRequests;

    private RecyclerView recyclerView;
    private FriendRequestAdapter friendRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_list);

        initializeActivity();

        getRequests();
    }

    private void getRequests() {
        JsonArrayRequest objectiveListRequest = new JsonArrayRequest("https://exploreyourcity.xyz/api/requests/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(TAG, response.toString());

                        friendRequests = new ArrayList<>();

                        // Add list of friends to "friends"
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                friendRequests.add(new FriendRequest(response.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i("Deserialization", friendRequests.toString());

                        initRecyclerView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, new String(error.networkResponse.data));
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

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.frequests_list_recycler_view);
        friendRequestAdapter = new FriendRequestAdapter(friendRequests, this);
        recyclerView.setAdapter(friendRequestAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initializeActivity() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);
        player_id = sp.getString("PLAYER_ID", "");
    }

    @Override
    public void onFriendRequestClick(int position) {

        FriendRequest friendRequest = friendRequests.get(position);
        Log.d("Recycler", "Clicked on friend request " + friendRequest.toString());

        Intent friendRequestIntent = new Intent(getApplicationContext(), RequestListDetail.class);
        friendRequestIntent.putExtra("REQUEST_ID", friendRequest.getId());
        friendRequestIntent.putExtra("REQUEST_FROM_USERNAME", friendRequest.getFrom().getUsername());
        startActivity(friendRequestIntent);

    }
}

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
import com.example.exploreyourcity.adapters.FriendAdapter;
import com.example.exploreyourcity.adapters.ObjectiveAdapter;
import com.example.exploreyourcity.models.Friend;
import com.example.exploreyourcity.models.Objective;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsListActivity extends AppCompatActivity implements FriendAdapter.OnFriendListener {

    private String player_id;

    private ArrayList<Friend> friends;

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        initializeActivity();

        getFriends();
    }

    private void getFriends() {

        JsonArrayRequest objectiveListRequest = new JsonArrayRequest("https://exploreyourcity.xyz/api/players/friends/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", response.toString());

                        friends = new ArrayList<>();

                        // Add list of friends to "friends"
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                friends.add(new Friend(response.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i("Deserialization", friends.toString());

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
        recyclerView = findViewById(R.id.friends_list_recycler);
        friendAdapter = new FriendAdapter(friends, this);
        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initializeActivity() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);
        player_id = sp.getString("PLAYER_ID", "");
    }

    @Override
    public void onFriendClick(int position) {
        // Switch to intent here
        Friend friend = friends.get(position);
        Log.d("Recycler", "Clicked on friend " + friend.toString());

        Intent friendProfileIntent = new Intent(getApplicationContext(), FriendProfileActivity.class);
        friendProfileIntent.putExtra("FRIEND_ID", friend.getId());
        startActivity(friendProfileIntent);
    }
}

package com.example.exploreyourcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.exploreyourcity.adapters.FriendAdapter;
import com.example.exploreyourcity.adapters.RequestAdapter;

public class RequestsListActivity extends AppCompatActivity {

    private String player_id;

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_list);

        initializeActivity();

        getRequests();
    }

    private void getRequests() {
    }

    private void initRecyclerView() {

    }

    private void initializeActivity() {
        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);
        player_id = sp.getString("PLAYER_ID", "");
    }
}

package com.example.exploreyourcity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendFriendRequestActivity extends AppCompatActivity {

    EditText activity_send_friend_request_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friend_request);

        activity_send_friend_request_username= (EditText) findViewById(R.id.activity_send_friend_request_username);

        Button activity_send_friend_request_send_button = (Button) findViewById(R.id.activity_send_friend_request_send_button);
        activity_send_friend_request_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequest(activity_send_friend_request_username.getText().toString());
            }
        });

    }

    private void sendFriendRequest(String recipient) {
        // TODO
    }
}

package com.example.exploreyourcity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView profileImage = (ImageView) findViewById(R.id.profile_activity_profile_imageview);
        TextView usernameTextView = (TextView) findViewById(R.id.profile_activity_username_textview);
        TextView pointsTextView = (TextView) findViewById(R.id.profile_activity_points_textview);
        TextView missionsCompleteTextView = (TextView) findViewById(R.id.profile_activity_num_missions_complete_textview);
        Button completedMissionsButton = (Button) findViewById(R.id.profile_activity_completed_missions_button);
        Button friendsListButton = (Button) findViewById(R.id.profile_activity_friends_list_button);
        Button logoutButton = (Button) findViewById(R.id.profile_activity_logout_button);
        Button deleteAccountButton = (Button) findViewById(R.id.profile_activity_delete_account_button);


    }
}

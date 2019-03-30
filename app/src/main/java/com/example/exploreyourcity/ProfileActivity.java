package com.example.exploreyourcity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear credentials from SharedPreferences
                SharedPreferences.Editor sp_editor = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE).edit();
                sp_editor.clear();
                sp_editor.apply();

                // Start RegisterActivity while clearing all other running activities
                Intent registerIntent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerIntent);
                finish();
            }
        });

    }
}

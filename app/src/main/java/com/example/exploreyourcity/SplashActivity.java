package com.example.exploreyourcity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

// TODO: Wait for selection before closing the app

public class SplashActivity extends AppCompatActivity {

    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean hasPermissions = hasPermissions();

        if (hasPermissions) {
            // Check if credentials already exist. If they do, login automatically. If not, go to login.
            SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);
            if (sp.contains("USERNAME") && sp.contains("PASSWORD")){
                // This just assumes any username and password saved in sp work
                Intent mapIntent = new Intent(getApplicationContext(),
                        MapActivity.class);
                startActivity(mapIntent);
            } else {
                Intent registerIntent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(registerIntent);
            }
        }
        finish();
    }

    private boolean hasPermissions() {
        // Get required permissions from user
        int permissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        }

        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}

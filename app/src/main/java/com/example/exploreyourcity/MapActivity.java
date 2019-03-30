package com.example.exploreyourcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button availableMissionListButton = (Button) findViewById(R.id.map_activity_available_mission_list_button);
        availableMissionListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableMissionListIntent = new Intent(getApplicationContext(),
                        AvailableMissionListActivity.class);
                startActivity(availableMissionListIntent);
            }
        });

        Button profileButton = (Button) findViewById(R.id.map_activity_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(),
                        ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Create a marker for Wentworth
        LatLng test1 = new LatLng(42.3361, -71.0954);
        googleMap.addMarker(new MarkerOptions().position(test1).title("wentworth"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(test1));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

}

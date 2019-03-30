package com.example.exploreyourcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get location listener set up
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences.Editor sp_editor = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE).edit();
                sp_editor.putString("CURRENT_LATITUDE", "" + location.getLatitude());
                sp_editor.putString("CURRENT_LONGITUDE", "" +  location.getLongitude());
                sp_editor.apply();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Get Google map set up
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set onClickListenter for Available Missions button
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

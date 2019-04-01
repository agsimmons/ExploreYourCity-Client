package com.example.exploreyourcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.exploreyourcity.models.Objective;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeLocationServices();

        // Get Google map set up
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set onClickListener for Active Missions button
        Button activeMissionListButton = (Button) findViewById(R.id.map_activity_active_mission_list_button);
        activeMissionListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableMissionListIntent = new Intent(getApplicationContext(),
                        MissionListActivity.class);
                availableMissionListIntent.putExtra("MODE", "CURRENT");
                startActivity(availableMissionListIntent);
            }
        });

        // Set onClickListener for Available Missions button
        Button availableMissionListButton = (Button) findViewById(R.id.map_activity_available_mission_list_button);
        availableMissionListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent availableMissionListIntent = new Intent(getApplicationContext(),
                        MissionListActivity.class);
                availableMissionListIntent.putExtra("MODE", "AVAILABLE");
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

    private void initializeLocationServices() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences.Editor sp_editor = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE).edit();
                sp_editor.putString("CURRENT_LATITUDE", "" + location.getLatitude());
                sp_editor.putString("CURRENT_LONGITUDE", "" +  location.getLongitude());
                sp_editor.apply();
                
                updatePlayerLocation();
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

    }

    // TODO: Make this more efficient
    private void updatePlayerLocation() {
        // Don't update user location on map unless map is initialized
        if (gMap == null) {
            return;
        }

        SharedPreferences sp = getSharedPreferences("EYCPrefs", Context.MODE_PRIVATE);

        gMap.clear();

        // Add user location to map and move camera to focus on it
        LatLng player = new LatLng(
                Double.parseDouble(sp.getString("CURRENT_LATITUDE", "90.0")),
                Double.parseDouble(sp.getString("CURRENT_LONGITUDE", "90.0")));
        gMap.addMarker(new MarkerOptions().position(player).title("Current Location"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(player));
        gMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        // Add active objectives to map
        JsonArrayRequest activeObjectiveListRequest = new JsonArrayRequest("https://exploreyourcity.xyz/api/players/" + sp.getString("PLAYER_ID", "") + "/active_objectives/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", response.toString());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Objective objective = new Objective(response.getJSONObject(i));
                                LatLng objectiveLatLng = new LatLng(objective.getLatitude(), objective.getLongitude());
                                if (Utilities.getDistanceBetween(
                                    objective.getLatitude(),
                                    objective.getLongitude(),
                                    playerLatitude,
                                    playerLongitude) <= Constants.OBJECTIVE_COMPLETE_RADIUS) {

                                    completeObjective(objective);

                                    gMap.addMarker(new MarkerOptions().position(objectiveLatLng).title("Complete: " + objective.getName()));
                                } else {
                                    gMap.addMarker(new MarkerOptions().position(objectiveLatLng).title("Incomplete: " + objective.getName()));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                addToRequestQueue(activeObjectiveListRequest);

        // Add completed objectives to map
        JsonArrayRequest completedObjectiveListRequest = new JsonArrayRequest("https://exploreyourcity.xyz/api/players/" + sp.getString("PLAYER_ID", "") + "/completed_objectives/",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RequestResponse", response.toString());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Objective objective = new Objective(response.getJSONObject(i));
                                LatLng objectiveLatLng = new LatLng(objective.getLatitude(), objective.getLongitude());
                                gMap.addMarker(new MarkerOptions().position(objectiveLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.objective_complete)).title("Completed: " + objective.getName()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                addToRequestQueue(completedObjectiveListRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Create a marker for Wentworth
        LatLng test1 = new LatLng(42.3361, -71.0954);
        googleMap.addMarker(new MarkerOptions().position(test1).title("Wentworth"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(test1));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

}

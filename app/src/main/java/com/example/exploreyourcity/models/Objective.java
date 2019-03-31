package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Objective {
    private int id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;

    public Objective(int id, String name, Double latitude, Double longitude, String formattedAddress) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.formattedAddress = formattedAddress;
    }

    public Objective(JSONObject objective) {
        try {
            this.id = objective.getInt("id");
            this.name = objective.getString("name");
            this.latitude = objective.getDouble("latitude");
            this.longitude = objective.getDouble("longitude");
            this.formattedAddress = objective.getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }


    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.formattedAddress;
    }
}

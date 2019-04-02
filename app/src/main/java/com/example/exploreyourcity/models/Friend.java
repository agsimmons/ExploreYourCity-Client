package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

    private int id;
    private String username;

    public Friend(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public Friend(JSONObject friend) {
        try {
            this.id = friend.getInt("id");
            this.username = friend.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return "Friend(" + this.username + ")";
    }

}

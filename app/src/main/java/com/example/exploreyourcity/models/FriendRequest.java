package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendRequest {
    private int id;
    private int from;

    public FriendRequest(int id, int from) {
        this.id = id;
        this.from = from;
    }

    public FriendRequest(JSONObject friendRequest) {
        try {
            this.id = friendRequest.getInt("id");
            this.from = friendRequest.getInt("request_from");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return this.id;
    }

    public int getFrom() {
        return this.from;
    }

    @Override
    public String toString() {
        return "FriendRequest(" + this.id + ")";
    }
}

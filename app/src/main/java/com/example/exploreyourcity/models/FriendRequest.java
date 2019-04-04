package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendRequest {
    private int id;
    private Friend from;

    public FriendRequest(int id, Friend from) {
        this.id = id;
        this.from = from;
    }

    public FriendRequest(JSONObject friendRequest) {
        try {
            this.id = friendRequest.getInt("id");
            this.from = new Friend(friendRequest.getJSONObject("request_from"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return this.id;
    }

    public Friend getFrom() {
        return this.from;
    }

    @Override
    public String toString() {
        return "FriendRequest(" + this.id + ")";
    }
}

package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Mission {
    private int id;
    private Category category;

    public Mission(int id, Category category) {
        this.id = id;
        this.category = category;
    }

    public Mission(JSONObject mission) {
        try {
            this.id = mission.getInt("id");
            this.category = new Category(mission.getJSONObject("category"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.category.getName() + " " + this.id;
    }

    public int getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    @Override
    public String toString() {
        return "Mission(" + this.id + "), " + this.category.toString();
    }
}

package com.example.exploreyourcity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(JSONObject category) {
        try {
            this.id = category.getInt("id");
            this.name = category.getString("name");
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

    @Override
    public String toString() {
        return "Category(" + this.id + ") - " + this.name;
    }
}

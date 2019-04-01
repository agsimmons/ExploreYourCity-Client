package com.example.exploreyourcity;

import android.content.Context;
import android.widget.Toast;

public class Utilities {

    public static void makeToast(Context context, String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    public static boolean usernameValid(Context context, String username) {
        if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            makeToast(context, "Username is too long");
            return false;
        }

        if (username.length() < Constants.MIN_USERNAME_LENGTH) {
            makeToast(context, "Username is too short");
            return false;
        }

        return true;
    }

    // Returns distance in meters between two coordinates
    public static Double getDistanceBetween(Double latitude1, Double longitude1, Double latitude2, Double longitude2) {
        Double lat1Rad = Math.toRadians(latitude1);
        Double lon1Rad = Math.toRadians(longitude1);
        Double lat2Rad = Math.toRadians(latitude2);
        Double lon2Rad = Math.toRadians(longitude2);

        // Haversine
        Double dlon = lon2Rad - lon1Rad;
        Double dlat = lat2Rad - lat1Rad;
        Double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dlon / 2), 2);
        Double c = 2 * Math.asin(Math.sqrt(a));
        Double distance = c * Constants.EARTH_RADIUS * 1000;

        return distance;
    }
}

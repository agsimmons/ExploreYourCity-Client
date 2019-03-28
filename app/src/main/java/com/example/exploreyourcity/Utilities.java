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

}

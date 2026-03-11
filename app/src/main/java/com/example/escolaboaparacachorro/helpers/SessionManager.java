package com.example.escolaboaparacachorro.helpers;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_TUTOR_UID = "tutorUid";
    private static final String KEY_DOG_ID = "dogId";

    public SessionManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String tutorUid, String email, String dogId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TUTOR_UID, tutorUid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOG_ID, dogId);
        editor.apply();
    }

    public String getDogId() {
        return sharedPreferences.getString(KEY_DOG_ID, null);
    }

    public String getTutorUid() {
        return sharedPreferences.getString(KEY_TUTOR_UID, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
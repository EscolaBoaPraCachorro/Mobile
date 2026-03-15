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

    public void createLoginSession(Long tutorUid, String email, Long dogId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_TUTOR_UID, tutorUid != null ? tutorUid : -1L);
        editor.putString(KEY_EMAIL, email);
        editor.putLong(KEY_DOG_ID, dogId != null ? dogId : -1L);
        editor.apply();
    }

    public Long getDogId() {
        long id = sharedPreferences.getLong(KEY_DOG_ID, -1L);
        return (id == -1L) ? null : id;
    }

    public Long getTutorUid() {
        long id = sharedPreferences.getLong(KEY_TUTOR_UID, -1L);
        return (id == -1L) ? null : id;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
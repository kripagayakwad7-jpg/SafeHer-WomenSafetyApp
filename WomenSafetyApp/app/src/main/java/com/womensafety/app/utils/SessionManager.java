package com.womensafety.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "WomenSafetySession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_SHAKE_SOS = "shake_sos_enabled";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(Long userId, String token, String name, String email, String phone) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_TOKEN, token)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PHONE, phone)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getLong(KEY_USER_ID, -1) != -1;
    }

    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, ""); }
    public String getName() { return prefs.getString(KEY_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }

    public boolean isShakeSOSEnabled() {
        return prefs.getBoolean(KEY_SHAKE_SOS, true);
    }

    public void setShakeSOSEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SHAKE_SOS, enabled).apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}

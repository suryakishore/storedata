package com.storedata.com;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String REGISTER_ID = "register_id";
    private String USER_NAME = "user_name";
    private String IS_LOGIN = "user_is_login";
    private String PUSH_KEY = "push_key";
    private String USER = "user";

    public SessionManager(Context context) {
        String STOREDATA = "STORE_DATA";

        /**
         * Mod is private.
         */

        int PREFERENCE_MODE = 0;
        sharedPreferences = context.getSharedPreferences(STOREDATA, PREFERENCE_MODE);
        editor = sharedPreferences.edit();
        editor.commit();

    }

    public void setRegister_ID(String register_id) {
        editor.putString(REGISTER_ID, register_id);
        editor.commit();
        // Utility.printLog("FCM ID",register_id);
    }

    public String getRegister_ID() {
        return sharedPreferences.getString(REGISTER_ID, "");
    }

    public void setPUSH_KEY(String push_key) {
        editor.putString(PUSH_KEY, push_key);
        editor.commit();
        // Utility.printLog("FCM ID",register_id);
    }

    public String getPUSH_KEY() {
        return sharedPreferences.getString(PUSH_KEY, "");
    }


    public void setUserLoginStatus(boolean is_login) {
        editor.putBoolean(IS_LOGIN, is_login);
        editor.commit();
    }

    public boolean getUserLoginStatus() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public void setUSER_NAME(String user_name) {
        editor.putString(USER_NAME, user_name);
        editor.commit();
    }

    public String getUSER_NAME() {
        return sharedPreferences.getString(USER_NAME, "");
    }


    public void setUserEmail(String user) {
        editor.putString(USER, user);
        editor.commit();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER, "");
    }



}

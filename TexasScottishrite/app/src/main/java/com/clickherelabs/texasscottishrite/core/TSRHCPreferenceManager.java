package com.clickherelabs.texasscottishrite.core;

/**
 * Created by armando_contreras on 7/20/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by armando_contreras on 7/19/15.
 */
public class TSRHCPreferenceManager {
    public static final String TAG = TSRHCPreferenceManager.class.getSimpleName();

    private static final String TSRHC_PREF_MANAGER = "TSRHC_PREF_MANAGER";
    private static final String PREF_FCM_TOKEN = "PREF_FCM_TOKEN";
    private static final String PREF_SUBSCRIBED = "PREF_SUBSCRIBED";
    private SharedPreferences mSharedPreferences;


    private TSRHCPreferenceManager() {
    }

    private static TSRHCPreferenceManager instance = null;

    public static TSRHCPreferenceManager getInstance() {
        if (instance == null) {
            instance = new TSRHCPreferenceManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        mSharedPreferences = context.getSharedPreferences(TSRHC_PREF_MANAGER, 0);
    }

    public void setFcmToken(String token) {
        Log.d(TAG, "[setAcceptedTerms]");
        mSharedPreferences.edit().putString(PREF_FCM_TOKEN, token).commit();

    }

    public String getFcmToken() {
        Log.d(TAG, "[setAcceptedTerms]");
        return mSharedPreferences.getString(PREF_FCM_TOKEN, null);
    }

    public boolean hasSubscribed() {
        Log.d(TAG, "[setAcceptedTerms]");
        return mSharedPreferences.getBoolean(PREF_SUBSCRIBED, false);

    }

    public void setSubscribed(boolean subscribed) {
        Log.d(TAG, "[setAcceptedTerms]");
        mSharedPreferences.edit().putBoolean(PREF_FCM_TOKEN, subscribed).commit();
    }
}


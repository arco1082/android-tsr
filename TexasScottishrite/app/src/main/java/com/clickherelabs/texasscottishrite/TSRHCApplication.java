package com.clickherelabs.texasscottishrite;

import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.View;

import com.clickherelabs.texasscottishrite.core.TSRHCPreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by armando_contreras on 7/21/16.
 */
public class TSRHCApplication extends Application {
    private static String TAG = TSRHCApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        TSRHCPreferenceManager.getInstance().initialize(this);

    }

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/
}

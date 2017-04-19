package com.clickherelabs.texasscottishrite.core;

/**
 * Created by armando_contreras on 7/21/16.
 */

import android.app.IntentService;
import android.content.Intent;

import com.clickherelabs.texasscottishrite.R;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;

/**
 * While subscribed in the background, this service shows a persistent notification with the
 * current set of messages from nearby beacons. Nearby launches this service when a message is
 * found or lost, and this service updates the notification, then stops itself.
 */
public class BackgroundSubscribeIntentService extends IntentService {
    private static final int MESSAGES_NOTIFICATION_ID = 1;
    private static final int NUM_MESSAGES_IN_NOTIFICATION = 4;
    private static final String TAG = BackgroundSubscribeIntentService.class.getSimpleName();

    public BackgroundSubscribeIntentService() {
        super("BackgroundSubscribeIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        if (intent != null) {
            Nearby.Messages.handleIntent(intent, new MessageListener() {
                @Override
                public void onFound(Message message) {
                    Log.d(TAG, "onFound: "+message.getType().toString());
                    String messageString = new String(message.getContent());
                    Log.d(TAG, "onFound: "+messageString);
                    Utils.saveFoundMessage(getApplicationContext(), message);
                    updateNotification();
                }

                @Override
                public void onLost(Message message) {
                    Utils.removeLostMessage(getApplicationContext(), message);
                    updateNotification();
                }
            });
        }
    }

    private void updateNotification() {
        List<String> messages = Utils.getCachedMessages(getApplicationContext());
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent launchIntent = new Intent(getApplicationContext(), NearbyActivity.class);
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentTitle = getContentTitle(messages);
        String contentText = getContentText(messages);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_tsrhc_notification)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setOngoing(true)
                .setContentIntent(pi);
        notificationManager.notify(MESSAGES_NOTIFICATION_ID, notificationBuilder.build());
    }

    private String getContentTitle(List<String> messages) {
        switch (messages.size()) {
            case 0:
                return getResources().getString(R.string.scanning);
            case 1:
                return getResources().getString(R.string.one_message);
            default:
                return getResources().getString(R.string.many_messages, messages.size());
        }
    }

    private String getContentText(List<String> messages) {
        String newline = System.getProperty("line.separator");
        if (messages.size() < NUM_MESSAGES_IN_NOTIFICATION) {
            return TextUtils.join(newline, messages);
        }
        return TextUtils.join(newline, messages.subList(0, NUM_MESSAGES_IN_NOTIFICATION)) +
                newline + "&#8230;";
    }
}
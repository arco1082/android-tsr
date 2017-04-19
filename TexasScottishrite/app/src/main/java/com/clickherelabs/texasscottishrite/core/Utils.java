package com.clickherelabs.texasscottishrite.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Created by armando_contreras on 7/21/16.
 */
public class Utils {
    public static final String KEY_CACHED_MESSAGES = "cached-messages";

    /**
     * Fetches message strings stored in {@link SharedPreferences}.
     *
     * @param context The context.
     * @return  A list (possibly empty) containing message strings.
     */
    public static List<String> getCachedMessages(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        String cachedMessagesJson = sharedPrefs.getString(KEY_CACHED_MESSAGES, "");
        if (TextUtils.isEmpty(cachedMessagesJson)) {
            return Collections.emptyList();
        } else {
            Type type = new TypeToken<List<String>>() {}.getType();
            return new Gson().fromJson(cachedMessagesJson, type);
        }
    }

    /**
     * Saves a message string to {@link SharedPreferences}.
     *
     * @param context The context.
     * @param message The Message whose payload (as string) is saved to SharedPreferences.
     */
    public static void saveFoundMessage(Context context, Message message) {
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        Set<String> cachedMessagesSet = new HashSet<>(cachedMessages);
        String messageString = new String(message.getContent());
        if (!cachedMessagesSet.contains(messageString)) {
            cachedMessages.add(0, new String(message.getContent()));
            getSharedPreferences(context)
                    .edit()
                    .putString(KEY_CACHED_MESSAGES, new Gson().toJson(cachedMessages))
                    .apply();
        }
    }

    /**
     * Removes a message string from {@link SharedPreferences}.
     * @param context The context.
     * @param message The Message whose payload (as string) is removed from SharedPreferences.
     */
    public static void removeLostMessage(Context context, Message message) {
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        cachedMessages.remove(new String(message.getContent()));
        getSharedPreferences(context)
                .edit()
                .putString(KEY_CACHED_MESSAGES, new Gson().toJson(cachedMessages))
                .apply();
    }

    /**
     * Gets the SharedPReferences object that is used for persisting data in this application.
     *
     * @param context The context.
     * @return The single {@link SharedPreferences} instance that can be used to retrieve and modify
     *         values.
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
    }
}

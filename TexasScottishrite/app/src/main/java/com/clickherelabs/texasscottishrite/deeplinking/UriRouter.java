package com.clickherelabs.texasscottishrite.deeplinking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/20/16.
 */
public class UriRouter {
    private static final String TAG = UriRouter.class.getSimpleName();

    List<IUriHandler> mUriHandlerList;

    /**
     * Singleton private constructor.
     */
    private UriRouter() {
        mUriHandlerList = new ArrayList<>();
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final UriRouter instance = new UriRouter();
    }

    public static UriRouter getInstance() {
        return SingletonHolder.instance;
    }


    /**
     * Add more URI handlers as need be.  Currently, only home screen, login screen, rso screen, change zipcode screen and more services screen
     */

    public void initHandlerList(List<IUriHandler> list) {
        mUriHandlerList.clear();
        mUriHandlerList.addAll(list);
        mUriHandlerList.add(new HomeUriHandler());
    }

    public IUriHandler getHandlerForUri(String uriString) {
        Uri uri = Uri.parse(uriString);
        if (uri != null) {
            return getHandlerForUri(uri);
        }
        return null;
    }

    public IUriHandler getHandlerForUri(Uri uri) {
        if (uri != null) {
            for (IUriHandler uriHandler : mUriHandlerList) {
                if (uriHandler.canHandleUri(uri)) {
                    return uriHandler;
                }
            }
        }
        return null;
    }

    /**
     * Check if a URI is white listed to be handled for deep linking
     */

    public boolean canHandleUri(Uri uri) {
        return getHandlerForUri(uri) != null;
    }

    public boolean handleUri(FragmentActivity activity, String uriString) {
        if (uriString == null) return false;
        return handleUri(activity, Uri.parse(uriString));
    }

    public boolean handleUri(FragmentActivity activity, Uri uri) {
        IUriHandler uriHandler = getHandlerForUri(uri);
        return handleUri(activity, uriHandler, uri);
    }

    public boolean handleUri(FragmentActivity activity, IUriHandler uriHandler, Uri uri) {
        if (uriHandler != null) {
            return uriHandler.handleUri(activity, uri);
        }
        return launchUnhandledUri(activity, uri);
    }

    private boolean launchUnhandledUri(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        try {
            activity.startActivity(intent);
            return true;
        } catch (Throwable t) {
            Log.e(TAG, "Device cannot launch Uri: " + uri.toString());
        }
        return false;
    }

}

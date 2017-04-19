package com.clickherelabs.texasscottishrite.deeplinking;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.clickherelabs.texasscottishrite.activity.CategorySelectionActivity;
import com.clickherelabs.texasscottishrite.activity.SignInActivity;

/**
 * Created by armando_contreras on 7/20/16.
 */
public class HomeUriHandler implements IUriHandler {
    private static final String TAG = HomeUriHandler.class.getSimpleName();

    /*
     * CUSTOMS_SCHEME://www.tsrhc.com/home
     *
     * @param uri - URI that gets passed in when launching UriRouter
     */

    @Override
    public boolean canHandleUri(Uri uri) {
        Log.d(TAG, "canHandleUri");
        return uri.toString().contains("home");
    }

    /**
     * @param activity - Activity to be used for Context
     * @param uri - URI that gets passed in from custom scheme to retrieve data
     */

    @Override
    public boolean handleUri(FragmentActivity activity, Uri uri) {
        Log.d(TAG, "handleUri");
        Intent intent = new Intent(activity, SignInActivity.class);
        activity.startActivity(intent);

        return true;
    }

}

package com.clickherelabs.texasscottishrite.deeplinking;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;

/**
 * Created by armando_contreras on 7/20/16.
 */
public interface IUriHandler {
    /**
     * @param uri - URI that gets passed in from custom scheme and check if its valid
     */
    public boolean canHandleUri(Uri uri);

    public boolean handleUri(FragmentActivity activity, Uri uri);
}


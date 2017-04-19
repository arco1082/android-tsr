package com.clickherelabs.texasscottishrite.deeplinking;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clickherelabs.texasscottishrite.R;

import java.util.ArrayList;

/**
 * Created by armando_contreras on 7/20/16.
 */
public class DeepLinkActivity extends AppCompatActivity {
    private static String TAG = DeepLinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "DeepLinkActivity");
        setContentView(R.layout.activity_sign_in);
        Uri uri = getIntent().getData();
        if (uri != null) {
            ArrayList list = new ArrayList<>();
            UriRouter.getInstance().initHandlerList(list);
            UriRouter.getInstance().handleUri(this, uri);
        }
        finish();
    }


}

package com.clickherelabs.texasscottishrite.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clickherelabs.texasscottishrite.BuildConfig;
import com.clickherelabs.texasscottishrite.R;
import com.clickherelabs.texasscottishrite.core.BackgroundSubscribeIntentService;
import com.clickherelabs.texasscottishrite.core.TSRHCPreferenceManager;
import com.clickherelabs.texasscottishrite.core.Utils;
import com.clickherelabs.texasscottishrite.fragment.ActivityFeedFragment;
import com.clickherelabs.texasscottishrite.fragment.CategorySelectionFragment;
import com.clickherelabs.texasscottishrite.fragment.VideoListFeedFragment;
import com.clickherelabs.texasscottishrite.helper.ApiLevelHelper;
import com.clickherelabs.texasscottishrite.helper.PreferencesHelper;
import com.clickherelabs.texasscottishrite.model.Player;
import com.clickherelabs.texasscottishrite.model.VideoList;
import com.clickherelabs.texasscottishrite.persistence.TopekaDatabaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Messages;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/28/16.
 */
public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String EXTRA_PLAYER = "player";
    private static final String TAG = HomeActivity.class.getSimpleName();


    private static final int PERMISSIONS_REQUEST_CODE = 1111;

    private static final String KEY_SUBSCRIBED = "subscribed";
    protected Toolbar mToolbar;
    private List<DrawerLayout.DrawerListener> mDrawerListeners = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    private GoogleApiClient mGoogleApiClient;

    private RelativeLayout mContainer;

    private boolean mSubscribed = false;

    private List<String> mNearbyMessagesList = new ArrayList<>();

    public static void start(Activity activity, Player player, ActivityOptionsCompat options) {
        Intent starter = getStartIntent(activity, player);
        ActivityCompat.startActivity(activity, starter, options.toBundle());
    }

    public static void start(Context context, Player player) {
        Intent starter = getStartIntent(context, player);
        context.startActivity(starter);
    }

    @NonNull
    static Intent getStartIntent(Context context, Player player) {
        Intent starter = new Intent(context, HomeActivity.class);
        starter.putExtra(EXTRA_PLAYER, player);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feeds);
        Player player = getIntent().getParcelableExtra(EXTRA_PLAYER);
        if (!PreferencesHelper.isSignedIn(this) && player != null) {
            PreferencesHelper.writeToPreferences(this, player);
        }

        //setUpToolbar(player);
        if (savedInstanceState == null) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.feeds_view_pager);
            FeedsPagerAdapter adapter = new FeedsPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(ActivityFeedFragment.newInstance(ActivityFeedFragment.TYPE_ACTIVITIES), "ACTIVITIES");
            adapter.addFragment(VideoListFeedFragment.newInstance(VideoListFeedFragment.TYPE_VIDEOS), "VIDEOS");
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.feeds_tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        }

        supportPostponeEnterTransition();

        setUpToolbar(player);

        initNavDrawer();

        if (savedInstanceState != null) {
            mSubscribed = savedInstanceState.getBoolean(KEY_SUBSCRIBED, false);
        }

        if (!havePermissions()) {
            Log.i(TAG, "Requesting permissions needed for this app.");
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);

        if (havePermissions()) {
            buildGoogleApiClient();
        }
    }

    private void initNavDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (mNavigationView != null && mDrawerLayout != null) {

            Log.d(TAG, "initDrawer - mDrawerLayout");

            mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    mDrawerLayout.closeDrawers();
                    //menuItem.setChecked(true);
                    switch (menuItem.getItemId()) {
                        case R.id.nav_leaders:
                            Log.d(TAG, "leaders");
                            //Intent termsIntent = WebviewActivity.createDefaultIntent(BaseActivity.this, "http://clickherelabs.com/legal-notice/", "Terms and Conditions");
                            //startActivity(termsIntent);
                            break;
                        case R.id.nav_sign_out:
                            Log.d(TAG, "signout");
                            signOut();
                            break;
                        // TODO - Handle other items
                    }
                    return true;
                }
            });

            View headerView = LayoutInflater.from(this).inflate(R.layout.header, mNavigationView, false);
            mNavigationView.addHeaderView(headerView);

            TextView ver = (TextView) headerView.findViewById(R.id.version_name);
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                ver.setText(String.format(getString(R.string.app_version),pInfo.versionName));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }

            TextView scoreView = (TextView) headerView.findViewById(R.id.points);
            final int score = TopekaDatabaseHelper.getScore(this);
            scoreView.setText(getString(R.string.x_points, score));

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.nav_drawer_open, R.string.nav_drawer_close){

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                    super.onDrawerOpened(drawerView);
                }
            };

            //Setting the actionbarToggle to drawer layout
            mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();


        }


    }

    private void setUpToolbar(Player player) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_player);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out: {
                signOut();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void signOut() {
        PreferencesHelper.signOut(this);
        TopekaDatabaseHelper.reset(this);
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setExitTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.category_enter));
        }
        SignInActivity.start(this, false);
        ActivityCompat.finishAfterTransition(this);
    }

    private String getDisplayName(Player player) {
        return getString(R.string.player_display_name, player.getFirstName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // There are states to watch when a user denies permission when presented with
                // the Nearby permission dialog: 1) When the user pressed "Deny", but does not
                // check the "Never ask again" option. In this case, we display a Snackbar which
                // lets the user kick off the permissions flow again. 2) When the user pressed
                // "Deny" and also checked the "Never ask again" option. In this case, the
                // permission dialog will no longer be presented to the user. The user may still
                // want to authorize location and use the app, and we present a Snackbar that
                // directs them to go to Settings where they can grant the location permission.
                if (shouldShowRequestPermissionRationale(permission)) {
                    Log.i(TAG, "Permission denied without 'NEVER ASK AGAIN': " + permission);
                    showRequestPermissionsSnackbar();
                } else {
                    Log.i(TAG, "Permission denied with 'NEVER ASK AGAIN': " + permission);
                    showLinkToSettingsSnackbar();
                }
            } else {
                Log.i(TAG, "Permission granted, building GoogleApiClient");
                buildGoogleApiClient();
            }
        }
    }

    /**
     * Builds {@link GoogleApiClient}, enabling automatic lifecycle management using
     * {@link GoogleApiClient.Builder#enableAutoManage(FragmentActivity,
     * int, GoogleApiClient.OnConnectionFailedListener)}. I.e., GoogleApiClient connects in
     * {@link AppCompatActivity#onStart} -- or if onStart() has already happened -- it connects
     * immediately, and disconnects automatically in {@link AppCompatActivity#onStop}.
     */
    private synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
                            .setPermissions(NearbyPermissions.BLE)
                            .build())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .build();
        }
    }

    @Override
    protected void onPause() {
        getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mContainer != null) {
            Snackbar.make(mContainer, "Exception while connecting to Google Play services: " +
                            connectionResult.getErrorMessage(),
                    Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended. Error code: " + i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        subscribe();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(key, Utils.KEY_CACHED_MESSAGES)) {
            //mNearbyMessagesList.clear();
            //mNearbyMessagesList.addAll(Utils.getCachedMessages(this));
            //mNearbyMessagesArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBSCRIBED, mSubscribed);
    }

    private boolean havePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Calls {@link Messages#subscribe(GoogleApiClient, MessageListener, SubscribeOptions)},
     * using a {@link Strategy} for BLE scanning. Attaches a {@link ResultCallback} to monitor
     * whether the call to {@code subscribe()} succeeded or failed.
     */
    private void subscribe() {
        // In this sample, we subscribe when the activity is launched, but not on device orientation
        // change.
        Log.i(TAG, "subscribe.");
        if (TSRHCPreferenceManager.getInstance().hasSubscribed()) {
            Log.i(TAG, "Already subscribed.");
            return;
        }

        TSRHCPreferenceManager.getInstance().setSubscribed(true);

        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();

        Nearby.Messages.subscribe(mGoogleApiClient, getPendingIntent(), options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                        if (status.isSuccess()) {
                            mSubscribed = true;
                            Log.i(TAG, "Subscribed successfully.");
                            startService(getBackgroundSubscribeServiceIntent());
                        } else {
                            Log.e(TAG, "Operation failed. Error: " +
                                    NearbyMessagesStatusCodes.getStatusCodeString(
                                            status.getStatusCode()));
                        }
                    }
                });
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getService(this, 0,
                getBackgroundSubscribeServiceIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getBackgroundSubscribeServiceIntent() {
        return new Intent(this, BackgroundSubscribeIntentService.class);
    }

    /**
     * Displays {@link Snackbar} instructing user to visit Settings to grant permissions required by
     * this application.
     */
    private void showLinkToSettingsSnackbar() {
        if (mContainer == null) {
            return;
        }
        Snackbar.make(mContainer,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * Displays {@link Snackbar} with button for the user to re-initiate the permission workflow.
     */
    private void showRequestPermissionsSnackbar() {
        if (mContainer == null) {
            return;
        }
        Snackbar.make(mContainer, R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request permission.
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
    }
    class FeedsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FeedsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

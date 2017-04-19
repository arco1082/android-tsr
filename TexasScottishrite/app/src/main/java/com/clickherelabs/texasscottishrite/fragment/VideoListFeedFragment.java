package com.clickherelabs.texasscottishrite.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clickherelabs.texasscottishrite.R;
import com.clickherelabs.texasscottishrite.activity.QuizActivity;
import com.clickherelabs.texasscottishrite.adapter.FirebasePostAdapter;
import com.clickherelabs.texasscottishrite.adapter.FirebaseVideoListAdapter;
import com.clickherelabs.texasscottishrite.carboard.SimpleVrVideoActivity;
import com.clickherelabs.texasscottishrite.cast.VideoBrowserActivity;
import com.clickherelabs.texasscottishrite.firebase.FirebaseUtil;
import com.clickherelabs.texasscottishrite.helper.TransitionHelper;
import com.clickherelabs.texasscottishrite.model.Category;
import com.clickherelabs.texasscottishrite.model.Game;
import com.clickherelabs.texasscottishrite.model.Post;
import com.clickherelabs.texasscottishrite.model.Theme;
import com.clickherelabs.texasscottishrite.model.VideoList;
import com.clickherelabs.texasscottishrite.widget.OffsetDecoration;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/28/16.
 */
public class VideoListFeedFragment extends Fragment {
    private static String TAG = ActivityFeedFragment.class.getSimpleName();

    private static final int REQUEST_CATEGORY = 0x2300;
    private int mRecyclerViewPosition = 0;
    private static final String KEY_TYPE = "type";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private RecyclerView mRecyclerView;
    private FirebaseVideoListAdapter mAdapter;
    private OnPostSelectedListener mListener;
    private List<Post> mItems = new ArrayList<>();

    public static final int TYPE_ACTIVITIES = 1001;
    public static final int TYPE_VIDEOS = 1002;

    public interface OnPostSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    public static VideoListFeedFragment newInstance(int type) {
        VideoListFeedFragment fragment = new VideoListFeedFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static VideoListFeedFragment newInstance() {
        return new VideoListFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpQuizGrid(mRecyclerView);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpQuizGrid(RecyclerView categoriesView) {
        final int spacing = getContext().getResources()
                .getDimensionPixelSize(R.dimen.spacing_nano);
        categoriesView.addItemDecoration(new OffsetDecoration(spacing));

        FirebaseUtil.getVideoListsByBrand("tsrhc")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        mAdapter.addItem(dataSnapshot.getKey());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        mAdapter.removeItem(dataSnapshot.getKey());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mAdapter = new FirebaseVideoListAdapter(getActivity(), "tsrhc", null,  new FirebaseVideoListAdapter.OnSetupViewListener() {
            @Override
            public void onSetupView(FirebaseVideoListAdapter.ViewHolder holder, VideoList post, int position, String postKey) {
                setupVideoList(holder, post, position, postKey);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void setupVideoList( FirebaseVideoListAdapter.ViewHolder viewHolder, final VideoList post, final int position, String postKey) {
        viewHolder.title.setText(post.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                startCastActivityWithTransition(activity,
                        v.findViewById(R.id.category_title), post.getTitle());
            }
        });

        final Theme theme = Theme.valueOf(post.getTheme());

        if (getActivity() != null) {
            Activity act = getActivity();
            viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.fpo));
            viewHolder.itemView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
            viewHolder.title.setBackgroundColor(getColor(theme.getPrimaryColor()));
            viewHolder.title.setTextColor(getColor(theme.getTextPrimaryColor()));
            viewHolder.function_icon.setImageDrawable(null);
        }


    }


    /**
     * Convenience method for color loading.
     *
     * @param colorRes The resource id of the color to load.
     * @return The loaded color.
     */
    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(getActivity(), colorRes);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        // TODO: Is null check necessary?
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }

    @Override
    public void onResume() {
        getActivity().supportStartPostponedEnterTransition();
        super.onResume();
    }

    private void startCastActivityWithTransition(Activity activity, View toolbar,
                                                 String category) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = VideoBrowserActivity
                .getStartIntent(activity, category);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }
}


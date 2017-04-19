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
import com.clickherelabs.texasscottishrite.adapter.CategoryAdapter;
import com.clickherelabs.texasscottishrite.adapter.FirebasePostAdapter;
import com.clickherelabs.texasscottishrite.carboard.CardBoardActivity;
import com.clickherelabs.texasscottishrite.carboard.SimpleVrVideoActivity;
import com.clickherelabs.texasscottishrite.cast.VideoBrowserActivity;
import com.clickherelabs.texasscottishrite.firebase.FirebaseUtil;
import com.clickherelabs.texasscottishrite.helper.TransitionHelper;
import com.clickherelabs.texasscottishrite.model.Brand;
import com.clickherelabs.texasscottishrite.model.Category;
import com.clickherelabs.texasscottishrite.model.Game;
import com.clickherelabs.texasscottishrite.model.Post;
import com.clickherelabs.texasscottishrite.model.Theme;
import com.clickherelabs.texasscottishrite.persistence.TopekaDatabaseHelper;
import com.clickherelabs.texasscottishrite.widget.OffsetDecoration;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/28/16.
 */
public class ActivityFeedFragment extends Fragment {
    private static String TAG = ActivityFeedFragment.class.getSimpleName();

    private static final int REQUEST_CATEGORY = 0x2300;
    private int mRecyclerViewPosition = 0;
    private static final String KEY_TYPE = "type";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private RecyclerView mRecyclerView;
    private FirebasePostAdapter mAdapter;
    private OnPostSelectedListener mListener;
    private List<Post> mItems = new ArrayList<>();

    public static final int TYPE_ACTIVITIES = 1001;
    public static final int TYPE_VIDEOS = 1002;

    public interface OnPostSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    public static ActivityFeedFragment newInstance(int type) {
        ActivityFeedFragment fragment = new ActivityFeedFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActivityFeedFragment newInstance() {
        return new ActivityFeedFragment();
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

        FirebaseUtil.getPostByBrand("tsrhc")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "adding post key: " + dataSnapshot.getKey());
                        Post post = dataSnapshot.getValue(Post.class);
                        Long tsLong = System.currentTimeMillis()/1000;
                        Log.d(TAG, "time " + tsLong);
                        if (Long.valueOf(post.getEnabledOndate().toString()) > 0 && Long.valueOf(post.getDisabledOnDate().toString()) > 0) {
                            if (Long.valueOf(post.getEnabledOndate().toString()) <= tsLong && Long.valueOf(post.getDisabledOnDate().toString()) >= tsLong) {
                                Log.d(TAG, "found enabled post");
                                mAdapter.addItem(dataSnapshot.getKey());
                                mAdapter.notifyDataSetChanged();

                            }
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildChanged");
                        Post post = dataSnapshot.getValue(Post.class);
                        Long tsLong = System.currentTimeMillis()/1000;
                        Log.d(TAG, "time " + tsLong);
                        if (Long.valueOf(post.getEnabledOndate().toString()) > 0 && Long.valueOf(post.getDisabledOnDate().toString()) > 0) {
                            if (Long.valueOf(post.getEnabledOndate().toString()) <= tsLong && Long.valueOf(post.getDisabledOnDate().toString()) >= tsLong) {
                                Log.d(TAG, "found enabled post");
                                mAdapter.updateItem(dataSnapshot.getKey());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "found disabled post");
                                mAdapter.removeItem(dataSnapshot.getKey());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
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

        mAdapter = new FirebasePostAdapter(getActivity(), "tsrhc",null,  new FirebasePostAdapter.OnSetupViewListener() {
            @Override
            public void onSetupView(FirebasePostAdapter.ViewHolder holder, Post post, int position, String postKey) {
                Log.d(TAG, "onSetupView " + position);
                setupPost(holder, post, position, postKey);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void setupPost( FirebasePostAdapter.ViewHolder viewHolder, final Post post, final int position, String postKey) {
        viewHolder.title.setText(post.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals(post.getType(), "quiz")) {
                    List<Category> mCategories = TopekaDatabaseHelper.getCategories(getActivity(), true);
                    Activity activity = getActivity();
                    startQuizActivityWithTransition(activity,
                            v.findViewById(R.id.category_title), mCategories.get(0));
                } else if(TextUtils.equals(post.getType(), "360")) {
                    Activity activity = getActivity();
                    startVideoActivityWithTransition(activity,
                            v.findViewById(R.id.category_title));
                } else {
                    Activity activity = getActivity();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(post.getIntent()));
                    activity.startActivity(intent);
                }


                //mOnItemClickListener.onClick(v, position);
            }
        });

        final Theme theme = Theme.valueOf(post.getTheme());

        if (getActivity() != null) {
            Activity act = getActivity();
            if (TextUtils.equals(post.getType(), "360")) {
                viewHolder.function_icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.video_360));
            } else if (TextUtils.equals(post.getType(), "ar")) {
                viewHolder.function_icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.ar));
            } else if (TextUtils.equals(post.getType(), "cardboard")) {
                viewHolder.function_icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.cardboard));
            } else if (TextUtils.equals(post.getType(), "game")) {
                viewHolder.function_icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.game));
            } else {
                viewHolder.function_icon.setImageDrawable(null);
            }
            viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.fpo));
            viewHolder.itemView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
        }


    }

    private Drawable wrapAndTint(Drawable done, @ColorRes int color) {
        Drawable compatDrawable = DrawableCompat.wrap(done);
        DrawableCompat.setTint(compatDrawable, getColor(color));
        return compatDrawable;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        loadBrand();
        getActivity().supportStartPostponedEnterTransition();
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        detachListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }*/
    }

    private void detachListeners() {
        Log.d(TAG, "detachListeners");

        if (mQueryBrand != null) {
            mQueryBrand.removeEventListener(mBrandListener);
        }

    }

    private void setCategoryIcon(FirebasePostAdapter.ViewHolder holder, Game game) {
        //Drawable solvedIcon = loadSolvedIcon(category, categoryImageResource);
        String type = game.getType();

        if (TextUtils.equals(type, "360")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.video_360));
        } else if (TextUtils.equals(type, "ar")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ar));
        } else if (TextUtils.equals(type, "cardboard")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.cardboard));
        } else if (TextUtils.equals(type, "game")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.game));
        } else {
            holder.function_icon.setImageDrawable(null);
        }
        holder.icon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.fpo));
    }

    Query mQueryBrand;

    private void loadBrand() {
        if (mQueryBrand == null) {
            mQueryBrand = FirebaseUtil.getBrandByCode("tsrhc");
        }

        mQueryBrand.addValueEventListener(mBrandListener);
    }


    ValueEventListener mBrandListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "[loadParticipant] onDataChange" + dataSnapshot);
            Brand brand = dataSnapshot.getValue(Brand.class);
            if (brand != null) {
                Log.d(TAG, "[loadParticipant] onDataChange" + brand.getCode());
                Log.d(TAG, "[onLoadFinished] " + brand.getName());

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "[loadParticipant] onCancelled " + databaseError);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CATEGORY && resultCode == R.id.solved) {
            //mAdapter.notifyItemChanged(data.getStringExtra(JsonAttributes.ID));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startQuizActivityWithTransition(Activity activity, View toolbar,
                                                 Category category) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = QuizActivity.getStartIntent(activity, category);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }

    private void startVideoActivityWithTransition(Activity activity, View toolbar) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = SimpleVrVideoActivity.getStartIntent(activity);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }

    private void startCardboardActivityWithTransition(Activity activity, View toolbar,
                                                      Category category) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = CardBoardActivity.getStartIntent(activity, category);
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }

    private void startCastActivityWithTransition(Activity activity, View toolbar,
                                                 Category category) {

        final Pair[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<>(toolbar, activity.getString(R.string.transition_toolbar)));
        @SuppressWarnings("unchecked")
        ActivityOptionsCompat sceneTransitionAnimation = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, pairs);

        // Start the activity with the participants, animating from one to the other.
        final Bundle transitionBundle = sceneTransitionAnimation.toBundle();
        Intent startIntent = VideoBrowserActivity
                .getStartIntent(activity, category.getName());
        ActivityCompat.startActivityForResult(activity,
                startIntent,
                REQUEST_CATEGORY,
                transitionBundle);
    }
}

/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package  com.clickherelabs.texasscottishrite.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.clickherelabs.texasscottishrite.widget.OffsetDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CategorySelectionFragment extends Fragment {
    private static String TAG = CategorySelectionFragment.class.getSimpleName();

    private static final int REQUEST_CATEGORY = 0x2300;
    private int mRecyclerViewPosition = 0;
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter<FirebaseGameAdapter.ViewHolder> mAdapter;
    private CategoryAdapter mAdapter;
    private OnGameSelectedListener mListener;

    public interface OnGameSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    public static CategorySelectionFragment newInstance() {
        return new CategorySelectionFragment();
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
        mAdapter = new CategoryAdapter(getActivity());
        mAdapter.setOnItemClickListener(
                new CategoryAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {

                        if (position == 0) {
                            Activity activity = getActivity();
                            startVideoActivityWithTransition(activity,
                                    v.findViewById(R.id.category_title),
                                    mAdapter.getItem(position));
                        } else if (position == 1) {
                            Activity activity = getActivity();
                            startCardboardActivityWithTransition(activity,
                                    v.findViewById(R.id.category_title),
                                    mAdapter.getItem(position));
                        } else if (position == 2) {
                            Activity activity = getActivity();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("tsrhcspellcast://home"));
                            activity.startActivity(intent);
                        } else if (position == 3) {
                            Activity activity = getActivity();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("kaleidescope://home"));
                            activity.startActivity(intent);
                        } else if (position == 4) {
                            Activity activity = getActivity();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("tsrhcar://home"));
                            activity.startActivity(intent);
                        } else if (position == 5) {
                            Activity activity = getActivity();
                            startCastActivityWithTransition(activity,
                                    v.findViewById(R.id.category_title),
                                    mAdapter.getItem(position));
                            /*Activity activity = getActivity();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("tsrhcnightmare://home"));
                            activity.startActivity(intent);*/

                        } else {

                            Activity activity = getActivity();
                            startCastActivityWithTransition(activity,
                                    v.findViewById(R.id.category_title),
                                    mAdapter.getItem(position));
                        }

                    }
                });
        categoriesView.setAdapter(mAdapter);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState.getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }

        final int spacing = getContext().getResources()
                .getDimensionPixelSize(R.dimen.spacing_nano);
        mRecyclerView.addItemDecoration(new OffsetDecoration(spacing));

        final List<String> postPaths = new ArrayList<>();

        mAdapter = new FirebaseGameAdapter(getActivity(), "tsrhc", postPaths,
                new FirebaseGameAdapter.OnSetupViewListener() {
                    @Override
                    public void onSetupView(FirebaseGameAdapter.ViewHolder holder, Game game, int position, String postKey) {
                        Log.d(TAG, "onSetupView");
                        setupGame(holder, game, position, postKey);
                    }
                });

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();*/

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

    private void startVideoActivityWithTransition(Activity activity, View toolbar,
                                                      Category category) {

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

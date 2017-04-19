package com.clickherelabs.texasscottishrite.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.clickherelabs.texasscottishrite.R;
import com.clickherelabs.texasscottishrite.firebase.FirebaseUtil;
import com.clickherelabs.texasscottishrite.model.VideoList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/28/16.
 */
public class FirebaseVideoListAdapter extends RecyclerView.Adapter<FirebaseVideoListAdapter.ViewHolder> {
    private final String TAG = FirebaseVideoListAdapter.class.getSimpleName();
    private List<String> mPostPaths;
    private OnSetupViewListener mOnSetupViewListener;
    private String brand;
    private final Resources mResources;
    private final String mPackageName;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;

    public FirebaseVideoListAdapter(Activity activity, String brand, List<String> paths, OnSetupViewListener onSetupViewListener) {

        Log.d(TAG, "FirebaseVideoListAdapter");
        mActivity = activity;
        mResources = mActivity.getResources();
        mPackageName = mActivity.getPackageName();
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());

        if (paths == null || paths.isEmpty()) {
            mPostPaths = new ArrayList<>();
        } else {
            mPostPaths = paths;
        }
        mOnSetupViewListener = onSetupViewListener;
        this.brand = brand;
    }

    @Override
    public FirebaseVideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new FirebaseVideoListAdapter.ViewHolder(v);
    }

    public void setPaths(List<String> postPaths) {
        mPostPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        Log.d(TAG, "removeItem");
        mPostPaths.add(path);
        notifyItemInserted(mPostPaths.size());
    }

    public void removeItem(String path) {
        Log.d(TAG, "removeItem");
        int index = 0;
        boolean found = false;

        for (int i = 0; i < mPostPaths.size(); i ++) {
            if (TextUtils.equals(path, mPostPaths.get(i))) {
                index = i;
                found = true;
            }
        }

        if (found) {
            mPostPaths.remove(index);
            notifyItemRemoved(index);
        }

    }

    @Override
    public void onBindViewHolder(final FirebaseVideoListAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        DatabaseReference ref = FirebaseUtil.getVideoListsByBrand(brand).child(mPostPaths.get(position));
        // TODO: Fix this so async event won't bind the wrong view post recycle.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VideoList post = dataSnapshot.getValue(VideoList.class);
                mOnSetupViewListener.onSetupView(holder, post, holder.getAdapterPosition(),
                        dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, "Error occurred: " + firebaseError.getMessage());
            }
        };
        ref.addValueEventListener(postListener);

    }

    @Override
    public void onViewRecycled(FirebaseVideoListAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mPostPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(FirebaseVideoListAdapter.ViewHolder holder, VideoList post, int position, String postKey);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView icon;
        public final TextView title;
        public final ImageView function_icon;

        public ViewHolder(View container) {
            super(container);
            icon = (ImageView) container.findViewById(R.id.category_icon);
            title = (TextView) container.findViewById(R.id.category_title);
            function_icon = (ImageView) container.findViewById(R.id.function_icon);
        }
    }
}


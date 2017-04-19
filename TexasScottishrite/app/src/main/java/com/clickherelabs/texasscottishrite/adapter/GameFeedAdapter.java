package com.clickherelabs.texasscottishrite.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickherelabs.texasscottishrite.R;
import com.clickherelabs.texasscottishrite.model.Category;
import com.clickherelabs.texasscottishrite.model.Game;
import com.clickherelabs.texasscottishrite.persistence.TopekaDatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando_contreras on 7/24/16.
 */
public class GameFeedAdapter extends FirebaseRecyclerAdapter<Game, FirebasePostAdapter.ViewHolder> {
    private final String TAG = "GameFeedAdapter";
    private List<String> mPostPaths;
    private OnSetupViewListener mOnSetupViewListener;
    private String brand;
    private final Resources mResources;
    private final String mPackageName;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private DatabaseReference mDatabaseReference;
    public static final String DRAWABLE = "drawable";
    private static final String ICON_CATEGORY = "icon_category_";
    private List<Category> mCategories;

    public GameFeedAdapter(Activity activity, List<String> paths, DatabaseReference databasereference) {

        super(Game.class, R.layout.item_category, FirebasePostAdapter.ViewHolder.class, databasereference);
        Log.d(TAG, "GameFeedAdapter");
        updateCategories(activity);
        mActivity = activity;
        mResources = mActivity.getResources();
        mPackageName = mActivity.getPackageName();
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());

        if (paths == null || paths.isEmpty()) {
            mPostPaths = new ArrayList<>();
        } else {
            mPostPaths = paths;
        }

        mDatabaseReference = databasereference;
        this.brand = brand;
    }

    private void updateCategories(Activity activity) {
        mCategories = TopekaDatabaseHelper.getCategories(activity, true);
    }

    @Override
    public FirebasePostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new FirebasePostAdapter.ViewHolder(v);
    }

    @Override
    public void populateViewHolder(final FirebasePostAdapter.ViewHolder postViewHolder,
                                   final Game game, final int position) {
        Log.d(TAG, "populateViewHolder");
        setupGame(postViewHolder, game, position, null);
    }

    private void setupGame(FirebasePostAdapter.ViewHolder holder, Game game, int position, String gameKey) {
        Log.d(TAG, "setupGame");
        //Theme theme = category.getTheme();
        setCategoryIcon(holder, game);
        //holder.itemView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
        holder.title.setText(game.getName());
        //holder.title.setTextColor(getColor(theme.getTextPrimaryColor()));
        //holder.title.setBackgroundColor(getColor(theme.getPrimaryColor()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mOnItemClickListener.onClick(v, position);
            }
        });
    }

    private void setCategoryIcon(FirebasePostAdapter.ViewHolder holder, Game game) {
        //Drawable solvedIcon = loadSolvedIcon(category, categoryImageResource);
        String type = game.getType();

        if (TextUtils.equals(type, "360")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.video_360));
        } else if (TextUtils.equals(type, "ar")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ar));
        } else if (TextUtils.equals(type, "cardboard")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.cardboard));
        } else if (TextUtils.equals(type, "game")) {
            holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.game));
        } else {
            holder.function_icon.setImageDrawable(null);
        }
        holder.icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.fpo));
    }

    public void setPaths(List<String> postPaths) {
        mPostPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        mPostPaths.add(path);
        notifyItemInserted(mPostPaths.size());
    }

    @Override
    public void onViewRecycled(FirebasePostAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private Drawable loadTintedCategoryDrawable(Category category, int categoryImageResource) {
        final Drawable categoryIcon = ContextCompat
                .getDrawable(mActivity, categoryImageResource).mutate();
        return wrapAndTint(categoryIcon, category.getTheme().getPrimaryColor());
    }

    private Drawable wrapAndTint(Drawable done, @ColorRes int color) {
        Drawable compatDrawable = DrawableCompat.wrap(done);
        DrawableCompat.setTint(compatDrawable, getColor(color));
        return compatDrawable;
    }

    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(mActivity, colorRes);
    }


    @Override
    public int getItemCount() {
        return mPostPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(FirebasePostAdapter.ViewHolder holder, Game game, int position, String postKey);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView icon;
        public final TextView title;
        public final ImageView function_icon;
        public DatabaseReference mGamesRef;
        public ValueEventListener mGameListener;

        public ViewHolder(View container) {
            super(container);
            icon = (ImageView) container.findViewById(R.id.category_icon);
            title = (TextView) container.findViewById(R.id.category_title);
            function_icon = (ImageView) container.findViewById(R.id.function_icon);
        }
    }
}

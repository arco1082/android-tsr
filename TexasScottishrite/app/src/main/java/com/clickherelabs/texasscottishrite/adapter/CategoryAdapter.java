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

package  com.clickherelabs.texasscottishrite.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
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
import com.clickherelabs.texasscottishrite.helper.ApiLevelHelper;
import com.clickherelabs.texasscottishrite.model.Category;
import com.clickherelabs.texasscottishrite.model.Theme;
import com.clickherelabs.texasscottishrite.persistence.TopekaDatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static String TAG = CategoryAdapter.class.getSimpleName();
    public static final String DRAWABLE = "drawable";
    private static final String ICON_CATEGORY = "icon_category_";
    private final Resources mResources;
    private final String mPackageName;
    private final LayoutInflater mLayoutInflater;
    private final Activity mActivity;
    private List<Category> mCategories;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public CategoryAdapter(Activity activity) {
        mActivity = activity;
        mResources = mActivity.getResources();
        mPackageName = mActivity.getPackageName();
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
        updateCategories(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Category category = mCategories.get(position);
        Theme theme = category.getTheme();
        setCategoryIcon(category, holder);
        holder.itemView.setBackgroundColor(getColor(theme.getWindowBackgroundColor()));
        holder.title.setText(category.getName());
        holder.title.setTextColor(getColor(theme.getTextPrimaryColor()));
        holder.title.setBackgroundColor(getColor(theme.getPrimaryColor()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mCategories.get(position).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public Category getItem(int position) {
        return mCategories.get(position);
    }

    /**
     * @see android.support.v7.widget.RecyclerView.Adapter#notifyItemChanged(int)
     * @param id Id of changed category.
     */
    public final void notifyItemChanged(String id) {
        updateCategories(mActivity);
        notifyItemChanged(getItemPositionById(id));
    }

    private int getItemPositionById(String id) {
        for (int i = 0; i < mCategories.size(); i++) {
            if (mCategories.get(i).getId().equals(id)) {
                return i;
            }

        }
        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void setCategoryIcon(Category category, ViewHolder holder) {
        final int categoryImageResource = mResources.getIdentifier(
                ICON_CATEGORY + category.getId(), DRAWABLE, mPackageName);
        final boolean solved = category.isSolved();
        Log.d(TAG, "setCategoryIcon " + category.getAction());
        if (solved) {
            Drawable solvedIcon = loadSolvedIcon(category, categoryImageResource);
            if (TextUtils.equals(category.getAction(), "360")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.video_360));
            } else if (TextUtils.equals(category.getAction(), "ar")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ar));
            } else if (TextUtils.equals(category.getAction(), "cardboard")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.cardboard));
            } else if (TextUtils.equals(category.getAction(), "game")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.game));
            } else {
                holder.function_icon.setImageDrawable(null);
            }
            holder.icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.fpo));
            /*Picasso.with(holder.icon.getContext()).load("https://placeholdit.imgix.net/~text?txtsize=50&txt=FPO&w=200&h=200").into(holder.icon, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("DemosRecyclerAdapter", "onSuccess");
                }

                @Override
                public void onError() {
                    Log.d("DemosRecyclerAdapter", "image url onError");
                }
            });*/

        } else {
            //holder.icon.setImageResource(categoryImageResource);
            if (TextUtils.equals(category.getAction(), "360")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.video_360));
            } else if (TextUtils.equals(category.getAction(), "ar")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ar));
            } else if (TextUtils.equals(category.getAction(), "cardboard")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.cardboard));
            } else if (TextUtils.equals(category.getAction(), "game")) {
                holder.function_icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.game));
            } else {
                holder.function_icon.setImageDrawable(null);
            }
            holder.icon.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.fpo));
            /*Picasso.with(holder.icon.getContext()).load("https://placeholdit.imgix.net/~text?txtsize=50&txt=FPO&w=200&h=200").into(holder.icon, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("DemosRecyclerAdapter", "onSuccess");
                }

                @Override
                public void onError() {
                    Log.d("DemosRecyclerAdapter", "image url onError");
                }
            });*/

        }
    }

    private void updateCategories(Activity activity) {
        mCategories = TopekaDatabaseHelper.getCategories(activity, true);
    }

    /**
     * Loads an icon that indicates that a category has already been solved.
     *
     * @param category The solved category to display.
     * @param categoryImageResource The category's identifying image.
     * @return The icon indicating that the category has been solved.
     */
    private Drawable loadSolvedIcon(Category category, int categoryImageResource) {
        if (ApiLevelHelper.isAtLeast(Build.VERSION_CODES.LOLLIPOP)) {
            return loadSolvedIconLollipop(category, categoryImageResource);
        }
        return loadSolvedIconPreLollipop(category, categoryImageResource);
    }

    @NonNull
    private LayerDrawable loadSolvedIconLollipop(Category category, int categoryImageResource) {
        final Drawable done = loadTintedDoneDrawable();
        final Drawable categoryIcon = loadTintedCategoryDrawable(category, categoryImageResource);
        Drawable[] layers = new Drawable[]{categoryIcon, done}; // ordering is back to front
        return new LayerDrawable(layers);
    }

    private Drawable loadSolvedIconPreLollipop(Category category, int categoryImageResource) {
        return loadTintedCategoryDrawable(category, categoryImageResource);
    }

    /**
     * Loads and tints a drawable.
     *
     * @param category The category providing the tint color
     * @param categoryImageResource The image resource to tint
     * @return The tinted resource
     */
    private Drawable loadTintedCategoryDrawable(Category category, int categoryImageResource) {
        final Drawable categoryIcon = ContextCompat
                .getDrawable(mActivity, categoryImageResource).mutate();
        return wrapAndTint(categoryIcon, category.getTheme().getPrimaryColor());
    }

    /**
     * Loads and tints a check mark.
     *
     * @return The tinted check mark
     */
    private Drawable loadTintedDoneDrawable() {
        final Drawable done = ContextCompat.getDrawable(mActivity, R.drawable.ic_tick);
        return wrapAndTint(done, android.R.color.white);
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
        return ContextCompat.getColor(mActivity, colorRes);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView icon;
        final TextView title;
        final ImageView function_icon;
        public ViewHolder(View container) {
            super(container);
            icon = (ImageView) container.findViewById(R.id.category_icon);
            title = (TextView) container.findViewById(R.id.category_title);
            function_icon = (ImageView) container.findViewById(R.id.function_icon);
        }
    }
}

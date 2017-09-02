/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2015 Redwarp
 */

package net.redwarp.library.testapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RandomUserAdapter extends RecyclerView.Adapter<RandomUserAdapter.ViewHolder> {

    private final Context mContext;
    private List<RandomUser> mStuffList;
    private ItemCountChangedListener mCountListener = null;


    private OnRandomUserClickedListener mUserClickedListener = null;

    public RandomUserAdapter(Context context, List<RandomUser> stuffList) {
        mContext = context;

        if (stuffList != null) {
            mStuffList = stuffList;
        } else {
            mStuffList = new ArrayList<>();
        }
    }

    public void addStuff(RandomUser stuff) {
        if (stuff != null) {
            mStuffList.add(stuff);
            notifyItemInserted(mStuffList.size() - 1);
            postCount();
        }
    }

    public void addAllStuff(List<RandomUser> stuffList) {
        if (stuffList != null) {
            int startingPoint = mStuffList.size();
            mStuffList.addAll(stuffList);
            notifyItemRangeInserted(startingPoint, stuffList.size());
            postCount();
        }
    }

    public void clearStuff() {
        int numberOfItems = mStuffList.size();
        mStuffList.clear();
        notifyItemRangeRemoved(0, numberOfItems);
        postCount();
    }

    private void postCount() {
        if (mCountListener != null) {
            mCountListener.onItemCountChange(mStuffList.size());
        }
    }

    public void setOnItemCountChangeListener(ItemCountChangedListener listener) {
        mCountListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View
            view =
            LayoutInflater.from(mContext).inflate(R.layout.item_random_stuff, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RandomUser stuff = mStuffList.get(position);
        holder.textView.setText(stuff.name);
    }

    @Override
    public int getItemCount() {
        return mStuffList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull final View view) {
            if (mUserClickedListener != null) {
                assert mStuffList != null;
                int position = getLayoutPosition();
                if (position < mStuffList.size()) {
                    mUserClickedListener
                        .onRandomUserClicked(mStuffList.get(position), view, position);
                }
            }
        }
    }

    public void setOnUserClickedListener(OnRandomUserClickedListener userClickedListener) {
        mUserClickedListener = userClickedListener;
    }

    public interface ItemCountChangedListener {

        void onItemCountChange(int newCount);
    }

    public interface OnRandomUserClickedListener {

        void onRandomUserClicked(final RandomUser user, final View view, final int position);
    }
}

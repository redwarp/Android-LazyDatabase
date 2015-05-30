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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Redwarp on 27/05/2015.
 */
public class RandomStuffAdapter extends RecyclerView.Adapter<RandomStuffAdapter.ViewHolder> {

  private final Context mContext;
  private List<RandomStuff> mStuffList;

  public RandomStuffAdapter(Context context, List<RandomStuff> stuffList) {
    mContext = context;

    if (stuffList != null) {
      mStuffList = stuffList;
    } else {
      mStuffList = new ArrayList<>();
    }
  }

  public void addStuff(RandomStuff stuff) {
    if (stuff != null) {
      mStuffList.add(stuff);
      notifyItemInserted(mStuffList.size() - 1);
    }
  }

  public void addAllStuff(List<RandomStuff> stuffList) {
    if (stuffList != null) {
      int startingPoint = mStuffList.size();
      mStuffList.addAll(stuffList);
      notifyItemRangeInserted(startingPoint, stuffList.size());
    }
  }

  public void clearStuff() {
    int numberOfItems = mStuffList.size();
    mStuffList.clear();
    notifyItemRangeRemoved(0, numberOfItems);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.cell_random_stuff, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    RandomStuff stuff = mStuffList.get(position);
    holder.textView.setText(stuff.name);
  }

  @Override
  public int getItemCount() {
    return mStuffList.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.text)
    TextView textView;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
    }
  }
}

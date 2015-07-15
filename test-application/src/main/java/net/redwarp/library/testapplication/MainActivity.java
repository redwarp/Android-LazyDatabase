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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.redwarp.library.testapplication.tools.NameGenerator;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
    implements RandomUserAdapter.ItemCountChangedListener {

  @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
  @Bind(R.id.add_button) FloatingActionButton mAddButton;

  private NameGenerator mGenerator;
  private RandomUserAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mGenerator = new NameGenerator();

    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setHasFixedSize(true);
    mAdapter = new RandomUserAdapter(this, null);
    mAdapter.setOnItemCountChangeListener(this);
    mAdapter.setOnUserClickedListener(new RandomUserAdapter.OnRandomUserClickedListener() {
      @Override
      public void onRandomUserClicked(RandomUser user, int position) {
        // Check https://medium.com/@fredrikaldgard/easy-android-shared-element-transition-ac36952a4a4
        // for custom transitions
        startActivity(new Intent(MainActivity.this, DetailActivity.class));
      }
    });
    mRecyclerView.setAdapter(mAdapter);

    setTitle(R.string.loading);
    AsyncTask<Void, Void, List<RandomUser>>
        fetchAllStuffTask =
        new AsyncTask<Void, Void, List<RandomUser>>() {
          @Override
          protected List<RandomUser> doInBackground(Void... voids) {
            return TestApplication.getDatabaseHelper().getAll(RandomUser.class);
          }

          @Override
          protected void onPostExecute(List<RandomUser> stuffList) {
            mAdapter.addAllStuff(stuffList);
          }
        };
    fetchAllStuffTask.execute();

    mAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addRandomStuff();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_clear) {
      clearRandomStuff();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Clear the list of item from database and display
   */
  private void clearRandomStuff() {
    AsyncTask<Void, Void, Integer> clearStuffTask = new AsyncTask<Void, Void, Integer>() {
      @Override
      protected Integer doInBackground(Void... voids) {
        return TestApplication.getDatabaseHelper().clear(RandomUser.class);
      }

      @Override
      protected void onPostExecute(Integer affectedRows) {
        if (affectedRows > 0) {
          mAdapter.clearStuff();

          Snackbar.make(findViewById(R.id.coordinator_layout), getResources()
                            .getQuantityString(R.plurals.deletion, affectedRows, affectedRows),
                        Snackbar.LENGTH_SHORT).show();
        }
      }
    };
    clearStuffTask.execute();
  }

  /**
   * Add a single item to the database and display it
   */
  private void addRandomStuff() {
    AsyncTask<Void, Void, Boolean> addStuffTask = new AsyncTask<Void, Void, Boolean>() {
      RandomUser savedStuff;

      @Override
      protected Boolean doInBackground(Void... voids) {
        RandomUser stuff = new RandomUser(mGenerator.next() + " " + mGenerator.next());
        savedStuff = stuff;
        return TestApplication.getDatabaseHelper().save(stuff);
      }

      @Override
      protected void onPostExecute(Boolean success) {
        if (success) {
          mAdapter.addStuff(savedStuff);
        }
      }
    };
    addStuffTask.execute();
  }

  @Override
  public void onItemCountChange(int newCount) {
    setTitle(getResources().getQuantityString(R.plurals.item_count, newCount, newCount));
  }
}

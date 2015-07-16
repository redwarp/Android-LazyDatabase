package net.redwarp.library.testapplication;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {

  @Bind(R.id.text) TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail);
    ButterKnife.bind(this);
    if (getActionBar() != null) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    long userId = getIntent().getLongExtra(MainActivity.EXTRA_USER_ID, -1);
    if (userId != -1) {
      RandomUser user = TestApplication.getDatabaseHelper().getWithId(RandomUser.class, userId);
      if (user != null) {
        mTextView.setText(user.name);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        ActivityCompat.finishAfterTransition(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

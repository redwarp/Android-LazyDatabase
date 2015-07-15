package net.redwarp.library.testapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {

  @Bind(R.id.text) private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_detail);
    ButterKnife.bind(this);
  }
}

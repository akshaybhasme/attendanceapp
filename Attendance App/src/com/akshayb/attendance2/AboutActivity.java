package com.akshayb.attendance2;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Button rate = (Button) findViewById(R.id.bRate);
		rate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.akshayb.attendance2"));
				//Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sam://details?bundleId=dafeb92c-68e5-11e1-a703-00505690390e"));
				marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(marketIntent);
			}
		});
		TextView nrc = (TextView) findViewById(R.id.tvNRC);
		SharedPreferences myPrefs = getSharedPreferences("myprefs", MODE_PRIVATE);
		nrc.setText(""+myPrefs.getInt("NRC", 0));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.shrink_from_bottomleft_to_topright);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent home = new Intent(getApplicationContext(), SetupActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

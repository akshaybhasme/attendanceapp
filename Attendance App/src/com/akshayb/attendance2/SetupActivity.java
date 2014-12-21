package com.akshayb.attendance2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class SetupActivity extends SherlockActivity implements View.OnClickListener {
	
	private Button subject,timetable,settings,log;
	private AdView adView1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.setuplayout);
		//ActionBar ab = getSupportActionBar();
		//ab.setDisplayHomeAsUpEnabled(true);
		subject = (Button) findViewById(R.id.ibSubjects);
		timetable = (Button) findViewById(R.id.ibTimetable);
		settings = (Button) findViewById(R.id.ibSet);
		log = (Button) findViewById(R.id.ibLog);
		
		subject.setOnClickListener(this);
		timetable.setOnClickListener(this);
		settings.setOnClickListener(this);
		log.setOnClickListener(this);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.llAd1);
		adView1 = new AdView(this, AdSize.BANNER, "a14f58ac3361462");
        layout.addView(adView1);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbSetup);
        AdListener adl = new AdListener() {
			
			@Override
			public void onReceiveAd(Ad arg0) {
				pb.setVisibility(LinearLayout.GONE);
			}
			
			@Override
			public void onPresentScreen(Ad arg0) {
				
			}
			
			@Override
			public void onLeaveApplication(Ad arg0) {
				
			}
			
			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				
			}
			
			@Override
			public void onDismissScreen(Ad arg0) {
				
			}
		};
		
        adView1.loadAd(new AdRequest());
        adView1.setAdListener(adl);
        pb.setVisibility(LinearLayout.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ibSubjects:
			Intent sub = new Intent(getApplicationContext(), ViewSubject.class);
			startActivity(sub);
			overridePendingTransition(R.anim.grow_from_topleft_to_bottomright, R.anim.still);
			//finish();
			break;
		case R.id.ibTimetable:
			Intent tt = new Intent(getApplicationContext(), com.akshayb.attendance2.ViewTT.class);
			startActivity(tt);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			//finish();
			break;
		case R.id.ibSet:
			Intent set = new Intent(getApplicationContext(), SettingsAct.class);
			startActivity(set);
			overridePendingTransition(R.anim.grow_from_bottomright_to_topleft, R.anim.still);
			//finish();
			break;
		case R.id.ibLog:
			Intent log = new Intent(getApplicationContext(), ViewLog.class);
			startActivity(log);
			overridePendingTransition(R.anim.grow_from_bottomleft_to_topright, R.anim.still);
			//finish();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_setup, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Intent abt = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(abt);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

package com.akshayb.attendance2;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class AttendanceLogActivity extends Activity implements View.OnClickListener {
	
	private TextView question;
	private Button att,natt,hap;
	private String subject;
	private int hour,minute;
	private AppDb data;
	private AdView adView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attlog);
		data = new AppDb(this);
		data.open();
		question = (TextView) findViewById(R.id.tvQuestion);
		att = (Button) findViewById(R.id.bAttendn);
		natt = (Button) findViewById(R.id.bNAttendn);
		hap = (Button) findViewById(R.id.bNhappn);
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
			subject = extras.getString("subject");
			hour = extras.getInt("hour");
			minute = extras.getInt("min");
		}
		question.setText("Are you attending "+subject+" at "+hour+":"+minute+"?");
		att.setOnClickListener(this);
		natt.setOnClickListener(this);
		hap.setOnClickListener(this);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llAd2);
		adView2 = new AdView(this, AdSize.BANNER, "a14f58ac3361462");
        layout.addView(adView2);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLog);
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
        adView2.loadAd(new AdRequest());
        adView2.setAdListener(adl);
		//data.close();
	}
	@Override
	public void onClick(View arg0) {
		
		switch(arg0.getId()){
		case R.id.bAttendn:
			if(data.SubAttended(subject)){
				//data.close();
				Calendar currentDate = Calendar.getInstance();
				  SimpleDateFormat formatter= 
				   new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
				  String dateNow = formatter.format(currentDate.getTime());
				  data.new_log(subject, dateNow, "Attended");
				finish();
			}
			break;
		case R.id.bNAttendn:
			if(data.SubNotAttended(subject)){
				//data.close();
				Calendar currentDate = Calendar.getInstance();
				  SimpleDateFormat formatter= 
				   new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
				  String dateNow = formatter.format(currentDate.getTime());
				  data.new_log(subject, dateNow, "Not Attended");
				finish();
			}
			break;
		case R.id.bNhappn:
			finish();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
}

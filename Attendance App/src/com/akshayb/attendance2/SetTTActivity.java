package com.akshayb.attendance2;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class SetTTActivity extends Activity implements OnItemSelectedListener {
	
	Spinner subs;
	TimePicker time;
	Button create,subjects[];
	AppDb data;
	int subLength,nrc;
	ArrayAdapter<String> adapter;
	String subjs[],profs[];
	int currentSelected = 1;
	int day,hour,mins,lhour,lmin;
	SharedPreferences myPrefs;
	SharedPreferences.Editor prefEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);
		data = new AppDb(SetTTActivity.this);
		data.open();
		try{
			init();
			myPrefs = getSharedPreferences("myprefs", MODE_PRIVATE);
			nrc = myPrefs.getInt("NRC", 100);
			prefEditor = myPrefs.edit();
			Cursor c = data.getAllSubjects();
			c.moveToFirst();
			int iName = c.getColumnIndex("sub_name");
			int iProf = c.getColumnIndex("sub_prof");
			for(int i=0;i<subLength;i++){
				subjs[i]=c.getString(iName);
				profs[i]=" by "+c.getString(iProf);
				c.moveToNext();
				}
			c.close();
			if(data.getEventLength(day)>0){
				c = data.getTTEventOfDay(day);
				c.moveToLast();
				int ihour = c.getColumnIndex(AppDb.TIMETABLE_HOUR);
				int imin = c.getColumnIndex(AppDb.TIMETABLE_MINUTES);
				lhour = c.getInt(ihour);
				lmin = c.getInt(imin);
				time.setCurrentHour(lhour+1);
				time.setCurrentMinute(lmin);
			}
			
			adapter = new ArrayAdapter<String>(SetTTActivity.this,android.R.layout.simple_spinner_item,subjs);
			subs.setAdapter(adapter);
			subs.setOnItemSelectedListener(this);
		}catch(Exception e){
			String error= e.toString();
			Dialog d = new Dialog(this);
			d.setTitle("Nope");
			TextView tv = new TextView(this);
			tv.setText(error);
			d.setContentView(tv);
			d.show();
		}
	}

	private void init() {
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
		    day = extras.getInt("today");
		}
		
		hour = Calendar.getInstance().get(Calendar.HOUR);
		mins = Calendar.getInstance().get(Calendar.MINUTE);
		subs = (Spinner) findViewById(R.id.spinnerSubject);
		time = (TimePicker) findViewById(R.id.timePicker1);
		create = (Button) findViewById(R.id.bCreateThisEvent);
		
		create.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(data.createTtEvent(day, subjs[currentSelected], hour,mins)){
					if(myPrefs.getBoolean("notification_switch", false)){
						Calendar calendar = Calendar.getInstance();
						int cday = calendar.get(Calendar.DAY_OF_WEEK);
						cday=(cday==7?0:cday);
						//Log.w("app",cday+" "+day);
						//Log.w("app", "Reached day compare");
						if(cday == day){
							nrc++;
							Calendar time = Calendar.getInstance();
							time.set(Calendar.HOUR_OF_DAY, hour);
							time.set(Calendar.MINUTE, mins);
							time.set(Calendar.SECOND, 0);
							//Log.w("app", "Reached time compare");
							if(time.getTimeInMillis()>calendar.getTimeInMillis()){
								AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
								Intent intent= new Intent(SetTTActivity.this,NewDayNotificationSetupReceiver.class);
								intent = new Intent(SetTTActivity.this,NotificationReceiver.class);
								intent.putExtra("SubName", subjs[currentSelected]);
								intent.putExtra("Hour", hour);
								intent.putExtra("Min", mins);
								intent.putExtra("NotID", nrc);
								PendingIntent pendingIntent = PendingIntent.getBroadcast(SetTTActivity.this, nrc, intent, PendingIntent.FLAG_ONE_SHOT);
								nrc++;
								prefEditor.putInt("NRC", nrc);
								prefEditor.commit();
								am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
								//Log.w("app", "Event set");
								}	
						}
					}
					
					setResult(RESULT_OK);
					finish();
					overridePendingTransition(0, R.anim.slide_out_top);
				}else{
					//String error= e.toString();
					Dialog d = new Dialog(SetTTActivity.this);
					d.setTitle("Error");
					TextView tv = new TextView(SetTTActivity.this);
					tv.setText("reached else");
					d.setContentView(tv);
					d.show();
				}
			}

		});
		subLength = data.getSubjectCount();
		subjects = new Button[subLength];
		subjs = new String[subLength];
		profs = new String[subLength];
		time.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
				hour = arg1;
				mins = arg2;
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		currentSelected = arg2;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.slide_out_top);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}


}

package com.akshayb.attendance2;

import java.util.Calendar;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;

public class SettingsAct extends SherlockActivity {
	
	ToggleButton tb;
	EditText co;
	Button rst, share;
	int cutoff;
	int day,hrs,mins,tHrs[],tMins[],eventLength,nrc;
	String subject[];
	AlarmManager am;
	Intent intent[],newDayIntent;
	PendingIntent pendingIntent[],newDayPendingIntent;
	Calendar calendar,scheduleTimes[];
	Date now;
	AppDb data;
	SharedPreferences myPrefs;
	SharedPreferences.Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		data= new AppDb(this);
		data.open();
		
		tb = (ToggleButton) findViewById(R.id.tbService);
		co = (EditText) findViewById(R.id.etDefaulter);
		
		//data.open();
		co.setText(""+data.getCutOff());
		//data.close();
		myPrefs = SettingsAct.this.getSharedPreferences("myprefs", MODE_PRIVATE);
		boolean alarmUp = myPrefs.getBoolean("notification_switch", false);
		if(alarmUp)
			tb.setChecked(true);
		tb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				if(tb.isChecked()){
					/*Intent i = new Intent("com.akshayb.attendance.LOGGERSERVICE");
					startService(i);*/
					SharedPreferences.Editor prefsEditor = myPrefs.edit();
					nrc = myPrefs.getInt("NRC", 100);
					//setupIntents();
					//setupNotifications();
					setupNewDay();
			        prefsEditor.putBoolean("notification_switch", true);
			        prefsEditor.putInt("NRC", nrc);
			        prefsEditor.commit();
					tb.setChecked(true);
				}else{
					/*Intent i = new Intent(SettingsAct.this,LoggerService.class);
					stopService(i);*/
					/*setupIntents();
					for(int i = 0;i<eventLength;i++){
						am.cancel(pendingIntent[i]);
					}*/
					SharedPreferences.Editor prefsEditor = myPrefs.edit();
			        prefsEditor.putBoolean("notification_switch", false);
			        prefsEditor.commit();
					tb.setChecked(false);
					/*newDayIntent = new Intent(SettingsAct.this,NewDayNotificationSetupReceiver.class);
					//newDayIntent.putExtra("mode", MODE_WORLD_READABLE);
					/*Calendar time = Calendar.getInstance();
					time.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 0, 1);
					time.add(Calendar.DATE, 1);
					newDayPendingIntent = PendingIntent.getBroadcast(SettingsAct.this, 0, newDayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					am.cancel(newDayPendingIntent);*/
				}
			}
		});
		rst = (Button) findViewById(R.id.bReset);
		rst.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingsAct.this);
			      builder.setTitle("Reset the application?")
			             .setMessage("This will delete all your saved data in the application. Are you sure?")
			             .setCancelable(true)
			             .setPositiveButton("Confirm Reset", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                    data.resetDatabase();
			                    Intent opener = new Intent("com.akshayb.attendance.SETUPACTIVITY");
			    				startActivity(opener);
			               }
			           })
			           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			               }
			           });
			     AlertDialog alert = builder.create();
			     alert.show();
			}
		});
		share = (Button) findViewById(R.id.bShare);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AsyncSendDatabase(SettingsAct.this).execute();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(!co.getText().toString().equals("")){
		cutoff=Integer.parseInt(co.getText().toString());
		if(cutoff<=100){
			AppDb data= new AppDb(this);
			data.open();
			data.setCutOff(cutoff);
			data.close();
		}
		}
		//finish();
	}
	public boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.akshayb.attendance.LoggerService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		/*Intent intent = new Intent("com.akshayb.attendance.SETUPACTIVITY");
		startActivity(intent);*/
		finish();
		overridePendingTransition(0, R.anim.shrink_from_topleft_to_bottomright);
	}
	
	/*private void setupNotifications(){
		for(int i = 0;i<eventLength;i++){
			am.set(AlarmManager.RTC_WAKEUP, scheduleTimes[i].getTimeInMillis(), pendingIntent[i]);
		}
	}
	
	
	private void setupIntents(){
		calendar = Calendar.getInstance();
		day = calendar.get(Calendar.DAY_OF_WEEK);//day=(day==7?0:day);
		hrs = calendar.get(Calendar.HOUR_OF_DAY);
		mins = calendar.get(Calendar.MINUTE);
		Log.w("app",day+" "+hrs+":"+mins);
		data.open();
		eventLength=data.getNextEventLength(day,hrs,mins);
		Log.w("app", "Event Length"+eventLength);
		eventLength=data.getNextEventLength(day,hrs,mins);
		tHrs = new int[eventLength];
		tMins = new int[eventLength];
		subject = new String[eventLength];
		intent = new Intent[eventLength];
		scheduleTimes = new Calendar[eventLength];
		pendingIntent = new PendingIntent[eventLength];
		//timer = new Timer[eventLength];
		//scheds = new Date[eventLength];
		Cursor c = data.getNextSchDay(day, hrs, mins);
		c.moveToFirst();
		int iHrs = c.getColumnIndex(AppDb.TIMETABLE_HOUR);
		int iMins = c.getColumnIndex(AppDb.TIMETABLE_MINUTES);
		int iSubs = c.getColumnIndex(AppDb.TIMETABLE_SUBJECT);
		for(int i=0;i<eventLength;i++){
			tHrs[i] = c.getInt(iHrs);
			tMins[i] = c.getInt(iMins);
			subject[i] = c.getString(iSubs);
			c.moveToNext();
		}
		for(int i = 0;i<eventLength;i++){
			intent[i] = new Intent(SettingsAct.this,NotificationReceiver.class);
			intent[i].putExtra("SubName", subject[i]);
			intent[i].putExtra("Hour", tHrs[i]);
			intent[i].putExtra("Min", tMins[i]);
			intent[i].putExtra("NotID", i);
			intent[i].putExtra("mode", MODE_WORLD_READABLE);
			scheduleTimes[i] = Calendar.getInstance();
			//scheduleTimes[i].clear();
			//scheduleTimes[i].set(Calendar.HOUR, tHrs[i]);
			//scheduleTimes[i].set(Calendar.MINUTE, tMins[i]);
			//scheduleTimes[i].s
			//nrc = myPrefs.getInt("NRC", 100);
			scheduleTimes[i].set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), tHrs[i], tMins[i]);
			pendingIntent[i] = PendingIntent.getBroadcast(SettingsAct.this, nrc, intent[i], PendingIntent.FLAG_ONE_SHOT);
			nrc++;
			Log.w("app", "Set "+subject[i]);
		}
	}*/
	
	private void setupNewDay(){
		newDayIntent = new Intent(SettingsAct.this,NewDayNotificationSetupReceiver.class);
		//newDayIntent.putExtra("mode", MODE_WORLD_READABLE);
		/*Calendar time = Calendar.getInstance();
		time.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 0, 1);
		time.add(Calendar.DATE, 1);*/
		Calendar time=Calendar.getInstance();

	    time.add(Calendar.DATE, 1);
	    time.set(Calendar.HOUR,1);
	    time.set(Calendar.MINUTE,1);
	    time.set(Calendar.SECOND,0);
	    time.set(Calendar.MILLISECOND,0);
		newDayPendingIntent = PendingIntent.getBroadcast(SettingsAct.this,nrc++, newDayIntent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), newDayPendingIntent);
		//Log.w("app", "Set New Day");
		//am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 1000, newDayPendingIntent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
}

package com.akshayb.attendance2;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class NewDayNotificationSetupReceiver extends BroadcastReceiver {

	AlarmManager am;
	Intent intent[],newDayIntent;
	PendingIntent pendingIntent[],newDayPendingIntent;
	Calendar calendar,scheduleTimes[];
	Date now;
	AppDb data;
	SharedPreferences myPrefs;
	SharedPreferences.Editor prefEditor;
	int day,hrs,mins,tHrs[],tMins[],eventLength,mode,nrc;
	String subject[];
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.w("Yayy!!", "Got this");
		myPrefs = context.getSharedPreferences("myprefs", Context.MODE_WORLD_READABLE);
		nrc = myPrefs.getInt("NRC", 100);
		prefEditor = myPrefs.edit();
		if(myPrefs.getBoolean("notification_switch", false)){
			setupIntents(context);
			setupNewDay(context);
			/*NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification = new Notification(R.drawable.noticon,"New Notifications set", System.currentTimeMillis());
	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,new Intent(), 0);
	        notification.setLatestEventInfo(context, "New Notifications set", "", contentIntent);
	        notification.flags=Notification.FLAG_AUTO_CANCEL;
	        long[] vibrate = {0,100,200,300};
	        notification.vibrate = vibrate;
	        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
	        notification.ledARGB = 0xff00ff00;
	        notification.ledOnMS = 300;
	        notification.ledOffMS = 2000;
	        manager.notify(123, notification);*/
		}
		prefEditor.putInt("NRC", nrc);
		prefEditor.commit();
	}
	
	
	private void setupIntents(Context context){
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		calendar = Calendar.getInstance();
		day = calendar.get(Calendar.DAY_OF_WEEK);day=(day==7?0:day);
		hrs = calendar.get(Calendar.HOUR_OF_DAY);
		mins = calendar.get(Calendar.MINUTE);
		Log.w("app",day+" "+hrs+":"+mins);
		data = new AppDb(context);
		data.open();
		eventLength=data.getNextEventLength(day,hrs,mins);
		Log.w("app", "Event Length "+eventLength);
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
		data.close();
		for(int i = 0;i<eventLength;i++){
			intent[i] = new Intent(context,NotificationReceiver.class);
			intent[i].putExtra("SubName", subject[i]);
			intent[i].putExtra("Hour", tHrs[i]);
			intent[i].putExtra("Min", tMins[i]);
			intent[i].putExtra("NotID", i);
			scheduleTimes[i] = Calendar.getInstance();
			//scheduleTimes[i].clear();
			//scheduleTimes[i].set(Calendar.HOUR, tHrs[i]);
			//scheduleTimes[i].set(Calendar.MINUTE, tMins[i]);
			//scheduleTimes[i].s
			scheduleTimes[i].set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), tHrs[i], tMins[i]);
			pendingIntent[i] = PendingIntent.getBroadcast(context, nrc, intent[i], PendingIntent.FLAG_ONE_SHOT);
			nrc++;
			am.set(AlarmManager.RTC_WAKEUP, scheduleTimes[i].getTimeInMillis(), pendingIntent[i]);
			//Log.w("app", "Set "+subject[i]);
		}
	}
	
	private void setupNewDay(Context context){
		newDayIntent = new Intent(context,NewDayNotificationSetupReceiver.class);
		newDayIntent.putExtra("mode", mode);
		/*Calendar time = Calendar.getInstance();
		time.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 0, 1);
		time.add(Calendar.DATE, 1);*/
		Calendar time=Calendar.getInstance();

	    time.add(Calendar.DATE, 1);
	    time.set(Calendar.HOUR_OF_DAY,0);
	    time.set(Calendar.MINUTE,0);
	    time.set(Calendar.SECOND,1);
	    time.set(Calendar.MILLISECOND,0);
		newDayPendingIntent = PendingIntent.getBroadcast(context, nrc, newDayIntent, PendingIntent.FLAG_ONE_SHOT);
		nrc++;
		am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), newDayPendingIntent);
		//Log.w("app", ""+time.getTime().toString());
		//Log.w("app", "Set New Day");
	}

}

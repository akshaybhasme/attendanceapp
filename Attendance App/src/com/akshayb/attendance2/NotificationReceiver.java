package com.akshayb.attendance2;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NotificationReceiver extends BroadcastReceiver {
	
	SharedPreferences myPrefs;
	int mode;
	AppDb data;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		mode = intent.getExtras().getInt("mode");
		myPrefs = context.getSharedPreferences("myprefs", mode);
		data = new AppDb(context);
		data.open();
		if(myPrefs.getBoolean("notification_switch", false)){
			String subject = intent.getExtras().getString("SubName");
			int hour = intent.getExtras().getInt("Hour");
			int min = intent.getExtras().getInt("Min");
			int id = intent.getExtras().getInt("NotID");
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			day = (day == 7 ? 0 : day);
			if(data.checkEventExists(day, subject, hour, min)){
				NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		        Notification notification = new Notification(R.drawable.noticon,"Scheduled Lecture", System.currentTimeMillis());
		        Intent i = new Intent(context, AttendanceLogActivity.class);
		        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        i.putExtra("subject", subject);
		        i.putExtra("hour", hour);
		        i.putExtra("min", min);
		        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,i, 0);
		        notification.setLatestEventInfo(context, "Are you attending?", subject, contentIntent);
		        notification.flags=Notification.FLAG_AUTO_CANCEL;
		        long[] vibrate = {0,100,200,300};
		        notification.vibrate = vibrate;
		        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		        notification.ledARGB = 0xff00ff00;
		        notification.ledOnMS = 300;
		        notification.ledOffMS = 2000;
		        manager.notify(id, notification);
			}
			
		}
		
	}

}

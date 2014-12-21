package com.akshayb.attendance2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.akshayb.attendance2.elements.Event;
import com.akshayb.attendance2.elements.Subject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class AsyncSendDatabase extends AsyncTask<String, Integer, String> {

	private ProgressDialog pd;
	private Context context;
	private AppDb data;
	
	public AsyncSendDatabase(Context context){
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(context, "", "Please wait...");
		data = new AppDb(context);
		data.open();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try{
			List<Subject> subjects = getSubjects();
			List<Event> events = getEvents();
			JSONObject root = new JSONObject();
			JSONArray subJsonArray = new JSONArray();
			JSONArray eventJsonArray = new JSONArray();
			for(Subject subject : subjects){
				JSONObject subJsonObject = new JSONObject();
				subJsonObject.put("id", subject.getId());
				subJsonObject.put("name", subject.getName());
				subJsonObject.put("prof", subject.getProf());
				subJsonObject.put("attended", subject.getAttended());
				subJsonObject.put("total", subject.getTotal());
				subJsonArray.put(subJsonObject);
			}
			for(Event event : events){
				JSONObject eventJsonObject = new JSONObject();
				eventJsonObject.put("id", event.getId());
				eventJsonObject.put("subjectname", event.getSubjectName());
				eventJsonObject.put("hour", event.getHour());
				eventJsonObject.put("minute", event.getMinute());
				eventJsonObject.put("day", event.getDay());
				eventJsonArray.put(eventJsonObject);
			}
			root.put("subjects", subJsonArray);
			root.put("events", eventJsonArray);
			root.put("cutoff", data.getCutOff());
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			String fName = "AttendanceAppDB_"+System.currentTimeMillis()+".attapp";
			File file = new File(path, fName);
			try {
				fOut = new FileOutputStream(file);
				fOut.write(root.toString().getBytes());
				fOut.flush();
				fOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			//i.setType("application/data");
			i.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath())); 
			context.startActivity(Intent.createChooser(i, "Share Database"));
		}catch (JSONException e) {
			
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(pd.isShowing()){
			pd.dismiss();
		}
		data.close();
		super.onPostExecute(result);
	}
	
	private List<Subject> getSubjects(){
		List<Subject> subjects = new ArrayList<Subject>();
		Cursor c = data.getAllSubjects();
		c.moveToFirst();
		int iName = c.getColumnIndex("sub_name");
		int iProf = c.getColumnIndex("sub_prof");
		int iAtt = c.getColumnIndex(AppDb.SUBJECT_ATTENDED);
		int iTot = c.getColumnIndex(AppDb.SUBJECT_HAPPENED);
		for(int i=0;i<c.getCount();i++){
			Subject subject = new Subject();
			subject.setId(c.getInt(c.getColumnIndex(AppDb.SUBJECT_ID)));
			subject.setName(c.getString(iName));
			subject.setProf(c.getString(iProf));
			subject.setAttended(c.getInt(iAtt));
			subject.setTotal(c.getInt(iTot));
			subjects.add(subject);
			c.moveToNext();
		}
		c.close();
		return subjects;
	}
	
	private List<Event> getEvents(){
		List<Event> list = new ArrayList<Event>();
		Cursor c = data.getAllEvents();
		c.moveToFirst();
		int iHour = c.getColumnIndex(AppDb.TIMETABLE_HOUR);
		int iMins = c.getColumnIndex(AppDb.TIMETABLE_MINUTES);
		int iSubs = c.getColumnIndex(AppDb.TIMETABLE_SUBJECT);
		int iID = c.getColumnIndex(AppDb.TIMETABLE_ID);
		int iDay = c.getColumnIndex(AppDb.TIMETABLE_DAY);
		for(int i=0;i<c.getCount();i++){
			Event event = new Event();
			event.setId(c.getInt(iID));
			event.setSubjectName(c.getString(iSubs));
			event.setHour(c.getInt(iHour));
			event.setMinute(c.getInt(iMins));
			event.setDay(c.getInt(iDay));
			list.add(event);
			c.moveToNext();
		}
		c.close();
		return list;
	}

}

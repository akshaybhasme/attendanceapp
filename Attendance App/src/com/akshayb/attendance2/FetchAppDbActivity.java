package com.akshayb.attendance2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.akshayb.attendance2.elements.Event;
import com.akshayb.attendance2.elements.Subject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class FetchAppDbActivity extends Activity {

	private ExpandableListView expandableListView;
	private FetchListAdapter adapter;
	private String weekDays[] = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fetch_app_db);
		try{
			Uri filePath = getIntent().getData();
			String data = getStringFromFile(filePath.getPath());
			JSONObject root = new JSONObject(data);
			adapter = new FetchListAdapter(FetchAppDbActivity.this);
			adapter.addSubjects(getSubjects(root));
			adapter.addEvents(getEvents(root));
			expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
			expandableListView.setAdapter(adapter);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private EventGroup getEvents(JSONObject root) {
		try{
			JSONArray eventJsonArray = root.getJSONArray("events");
			List<Event> events = new ArrayList<Event>();
			for(int i=0; i<eventJsonArray.length(); i++){
				JSONObject eventJsonObject = eventJsonArray.getJSONObject(i);
				Event event = new Event();
				event.setId(eventJsonObject.getInt("id"));
				event.setSubjectName(eventJsonObject.getString("subjectname"));
				event.setHour(eventJsonObject.getInt("hour"));
				event.setMinute(eventJsonObject.getInt("minute"));
				event.setDay(eventJsonObject.getInt("day"));
				events.add(event);
			}
			EventGroup eventGroup = new EventGroup("events", (ArrayList<Event>) events);
			return eventGroup;
		}catch (JSONException e) {
			return null;
		}	
	}

	private SubjectGroup getSubjects(JSONObject root) {
		try{
			JSONArray subjectJsonArray = root.getJSONArray("subjects");
			List<Subject> subjects = new ArrayList<Subject>();
			for(int i=0; i<subjectJsonArray.length(); i++){
				JSONObject subJsonObject = subjectJsonArray.getJSONObject(i);
				Subject subject = new Subject();
				subject.setId(subJsonObject.getInt("id"));
				subject.setName(subJsonObject.getString("name"));
				subject.setProf(subJsonObject.getString("prof"));
				subject.setAttended(subJsonObject.getInt("attended"));
				subject.setTotal(subJsonObject.getInt("total"));
				subjects.add(subject);
			}
			SubjectGroup subjectGroup = new SubjectGroup("subjects", (ArrayList<Subject>) subjects);
			return subjectGroup;
		}catch (JSONException e) {
			return null;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fetch_app_db, menu);
		return true;
	}
	
	private String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    return sb.toString();
	}

	private String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(fl);
	    String ret = convertStreamToString(fin);
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}
	
	public class FetchListAdapter extends BaseExpandableListAdapter{
		
		private List<Group> group;
		private LayoutInflater mInflater;
		
		public FetchListAdapter(Context context){
			mInflater = LayoutInflater.from(context);
			group = new ArrayList<FetchAppDbActivity.Group>();
		}
		
		public void addSubjects(SubjectGroup subjectGroup){
			group.add(subjectGroup);
		}
		
		public void addEvents(EventGroup eventGroup){
			group.add(eventGroup);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			Group g = group.get(groupPosition);
			if(g.getName().equals("subjects")){
				SubjectGroup sg = (SubjectGroup) g;
				return sg.getSubjects().get(childPosition);
			}else if(g.getName().equals("events")){
				EventGroup eg = (EventGroup) g;
				return eg.getEvents().get(childPosition);
			}
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			Group g = group.get(groupPosition);
			if(g.getName().equals("subjects")){
				SubjectViewHolder holder;
				SubjectGroup sg = (SubjectGroup) g;
				//if (convertView == null) {
					convertView	= mInflater.inflate(R.layout.sync_subject, null);
					
					holder 		= new SubjectViewHolder();
					holder.subject = (CheckBox) convertView.findViewById(R.id.checkBox1);
					convertView.setTag(holder);
				/*} else {
					holder = (SubjectViewHolder) convertView.getTag();
				}*/
		
				holder.subject.setText(sg.getSubjects().get(childPosition).getName());
				holder.subject.setChecked(true);
				
				return convertView;
			}else if(g.getName().equals("events")){
				EventGroup eg = (EventGroup) g;
				EventViewHolder holder;
				//if (convertView == null) {
					convertView	= mInflater.inflate(R.layout.sync_event, null);
					
					holder 		= new EventViewHolder();
					holder.subject = (CheckBox) convertView.findViewById(R.id.tvScheduleName);
					holder.hour = (TextView) convertView.findViewById(R.id.tvHours);
					holder.minute = (TextView) convertView.findViewById(R.id.tvMins);
					holder.median = (TextView) convertView.findViewById(R.id.tvMedian);
					holder.hour = (TextView) convertView.findViewById(R.id.tvHours);
					holder.day = (TextView) convertView.findViewById(R.id.textView1);
					convertView.setTag(holder);
				/*} else {
					holder = (EventViewHolder) convertView.getTag();
				}*/
		
				holder.subject.setText(eg.getEvents().get(childPosition).getSubjectName());
				holder.subject.setChecked(true);
				holder.hour.setText(eg.getEvents().get(childPosition).getHour()+"");
				holder.minute.setText(eg.getEvents().get(childPosition).getMinute()+"");
				holder.day.setText(weekDays[eg.getEvents().get(childPosition).getDay()]);
				return convertView;
			}
			return null;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if(group.get(groupPosition).getName().equals("subjects")){
				SubjectGroup subjectGroup = (SubjectGroup) group.get(groupPosition);
				return subjectGroup.getSubjects().size();
			}else{
				EventGroup eventGroup = (EventGroup) group.get(groupPosition);
				return eventGroup.getEvents().size();
			}
		}

		@Override
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHeaderHolder holder;
			if (convertView == null) {
				convertView	= mInflater.inflate(R.layout.group_header, null);
				
				holder 		= new GroupHeaderHolder();
				holder.groupName = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else {
				holder = (GroupHeaderHolder) convertView.getTag();
			}
			if(group.get(groupPosition).getName().equals("subjects")){
				holder.groupName.setText("Subjects");
			}else{
				holder.groupName.setText("Events");
			}
			
			return convertView;
			//return null;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
	
	public static class GroupHeaderHolder{
		TextView groupName;
	}
	
	public static class SubjectViewHolder{
		CheckBox subject;
	}
	
	public static class EventViewHolder{
		TextView day, hour, minute, median;
		CheckBox subject;
	}
	
	public class Group{
		private String name;
		public Group(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public class SubjectGroup extends Group {
		private ArrayList<Subject> subjects;
		public SubjectGroup(String name, ArrayList<Subject> subjects) {
			super(name);
			this.subjects = subjects;
		}
		public ArrayList<Subject> getSubjects() {
			return subjects;
		}
		public void setSubjects(ArrayList<Subject> subjects) {
			this.subjects = subjects;
		}
	}
	
	public class EventGroup extends Group {
		private ArrayList<Event> events;
		public EventGroup(String name, ArrayList<Event> events) {
			super(name);
			this.events = events;
		}
		public ArrayList<Event> getEvents() {
			return events;
		}
		public void setEvents(ArrayList<Event> events) {
			this.events = events;
		}
	}

}

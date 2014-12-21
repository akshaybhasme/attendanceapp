package com.akshayb.attendance2;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimetableAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<String> schedule;
	private List<Integer> hours,minutes;
	
	public TimetableAdapter(Context context){
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return schedule.size();
	}

	@Override
	public Object getItem(int arg0) {
		return schedule.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void emptyListView(){
		schedule=null;
		hours=null;
		minutes=null;
	}
	
	public void setData(List<String> schedule,List<Integer> hours,List<Integer> minutes){
		this.schedule=schedule;
		this.hours=hours;
		this.minutes=minutes;
		notifyDataSetChanged();
	}
	
	public void add(String sch, int hour, int min){
		schedule.add(sch);
		hours.add(hour);
		minutes.add(min);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
			if (convertView == null) {
				convertView	= mInflater.inflate(R.layout.timetable_list, null);
				
				holder 		= new ViewHolder();
				
				holder.schedule	= (TextView) convertView.findViewById(R.id.tvScheduleName);
				holder.hours = (TextView) convertView.findViewById(R.id.tvHours);
				holder.minutes = (TextView) convertView.findViewById(R.id.tvMins);
				holder.median = (TextView) convertView.findViewById(R.id.tvMedian);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.schedule.setText(schedule.get(position));
			if(hours.get(position)>12){
				holder.hours.setText(""+(hours.get(position)-12));
				holder.median.setText("PM");
				}
			else
				holder.hours.setText(""+(hours.get(position)));
			if(minutes.get(position)<10)
				holder.minutes.setText("0"+minutes.get(position));
			else
				holder.minutes.setText(""+minutes.get(position));
			
			return convertView;
	}
	
	static class ViewHolder{
		TextView schedule,hours,minutes,median;
	}


}

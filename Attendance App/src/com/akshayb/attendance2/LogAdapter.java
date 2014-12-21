package com.akshayb.attendance2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LogAdapter extends BaseAdapter {

	String[] SName,status,datetime;
	private LayoutInflater mInflater;
	
	public LogAdapter(Context context){
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(String[] SName, String[] status, String[] datetime){
		this.SName = SName;
		this.status = status;
		this.datetime = datetime;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return SName!=null?SName.length:0;
	}

	@Override
	public Object getItem(int arg0) {
		return SName[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView	= mInflater.inflate(R.layout.log_list, null);
			
			holder 		= new ViewHolder();
			
			holder.SName	= (TextView) convertView.findViewById(R.id.tvSubject);
			holder.status = (TextView) convertView.findViewById(R.id.tvStatus);
			holder.datetime = (TextView) convertView.findViewById(R.id.tvDateTime);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.SName.setText(SName[position]);
		holder.status.setText(status[position]);
		holder.datetime.setText(datetime[position]);
		
		if(status[position].equals("Attended")){
			holder.status.setTextColor(Color.parseColor("#3FB8AF"));
		}
		else{
			holder.status.setTextColor(Color.parseColor("#FF3D7F"));
		}
		
		return convertView;
	}

	static class ViewHolder{
		TextView SName,status,datetime;
	}
	
}

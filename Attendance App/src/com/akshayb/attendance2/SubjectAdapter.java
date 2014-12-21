package com.akshayb.attendance2;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SubjectAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> sName;
	private List<Integer> attended,total;
	private int cutoff;
	
	public SubjectAdapter(Context context){
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return sName.size();
	}

	@Override
	public Object getItem(int arg0) {
		return sName.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void setData(List<String> SName,List<Integer> attended,List<Integer> total,int cutoff){
		this.sName=SName;
		this.attended=attended;
		this.total=total;
		this.cutoff=cutoff;
		notifyDataSetChanged();
	}
	
	public String getSubjectName(int position){
		return sName.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
			
			if (convertView == null) {
				convertView	= mInflater.inflate(R.layout.subjectlist, null);
				
				holder 		= new ViewHolder();
				
				holder.perc = (TextView) convertView.findViewById(R.id.tvPerc);
				holder.SubjectName	= (TextView) convertView.findViewById(R.id.tvSName);
				holder.attended = (TextView) convertView.findViewById(R.id.tvAttended);
				holder.total = (TextView) convertView.findViewById(R.id.tvTotal);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
	
			holder.SubjectName.setText(sName.get(position));
			holder.attended.setText(""+attended.get(position));
			holder.total.setText(""+total.get(position));
			try{
				holder.perc.setText(""+(int)(((float) attended.get(position)/(float) total.get(position))*100)+"%");
			}catch (Exception e) {
			}
			
			if(attended.get(position)!=0){
				if(calc(attended.get(position),total.get(position)))
					holder.perc.setTextColor(Color.parseColor("#3FB8AF"));//Blue
				else
					holder.perc.setTextColor(Color.parseColor("#FF3D7F"));//Red
			}else{
				holder.perc.setTextColor(Color.BLACK);
			}
			
			return convertView;
	}
	
	private boolean calc(int attended,int total){
		float abyt=(float) attended/total;
		float co =(float) cutoff/100;
		if(abyt>co) return true;
		return false;
	}
	
	static class ViewHolder{
		TextView SubjectName,attended,total, perc;
	}

}

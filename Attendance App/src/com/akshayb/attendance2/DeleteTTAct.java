package com.akshayb.attendance2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class DeleteTTAct extends Activity {
	
	private TextView text;
	private Button confirm;
	private AppDb data;
	private int hour,min,id;
	private String sname;
	private TimePicker tp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deltt);
		try{
			text = (TextView) findViewById(R.id.tvDel);
			confirm = (Button) findViewById(R.id.bDelSubConfirm);
			tp = (TimePicker) findViewById(R.id.timePicker1);
			Bundle extras = getIntent().getExtras(); 
			if(extras !=null) {
			    hour = extras.getInt("hour");
			    min = extras.getInt("mins");
			    sname = extras.getString("sub");
			    id = extras.getInt("id");
			}
			tp.setCurrentHour(hour);
			tp.setCurrentMinute(min);
			setTitle(sname);
			data = new AppDb(this);
			data.open();
			text.setText("Edit "+sname+" at "+hour+":"+min+"?");
			confirm.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					data.open();
					if(data.updateEventByID(id, tp.getCurrentHour(), tp.getCurrentMinute())){
						setResult(RESULT_OK);
						finish();
					}
				}
			});
			
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
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
}

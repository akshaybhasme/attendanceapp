package com.akshayb.attendance2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ViewLog extends SherlockActivity {

	//private Button newLog;
	private ListView mList;
	private String[] SName,status,datetime;
	private AppDb data;
	private int logLength,ids[];
	private LogAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		data = new AppDb(this);
		data.open();
		getData();
		
		mList 			= (ListView) findViewById(R.id.subjectlist);
		//mList.setDivider(null);
		TextView emptyview = (TextView) findViewById(R.id.tvLogEmpty);
		mList.setEmptyView(emptyview);
        
        adapter = new LogAdapter(this);
        
        adapter.setData(SName,status,datetime);

		View view = new View(this);
		view.setPadding(10, 10, 10, 20);
		mList.addHeaderView(view);
		
        mList.setAdapter(adapter);
        
        mList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showLongPressMenu(arg2-1);
				return true;
			}
		});
		
	}

	private void getData() {
		logLength = data.getLogCount();
		SName = new String[logLength];
		status = new String[logLength];
		datetime = new String[logLength];
		ids = new int[logLength];
		Cursor c = data.get_all_log();
		c.moveToLast();
		int iID = c.getColumnIndex(AppDb.LOG_ID);
		int iSName = c.getColumnIndex(AppDb.LOG_SUBJECT);
		int iDT = c.getColumnIndex(AppDb.LOG_DATE_TIME);
		int iStatus = c.getColumnIndex(AppDb.LOG_STATUS);
		for(int i=0;i<logLength;i++){
			ids[i] = c.getInt(iID);
			SName[i] = new String();
			SName[i] = c.getString(iSName);
			status[i] = new String();
			status[i] = c.getString(iStatus);
			datetime[i] = new String();
			datetime[i] = c.getString(iDT);
			c.moveToPrevious();
		}
		c.close();
	}

	private void showLongPressMenu(final int selected){
		final CharSequence[] items = {"Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLog.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].equals("Delete")){
                	AlertDialog.Builder builder = new AlertDialog.Builder(ViewLog.this);
				      builder.setTitle("Delete Subject")
				             .setMessage("Deleting log. Are you sure?")
				             .setCancelable(true)
				             .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {        
				            	   if(data.deleteLogById(ids[selected])){
										getData();
										adapter.setData(SName, status, datetime);
									}
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
            }
        });
        builder.show();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.shrink_from_topright_to_bottomleft);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_log, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent home = new Intent(getApplicationContext(), SetupActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			return true;
		
		case R.id.menu_about:
			Intent abt = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(abt);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			return true;
			
		case R.id.menu_settings:
			Intent s = new Intent(getApplicationContext(), SettingsAct.class);
			startActivity(s);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			return true;
			

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

package com.akshayb.attendance2;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ViewSubject extends SherlockActivity {
	
	public static final int EDITSUBJECT = 846;
	
	private AppDb data;
	private List<String> subjects,profs;
	private int cutoff;
	private List<Integer> attended, total;
	private ListView mList;
	private SubjectAdapter adapter;
	private View footerView;
	private TextView tvTotalPerc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basic);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		
		data = new AppDb(this);
		data.open();
		
		mList = (ListView) findViewById(R.id.subjectlist);
        adapter = new SubjectAdapter(this);
		
		getData();
		
		View view = new View(this);
		view.setPadding(10, 10, 10, 20);
		mList.addHeaderView(view);
		
        adapter.setData(subjects, attended, total,cutoff);
        footerView = LayoutInflater.from(ViewSubject.this).inflate(R.layout.subject_list_footer, null);
        tvTotalPerc = (TextView) footerView.findViewById(R.id.tvTotal);
        mList.addFooterView(footerView, null, false);
        setupFooter();
        mList.setAdapter(adapter);
        TextView emptyView = (TextView) findViewById(R.id.tvSubEmpty);
        emptyView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createSubject();
			}
		});
        mList.setEmptyView(emptyView);
        
        mList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showLongClickMenu(arg2-1);
				return true;
			}
		});
        
        mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int subjectPosition,
					long arg3) {
				//Minus one because of header view
				Intent det = new Intent(getApplicationContext(), SubjectDetails.class);
                det.putExtra("subname", subjects.get(subjectPosition-1));
                det.putExtra("attended", attended.get(subjectPosition-1));
                det.putExtra("total", total.get(subjectPosition-1));
                det.putExtra("cutoff", cutoff);
                startActivity(det);
			}
		});
	}
	
	private void setupFooter(){
		int avgTotal = 0, avgAttended = 0;
        for(int ele:total){
        	avgTotal += ele;
        }
        for(int ele:attended){
        	avgAttended += ele;
        }
        float totalPerc = (float) avgAttended/avgTotal;
        tvTotalPerc.setText(" "+(int)(totalPerc*100)+"%");
        if((totalPerc*100)<cutoff){
        	tvTotalPerc.setTextColor(Color.parseColor("#FF3D7F"));//red
        }else{
        	tvTotalPerc.setTextColor(Color.parseColor("#3FB8AF"));//blue
        }
	}
	
	private void getData(){
		subjects = new ArrayList<String>();
		profs = new ArrayList<String>();
		attended = new ArrayList<Integer>();
		total = new ArrayList<Integer>();
		
		Cursor c = data.getAllSubjects();
		c.moveToFirst();
		int iName = c.getColumnIndex("sub_name");
		int iProf = c.getColumnIndex("sub_prof");
		int iAtt = c.getColumnIndex(AppDb.SUBJECT_ATTENDED);
		int iTot = c.getColumnIndex(AppDb.SUBJECT_HAPPENED);
		for(int i=0;i<c.getCount();i++){
			subjects.add(c.getString(iName));
			profs.add(c.getString(iProf));
			attended.add(c.getInt(iAtt));
			total.add(c.getInt(iTot));
			c.moveToNext();
		}
		c.close();
		cutoff = data.getCutOff();
		
	}
	
	private void showLongClickMenu(final int subjectPosition){
		final CharSequence[] items = { "Show details", "Edit","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewSubject.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Show details")) {
                	Intent det = new Intent(getApplicationContext(), SubjectDetails.class);
                    det.putExtra("subname", subjects.get(subjectPosition));
                    det.putExtra("attended", attended.get(subjectPosition));
                    det.putExtra("total", total.get(subjectPosition));
                    det.putExtra("cutoff", cutoff);
                    startActivity(det);
                	
                } else if (items[item].equals("Edit")) {
                	Intent i = new Intent(getApplicationContext(), DeleteSubjectAct.class);
                    i.putExtra("subname", subjects.get(subjectPosition));
                    startActivityForResult(i, EDITSUBJECT);
                	
                }else if(items[item].equals("Delete")){
                	AlertDialog.Builder builder = new AlertDialog.Builder(ViewSubject.this);
				      builder.setTitle("Delete Subject")
				             .setMessage("Deleting a subject will also delete all its scheduled lectures. Are you sure?")
				             .setCancelable(true)
				             .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {        
				            	   if(data.deleteSubByName(subjects.get(subjectPosition))){
										getData();
										setupFooter();
										adapter.setData(subjects, attended, total, cutoff);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case EDITSUBJECT:
				getData();
				setupFooter();
				adapter.setData(subjects, attended, total, cutoff);
				return;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.shrink_from_bottomright_to_topleft);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_subjects, menu);
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
			
		case R.id.menu_settings:
			Intent s = new Intent(getApplicationContext(), AboutActivity.class);
			startActivity(s);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			return true;
			
		case R.id.menu_about:
			Intent abt = new Intent(getApplicationContext(), SettingsAct.class);
			startActivity(abt);
			overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.still);
			return true;
		
		case R.id.menu_add_subject:
			createSubject();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void createSubject() {
		Intent i = new Intent(getApplicationContext(), SetSubjectActivity.class);
		startActivityForResult(i, EDITSUBJECT);
		overridePendingTransition(R.anim.slide_in_top, R.anim.still);
	}
}

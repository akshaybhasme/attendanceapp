package com.akshayb.attendance2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeleteSubjectAct extends Activity implements View.OnClickListener {
	
	private TextView tvAtt,tvTot;
	private Button confirm,changeConfirm,attDec,attInc,totDec,totInc;
	private int att,tot;
	private AppDb data;
	private String sname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delsubj);
		try{
			init();
			Bundle extras = getIntent().getExtras(); 
			if(extras !=null) {
			    sname = extras.getString("subname");
			}
			setTitle(sname);
			data = new AppDb(this);
			data.open();
			getData();
			confirm.setOnClickListener(this);
			changeConfirm.setOnClickListener(this);
			attDec.setOnClickListener(this);
			attInc.setOnClickListener(this);
			totDec.setOnClickListener(this);
			totInc.setOnClickListener(this);
			//data.close();
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
	
	private void getData() {
		Cursor c = data.getSubByName(sname);
		c.moveToFirst();
		int iAtt=c.getColumnIndex(AppDb.SUBJECT_ATTENDED);
		int iTot=c.getColumnIndex(AppDb.SUBJECT_HAPPENED);
		att=c.getInt(iAtt);
		tot=c.getInt(iTot);
		tvAtt.setText(""+att);
		tvTot.setText(""+tot);
	}
	
	private void init() {
		confirm = (Button) findViewById(R.id.bDelSubConfirm);
		changeConfirm = (Button) findViewById(R.id.bConfirmChng);
		attDec = (Button) findViewById(R.id.bAttDec);
		attInc = (Button) findViewById(R.id.bAttInc);
		totDec = (Button) findViewById(R.id.bTotDec);
		totInc = (Button) findViewById(R.id.bTotInc);
		tvAtt = (TextView) findViewById(R.id.tvAtt);
		tvTot = (TextView) findViewById(R.id.tvTot);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.bDelSubConfirm:data.open();
									AlertDialog.Builder builder = new AlertDialog.Builder(DeleteSubjectAct.this);
								      builder.setTitle("Delete Subject")
								             .setMessage("Are you sure?")
								             .setCancelable(true)
								             .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
								               public void onClick(DialogInterface dialog, int id) {        
								            	   if(data.deleteSubByName(sname))
													{
														data.close();
														Intent i = new Intent("com.akshayb.attendance.VIEWSUBJECT");
														startActivity(i);
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
					break;
		case R.id.bConfirmChng:data.open();
								if(data.updateSubject(sname, att, tot)){
									data.close();
									setResult(RESULT_OK);
									finish();
								}
					break;
		case R.id.bAttDec:if(att-1>=0)tvAtt.setText(""+(--att));
					break;
		case R.id.bAttInc:if(att+1>tot){
							att++;tot++;
							tvAtt.setText(""+(att));
							tvTot.setText(""+(tot));
						}else tvAtt.setText(""+(++att));
					break;
		case R.id.bTotDec:if(tot-1>=0){
									if(tot-1<att){
									tvTot.setText(""+(--tot));
									tvAtt.setText(""+(--att));
									}else tvTot.setText(""+(--tot));
									}
					break;
		case R.id.bTotInc:tvTot.setText(""+(++tot));
					break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
}

package com.akshayb.attendance2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SetSubjectActivity extends Activity implements View.OnClickListener {
	
	EditText sname,pname;
	Button create;
	AppDb data;
	CheckBox pracs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject);
		data = new AppDb(this);
		sname = (EditText) findViewById(R.id.etSName);
		//pname = (EditText) findViewById(R.id.etPName);
		create = (Button) findViewById(R.id.bCreate);
		create.setOnClickListener(this);
		pracs = (CheckBox) findViewById(R.id.cbPracs);
	}

	@Override
	public void onClick(View v) {
		boolean didItWork = true;
		try{
			String subject = sname.getText().toString();
			String prof = "o";
		
		data.open();
		if(pracs.isChecked()){
			if(data.createSubject(subject, prof) && data.createSubject(subject+" Practicals", prof)){
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(0, R.anim.slide_out_top);
		}
		}else{
			if(data.createSubject(subject, prof)){
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(0, R.anim.slide_out_top);
		}
		
		}
		data.close();
		}catch(Exception e){
			didItWork = false;
			String error= e.toString();
			Dialog d = new Dialog(this);
			d.setTitle("Error");
			TextView tv = new TextView(this);
			tv.setText(error);
			d.setContentView(tv);
			d.show();
		}finally{
			if(didItWork){
			
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.slide_out_top);
	}
	

}



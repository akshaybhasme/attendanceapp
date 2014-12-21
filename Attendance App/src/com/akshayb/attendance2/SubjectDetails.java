package com.akshayb.attendance2;
 
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
 
public class SubjectDetails extends SherlockActivity {
 
    TextView subject,tvAttended,tvTotal,tvAtt_perc,tvAtt_req;
    String sname;
    int attended,total,cutoff,att_req;
    float att_perc;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_details);
         
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            sname = extras.getString("subname");
            attended = extras.getInt("attended");
            total = extras.getInt("total");
            cutoff = extras.getInt("cutoff");
        }
        setTitle(sname);
        tvAttended = (TextView) findViewById(R.id.tvSDAtt);
        tvAttended.setText(""+attended);
        tvTotal = (TextView) findViewById(R.id.tvSDTot);
        tvTotal.setText(""+total);
        att_perc=0;
        if(total!=0){
            att_perc = (float) attended/total;
        }
        tvAtt_perc = (TextView) findViewById(R.id.tvPercAtt);
        tvAtt_perc.setText(""+att_perc*100);
        // Required attendance calculations
        if(((float)attended/total)>((float)cutoff/100)){
        	att_req=0;
        }else{
        	int tAtt = attended, tTot = total;
        	float fl1 = (float)tAtt/tTot, fl2 = (float)cutoff/100;
        	Log.d("fl1", ""+fl1);
        	Log.d("fl2", ""+fl2);
            while(fl1<fl2){
            	tAtt++;
            	tTot++;
            	fl1 = (float) tAtt/tTot;
            }
            att_req = tAtt-attended;
        }
        tvAtt_req = (TextView) findViewById(R.id.tvAttReq);
        tvAtt_req.setText(""+att_req);
    }
 
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case android.R.id.home:
    			Intent intent = new Intent(getApplicationContext(), ViewSubject.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    			return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
 
}

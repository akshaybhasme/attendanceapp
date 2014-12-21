package com.akshayb.attendance2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ViewTT extends SherlockFragmentActivity implements View.OnClickListener {
	
	public static final int ADD = 645;
	
	private int day;
	private Button monday,tuesday,wednesday,thursday,friday,saturday,sunday;
	private AppDb data;
	//private String weekDays[] = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
	private TimetableAdapter[] adapter;
	private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ttbasic);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			day = extras.getInt("day");
		}else{
			Calendar calendar = Calendar.getInstance();
			day = calendar.get(Calendar.DAY_OF_WEEK);
			day=(day==7?0:day);
		}
		//Initialize
		data = new AppDb(this);
		data.open();
		data.getSubjectCount();
		monday = (Button) findViewById(R.id.bMonday);
		monday.setOnClickListener(this);
		tuesday = (Button) findViewById(R.id.bTuesday);
		tuesday.setOnClickListener(this);
		wednesday = (Button) findViewById(R.id.bWednesday);
		wednesday.setOnClickListener(this);
		thursday = (Button) findViewById(R.id.bThursday);
		thursday.setOnClickListener(this);
		friday = (Button) findViewById(R.id.bFriday);
		friday.setOnClickListener(this);
		saturday = (Button) findViewById(R.id.bSaturday);
		saturday.setOnClickListener(this);
		sunday = (Button) findViewById(R.id.bSunday);
		sunday.setOnClickListener(this);
		adapter=new TimetableAdapter[7];
		for(int i=0;i<7;i++){
			adapter[i] = new TimetableAdapter(this);
		}
		setDayScroll(day);
		setup();
	}
	
	
	
	private void setup(){
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	     // Set up the ViewPager with the sections adapter.
	     mViewPager = (ViewPager) findViewById(R.id.pager);
	     mViewPager.setAdapter(mSectionsPagerAdapter);
	     mViewPager.setCurrentItem(day, true);
	     mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				day = arg0;
				resetDayButtons();
				setDayScroll(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	private void setDayScroll(int day){
		switch(day){
		case 0:saturday.setBackgroundResource(0);
			saturday.setTextColor(Color.BLACK);
			break;
		case 1:sunday.setBackgroundResource(0);
			sunday.setTextColor(Color.BLACK);
			break;
		case 2:monday.setBackgroundResource(0);
			monday.setTextColor(Color.BLACK);
			break;
		case 3:tuesday.setBackgroundResource(0);
			tuesday.setTextColor(Color.BLACK);
			break;
		case 4:wednesday.setBackgroundResource(0);
			wednesday.setTextColor(Color.BLACK);
			break;
		case 5:thursday.setBackgroundResource(0);
			thursday.setTextColor(Color.BLACK);
			break;
		case 6:friday.setBackgroundResource(0);
			friday.setTextColor(Color.BLACK);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

	@Override
	public void onClick(View v) {
		
			switch(v.getId()){
			case R.id.bMonday: day =2;
						resetDayButtons();
						monday.setBackgroundResource(0);
						monday.setTextColor(Color.BLACK);
						break;
			case R.id.bTuesday: day=3;
						resetDayButtons();
						tuesday.setBackgroundResource(0);
						tuesday.setTextColor(Color.BLACK);
						break;
			case R.id.bWednesday: day=4;
						resetDayButtons();
						wednesday.setBackgroundResource(0);
						wednesday.setTextColor(Color.BLACK);
						break;
			case R.id.bThursday: day=5;
						resetDayButtons();
						thursday.setBackgroundResource(0);
						thursday.setTextColor(Color.BLACK);
						break;
			case R.id.bFriday: day=6;
						resetDayButtons();
						friday.setBackgroundResource(0);
						friday.setTextColor(Color.BLACK);
						break;
			case R.id.bSaturday: day=0;
						resetDayButtons();
						saturday.setBackgroundResource(0);
						saturday.setTextColor(Color.BLACK);
						break;
			case R.id.bSunday: day=1;
						resetDayButtons();
						sunday.setBackgroundResource(0);
						sunday.setTextColor(Color.BLACK);
						break;
			}
			mViewPager.setCurrentItem(day, true);
		
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0, R.anim.shrink_from_bottomleft_to_topright);
	}
	
	private void resetDayButtons(){
		monday.setBackgroundResource(R.drawable.daybutton);
		monday.setTextColor(Color.WHITE);
		tuesday.setBackgroundResource(R.drawable.daybutton);
		tuesday.setTextColor(Color.WHITE);
		wednesday.setBackgroundResource(R.drawable.daybutton);
		wednesday.setTextColor(Color.WHITE);
		thursday.setBackgroundResource(R.drawable.daybutton);
		thursday.setTextColor(Color.WHITE);
		friday.setBackgroundResource(R.drawable.daybutton);
		friday.setTextColor(Color.WHITE);
		saturday.setBackgroundResource(R.drawable.daybutton);
		saturday.setTextColor(Color.WHITE);
		sunday.setBackgroundResource(R.drawable.daybutton);
		sunday.setTextColor(Color.WHITE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		data.close();
	}
	
	/**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	DayFragment fragment[];
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragment = new DayFragment[7];
        }

        @Override
        public Fragment getItem(int currentDay) {
        	fragment[currentDay] = new DayFragment();
        	fragment[currentDay].setContext(ViewTT.this);
        	Bundle b = new Bundle();
        	b.putInt("Day", currentDay);
        	fragment[currentDay].setArguments(b);
            return fragment[currentDay];
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 2: return getString(R.string.title_section1);
                case 3: return getString(R.string.title_section2);
                case 4: return getString(R.string.title_section3);
                case 5: return getString(R.string.title_section4);
                case 6: return getString(R.string.title_section5);
                case 0: return getString(R.string.title_section6);
                case 1: return getString(R.string.title_section7);
            }
            return null;
        }
    }
    
    public static class DayFragment extends Fragment {
    	
    	private List<String> subjects;
    	private List<Integer> hour,min, ids;
    	private ListView lv;
    	private TextView tv;
    	private Context context;
    	private TimetableAdapter adapter;
    	private AppDb data;
    	private int currentDay;
        
        public void setContext(Context context){
        	this.context = context;
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	currentDay = getArguments().getInt("Day");
        	
        	View view = inflater.inflate(R.layout.frag_tt, null);
        	lv = (ListView) view.findViewById(R.id.listView1);
        	tv = (TextView) view.findViewById(R.id.textView1);
        	View footerView = inflater.inflate(R.layout.add_schedule, null);
        	lv.addFooterView(footerView);
        	data = new AppDb(context);
        	data.open();
        	
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					
		        	getData();
		        	lv.post(new Runnable() {
						
						@Override
						public void run() {
							adapter = new TimetableAdapter(context);
				    		adapter.setData(subjects, hour, min);
				    		
				    		View view = new View(getActivity());
				    		view.setPadding(10, 10, 10, 20);
				    		lv.addHeaderView(view);
				    		
				    		lv.setAdapter(adapter);
				    		if(subjects.size() != 0) tv.setVisibility(View.GONE);
				    		
				    		lv.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									//minus one because of header view
									if(arg2-1 < subjects.size()){
										Intent i = new Intent(context, DeleteTTAct.class);
										i.putExtra("day", currentDay);
					                    i.putExtra("sub", subjects.get(arg2-1));
					                    i.putExtra("hour", hour.get(arg2-1));
					                    i.putExtra("mins", min.get(arg2-1));
					                    i.putExtra("id", ids.get(arg2-1));
					                    startActivityForResult(i, 0);
									}else{
										launchCreateEvent();
									}
								}
							});
				    		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

								@Override
								public boolean onItemLongClick(
										AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
									if(arg2-1 < subjects.size()){
										showLongClickMenu(arg2-1);
										return true;
									}
									return false;
								}
							});
						}
					});
				}
			}).start();
        	
        	return view;
        }
        
        private void showLongClickMenu(final int eventPosition){
    		final CharSequence[] items = {"Edit","Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Show details")) {
                    	
                    	
                    } else if (items[item].equals("Edit")) {
                    	Intent i = new Intent(context, DeleteTTAct.class);
						i.putExtra("day", currentDay);
	                    i.putExtra("sub", subjects.get(eventPosition));
	                    i.putExtra("hour", hour.get(eventPosition));
	                    i.putExtra("mins", min.get(eventPosition));
	                    i.putExtra("id", ids.get(eventPosition));
	                    startActivityForResult(i, 0);
                    	
                    }else if(items[item].equals("Delete")){
                    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    				      builder.setTitle("Delete Subject")
    				             .setMessage("Deleting scheduled lecture. Are you sure?")
    				             .setCancelable(true)
    				             .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    				               public void onClick(DialogInterface dialog, int id) {        
    				            	   if(data.deleteTTEvent(ids.get(eventPosition))){
    										getData();
    										adapter.setData(subjects, hour, min);
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
        
        private void getData(){
        	subjects = new ArrayList<String>();
        	hour = new ArrayList<Integer>();
        	min = new ArrayList<Integer>();
        	ids = new ArrayList<Integer>();
        	Cursor c = data.getTTEventOfDay(currentDay);
			c.moveToFirst();
			int iHour = c.getColumnIndex(AppDb.TIMETABLE_HOUR);
			int iMins = c.getColumnIndex(AppDb.TIMETABLE_MINUTES);
			int iSubs = c.getColumnIndex(AppDb.TIMETABLE_SUBJECT);
			int iID = c.getColumnIndex(AppDb.TIMETABLE_ID);
			for(int i=0;i<c.getCount();i++){
				subjects.add(c.getString(iSubs));
				hour.add(c.getInt(iHour));
				min.add(c.getInt(iMins));
				ids.add(c.getInt(iID));
				c.moveToNext();
			}
			c.close();
        }
        
        @Override
        public void onActivityResult(int requestCode, int resultCode,
        		Intent data) {
        	if(resultCode == RESULT_OK){
        		getData();
        		adapter.setData(subjects, hour, min);
        	}
        	super.onActivityResult(requestCode, resultCode, data);
        }
        
        private void launchCreateEvent(){
    		if(data.getSubjectCount()==0){
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    		      builder.setTitle("No Subjects Created")
    		             .setMessage("Create a subject first?")
    		             .setCancelable(true)
    		             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		               public void onClick(DialogInterface dialog, int id) {        
    		            	   Intent intent = new Intent(context, SetSubjectActivity.class);
    		            	   startActivity(intent);
    		            	   ((Activity) context).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottom);
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
    		else{
    			Intent i = new Intent(context, SetTTActivity.class);
    			i.putExtra("today", currentDay);
    			startActivityForResult(i, ADD);
    			((Activity) context).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottom);
    		}
    	}

    }
	
}

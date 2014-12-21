package com.akshayb.attendance2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDb {
	
	public static final String DATABASE_NAME="app_db";
	public static final String SUBJECT_TABLE="subject";
	public static final String SUBJECT_ID="_id";
	public static final String SUBJECT_NAME="sub_name";
	public static final String SUBJECT_PROF="sub_prof";
	public static final String SUBJECT_ATTENDED="sub_att";
	public static final String SUBJECT_HAPPENED="sub_total";
	public static final String TIMETABLE="time_table";
	public static final String TIMETABLE_ID="_id";
	public static final String TIMETABLE_DAY="day_of_week";
	public static final String TIMETABLE_SUBJECT="subject";
	public static final String TIMETABLE_HOUR="time_hour";
	public static final String TIMETABLE_MINUTES="time_minutes";
	public static final String LOG = "log";
	public static final String LOG_ID="_id";
	public static final String LOG_SUBJECT="subject";
	//public static final String LOG_TIME="time";
	public static final String LOG_DATE_TIME="date_time";
	public static final String LOG_STATUS="status";
	public static int DATABASE_VERSION=14;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabse;
	
	public static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+SUBJECT_TABLE+
					" ("+SUBJECT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
					+SUBJECT_NAME+" TEXT NOT NULL, "
					+SUBJECT_PROF+" TEXT NOT NULL, "
					+SUBJECT_ATTENDED+" INTEGER, "
					+SUBJECT_HAPPENED+" INTEGER);"
					);
			db.execSQL("CREATE TABLE "+TIMETABLE+
					" ("+TIMETABLE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
					+TIMETABLE_DAY+" INTEGER, "
					+TIMETABLE_HOUR+" INTEGER, "
					+TIMETABLE_MINUTES+" INTEGER, "
					+TIMETABLE_SUBJECT+" TEXT);"
					);
			db.execSQL("CREATE TABLE cutoff(_id INTEGER PRIMARY KEY AUTOINCREMENT, co INTEGER);");
			ContentValues cv =new ContentValues();
			cv.put("_id", 1);
			cv.put("co", 75);
			db.insert("cutoff", null, cv);
			db.execSQL("CREATE TABLE "+LOG+
					" ("+LOG_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
					+LOG_SUBJECT+" TEXT, "
					+LOG_DATE_TIME+" TEXT, "
					+LOG_STATUS+" TEXT);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+SUBJECT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+TIMETABLE);
			db.execSQL("DROP TABLE IF EXISTS cutoff");
			db.execSQL("DROP TABLE IF EXISTS log");
			onCreate(db);
		}
		
	}
	public AppDb(Context c){
		ourContext=c;
	}
	public AppDb open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabse =ourHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		ourHelper.close();
	}
	public boolean createSubject(String sname, String pname) {
		if(sname.length()!=0){
		ContentValues cv =new ContentValues();
		cv.put(SUBJECT_NAME, sname);
		cv.put(SUBJECT_PROF, pname);
		cv.put(SUBJECT_ATTENDED, 0);
		cv.put(SUBJECT_HAPPENED, 0);
		ourDatabse.insert(SUBJECT_TABLE, null, cv);
		return true;
		}
		else return false;
	}
	public Cursor getAllSubjects() {
		return ourDatabse.rawQuery("select * from subject;", null);
	}
	public boolean createTtEvent(int day, String sid, int hour, int mins) {
		if(sid!=""){
			ContentValues cv = new ContentValues();
			cv.put(TIMETABLE_DAY, day);
			cv.put(TIMETABLE_HOUR, hour);
			cv.put(TIMETABLE_MINUTES, mins);
			cv.put(TIMETABLE_SUBJECT, sid);
			ourDatabse.insert(TIMETABLE, null, cv);
			return true;
			
		}else return false;
	}
	public Cursor getTTEventOfDay(int dayIndex) {
		return ourDatabse.rawQuery("select _id,time_hour,time_minutes,subject from time_table where day_of_week="+dayIndex+" order by time_hour;", null);
	}
	public int getSubjectCount(){
		Cursor c = ourDatabse.rawQuery("select count(*) from subject;", null);
		c.moveToFirst();
		int x =c.getInt(0);
		c.close();
		return x;
	}
	public int getEventLength(int day){
		Cursor c = ourDatabse.rawQuery("select count(*) from time_table where day_of_week="+day+";", null);
		c.moveToFirst();
		int x = c.getInt(0);
		c.close();
		return x;
	}
	public String getSubjectbyId(int sid){
		Cursor c = ourDatabse.rawQuery("select sub_name from subject where _id="+sid+";", null);
		c.moveToFirst();
		int iSub = c.getColumnIndex("sub_name");
		String x = c.getString(iSub);
		c.close();
		return x;
	}
	public boolean deleteSubByName(String id){
		return ourDatabse.delete(SUBJECT_TABLE, SUBJECT_NAME+"='"+id+"'", null)>0 && ourDatabse.delete(TIMETABLE, TIMETABLE_SUBJECT+"='"+id+"'", null)>=0;
		
	}
	public boolean deleteTTEvent(int id){
		return ourDatabse.delete(TIMETABLE, TIMETABLE_ID+"="+id, null)>0;
	}
	public Cursor getNextSchDay(int day,int hrs,int mins){
		return ourDatabse.rawQuery("select time_hour,time_minutes,subject from time_table where day_of_week="+day+" and time_hour>="+hrs+" and time_minutes>="+mins+";", null);
	}
	public int getNextEventLength(int day,int hrs,int mins){
		Cursor c = ourDatabse.rawQuery("select count(*) from time_table where day_of_week="+day+" and time_hour>="+hrs+" and time_minutes>="+mins+";", null);
		c.moveToFirst();
		int x = c.getInt(0);
		c.close();
		return x;
	}
	public boolean SubAttended(String sub){
		if(!sub.contentEquals("")){
			ourDatabse.execSQL("update subject set sub_att=sub_att+1,sub_total=sub_total+1 where sub_name='"+sub+"';");
			return true;
		}
		return false;
	}
	public boolean SubNotAttended(String sub){
		if(!sub.contentEquals("")){
			ourDatabse.execSQL("update subject set sub_total=sub_total+1 where sub_name='"+sub+"';");
			return true;
		}
		return false;
	}
	public boolean setCutOff(int x){
		ContentValues cv = new ContentValues();
		cv.put("co", x);
		ourDatabse.execSQL("update cutoff set co="+x+" where _id=1;");
		return true;
	}
	public int getCutOff(){
		Cursor c = ourDatabse.rawQuery("select co from cutoff where _id=1;", null);
		c.moveToFirst();
		int al= c.getColumnIndex("co");
		int x = c.getInt(al);
		c.close();
		return x;
	}
	public boolean updateSubject(String sub, int att,int tot){
		if(!sub.contentEquals("")){
			ourDatabse.execSQL("update subject set sub_total="+tot+",sub_att="+att+" where sub_name='"+sub+"';");
			return true;
		}
		return false;
	}
	public Cursor getSubByName(String name){
		return ourDatabse.rawQuery("select * from subject where sub_name='"+name+"';", null);
	}
	public boolean resetDatabase(){
		ourDatabse.execSQL("delete from subject");
		ourDatabse.execSQL("delete from time_table");
		ourDatabse.execSQL("delete from log");
		//ourDatabse.execSQL("delete from cutoff");
		return true;
	}
	
	public boolean new_log(String sname, String date_time,String status) {
		if(sname.length()!=0){
		ContentValues cv =new ContentValues();
		cv.put(LOG_SUBJECT, sname);
		cv.put(LOG_DATE_TIME, date_time);
		cv.put(LOG_STATUS, status);
		ourDatabse.insert(LOG, null, cv);
		return true;
		}
		else return false;
	}
	
	public Cursor get_all_log() {
		return ourDatabse.rawQuery("select * from log;", null);
	}

	public int getLogCount(){
		Cursor c = ourDatabse.rawQuery("select count(*) from log order by _id desc;", null);
		c.moveToFirst();
		int x =c.getInt(0);
		c.close();
		return x;
	}
	
	public boolean deleteLogById(int id){
		return ourDatabse.delete(LOG, LOG_ID+"="+id, null)>0;
	}
	
	public boolean checkEventExists(int day, String name, int hour, int min){
		Cursor c = ourDatabse.rawQuery("select count(*) from time_table where day_of_week="+day+" and subject='"+name+"' and time_hour="+hour+" and time_minutes="+min+";", null);
		c.moveToFirst();
		int x =c.getInt(0);
		c.close();
		if(x>0) return true;
		else return false;
	}
	
	 public boolean updateEventByID(int id, int hour, int min){
         ourDatabse.execSQL("update time_table set time_hour="+hour+",time_minutes="+min+" where _id='"+id+"';");
         return true;
	 }
	 
	 public Cursor getAllEvents(){
		 return ourDatabse.rawQuery("select * from time_table;", null);
	 }

	
}

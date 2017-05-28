package com.example.neba.cool;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.AdapterView;

public class DBHelper extends SQLiteOpenHelper {
public static final String DATABASE_NAME = "etrack.db";
public static final String TABLE1_NAME = "messages";
public static final String TABLE2_NAME = "ids";
public static final String TABLE3_NAME = "logintbl";
public static final String TABLE4_NAME = "settings";
public static final String COLUMN_ID = "id";
public static final String COLUMN_CONTENT = "content";
public static final String COLUMN_LOCATION = "location";
public static final String COLUMN_URL = "url";
public static final String COLUMN_SEEN = "seen";
	private static final String IS_LOGIN = "IsLoggedIn";
	private static final String USER_ID = "userID";

public static final String COLUMN_PAGE= "pagenumber";
public static final String COLUMN_TYPE = "type";

public static final String COLUMN_TITLE= "title";
public static final String COLUMN_MESSAGE= "message";
	public static final String COLUMN_DATE= "datte";
public static final String COLUMN_CHECKID = "checkid";
private static Integer delete=0;
public DBHelper(Context context)
{
super(context, DATABASE_NAME, null, 1);
}
@Override
public void onCreate(SQLiteDatabase db) {
// TODO Auto-generated method stub
db.execSQL(
		"create table " + TABLE1_NAME +
				"(" + COLUMN_ID + " integer primary key , " + COLUMN_TITLE + " text," + COLUMN_MESSAGE + " text," + COLUMN_DATE + " text," +COLUMN_SEEN+" integer )"
);
	db.execSQL(
			"create table " + TABLE2_NAME +
					"(" + COLUMN_ID + " integer primary key , " + COLUMN_CHECKID + " text)"
	);
	db.execSQL(
			"create table " + TABLE3_NAME +
					"(" + COLUMN_ID + " integer," + USER_ID + " text," + IS_LOGIN + " integer)"
	);
	db.execSQL(
			"create table " + TABLE4_NAME +
					"(" + COLUMN_ID + " integer primary key, ssid text, key text, wifi integer, repeat integer )"
	);


	//insert empty session
	ContentValues contentValues = new ContentValues();
	contentValues.put(COLUMN_ID, 1);
	contentValues.put(USER_ID, "stk");
	contentValues.put(IS_LOGIN, 0);
	db.insert(TABLE3_NAME, null, contentValues);

	//insert empty session
	ContentValues conValues = new ContentValues();
	conValues.put(COLUMN_ID, 1);
	conValues.put("ssid", "stk");
	conValues.put("key", "thestkthestk");
	conValues.put("wifi", 1);
	conValues.put("repeat", 1);
	db.insert(TABLE4_NAME, null, conValues);
 }
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub
db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME + "");
db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME + "");
	db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME + "");
	db.execSQL("DROP TABLE IF EXISTS " + TABLE4_NAME + "");
onCreate(db);
}

//insert contacts into database
public boolean InsertMessages  (String title, String msg,String date)
{
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues contentValues = new ContentValues();
	contentValues.put(COLUMN_TITLE, title);
	contentValues.put(COLUMN_MESSAGE, msg);
		contentValues.put(COLUMN_DATE, date);
		contentValues.put(COLUMN_SEEN, 0);
	db.insert(TABLE1_NAME, null, contentValues);
return true;
}
public boolean InsertCheckId (String ids)
{
	String id=ids.trim();
   SQLiteDatabase db = this.getWritableDatabase();
   ContentValues contentValues = new ContentValues();
contentValues.put(COLUMN_CHECKID, id);

db.insert(TABLE2_NAME, null, contentValues);
return true;
}



//update seen status
public boolean UpdateSeenStatus ( Integer id,Integer seen)
{
SQLiteDatabase db = this.getWritableDatabase();
ContentValues contentValues = new ContentValues();
contentValues.put(COLUMN_SEEN, seen);
db.update(TABLE1_NAME, contentValues, "id = ? ", new String[] {
Integer.toString(id) } );
return true;
}



public Integer getDeleteId(){
	return delete;
}

//delete an existing contact

public Integer deleteJob (Integer id)
{
	delete =id;
SQLiteDatabase db = this.getWritableDatabase();
Integer num= db.delete(TABLE2_NAME,
"id = ? ",
new String[] { Integer.toString(id) });
return num;
}

//get all contacts from database and return them as an arraylist

public ArrayList<Option> getAllMessages()
{
ArrayList<Option> arraylist = new ArrayList<Option>();
SQLiteDatabase db = this.getReadableDatabase();
Cursor cursor=  db.rawQuery( "SELECT * FROM "+TABLE1_NAME+" ORDER BY "+COLUMN_ID+" DESC ", new String[] {} );
cursor.moveToFirst();
while(cursor.isAfterLast() == false)
{
	    arraylist.add(new Option(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
		cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)),
		cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),cursor.getInt(cursor.getColumnIndex(COLUMN_SEEN))));

cursor.moveToNext();
}


return arraylist;
}
	public ArrayList<String> getCheckIds()
	{
		ArrayList<String> arraylist = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery( "select * from "+TABLE2_NAME+"", null );
		if(!(cursor==null)) {
			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {

				arraylist.add(cursor.getString(cursor.getColumnIndex(COLUMN_CHECKID)));
				cursor.moveToNext();
			}
		}
		return arraylist;
	}



public Integer NumUnseen(){
	int c=0;
	
	SQLiteDatabase db = this.getWritableDatabase();
	String count = "SELECT count(*) FROM "+TABLE2_NAME+" WHERE "+COLUMN_SEEN+" = "+0;
	Cursor  mcursor = db.rawQuery(count, null);
	mcursor.moveToFirst();
	
	int icount = mcursor.getInt(0)+c;
    return icount;
	//populate table
}

	public Boolean MessageDBEmpty(){
		SQLiteDatabase db = this.getWritableDatabase();
		String count = "SELECT count(*) FROM "+TABLE1_NAME;
		Cursor mcursor = db.rawQuery(count, null);
		mcursor.moveToFirst();
		int icount = mcursor.getInt(0);
		if(icount>0)
			return false;
		else
			return true;
		//populate table
	}



	//session stuff
	public boolean createSession(String userID){

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(USER_ID, userID);
		contentValues.put(IS_LOGIN, 1);
		db.update(TABLE3_NAME, contentValues, "id = ? ", new String[] {
				Integer.toString(1) } );
		return true;

	}
	//session stuff
	public boolean SaveSettings(String ssid,String key,Integer wifi,Integer minutes){

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("ssid", ssid);
		contentValues.put("key", key);
		contentValues.put("wifi", wifi);
		contentValues.put("repeat",minutes);
		db.update(TABLE4_NAME, contentValues, "id = ? ", new String[] {
				Integer.toString(1) } );
		return true;

	}

	public String getKey(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE4_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex("key"));
	}
	public String getSSID(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE4_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex("ssid"));
	}
	public Integer getWifi(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE4_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("wifi"));
	}

	public Integer getRepeatTime(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE4_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex("repeat"));
	}

	public boolean isLoggedIn(){

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE3_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		if(cursor.getInt(cursor.getColumnIndex(IS_LOGIN))==1){
			return  true;
		}
		return false;
	}

	public  boolean logoutUser(){

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(USER_ID, "stk");
		contentValues.put(IS_LOGIN, 0);
		db.update(TABLE3_NAME, contentValues, "id = ? ", new String[] {
				Integer.toString(1) } );
		return true;
	}
	public  String getUserId(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=  db.rawQuery("SELECT * FROM " + TABLE3_NAME+" WHERE "+COLUMN_ID+" = "+1, new String[] {} );
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(USER_ID));
	}





}

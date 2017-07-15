package com.myapp.unknown.iNTCON.SQLiteDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by UNKNOWN on 2/9/2017.
 */
public class SqliteDataBaseUploadedOrError extends SQLiteOpenHelper {

    private static final String DATABASE_NAME   = "uploaded.db";
    private static final String TABLE_NAME      = "uploaded_list";
    private static final String TITLE           = "TITLE";
    private static final String FILE_PATH       = "FILE_PATH";
    private static final String PROGRESS        = "PROGRESS";
    private static final String SIZE            = "SIZE";
    private static final String DATE            = "DATE";
    private static final String TIME            = "TIME";
    private static final String KEY             = "KEY";
    private static final String PUSH_KEY        = "PUSH_KEY";
    private static final String IS_ERROR        = "IS_ERROR";
    private static final String IS_COMPLETED    = "IS_COMPLETED";
    private static final String REF_PATH        = "REF_PATH";

    public SqliteDataBaseUploadedOrError(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT"
                + ",TITLE TEXT, FILE_PATH TEXT, "
                + "PROGRESS INTEGER, SIZE LONG, "
                + "DATE TEXT, TIME TEXT, "
                + "IS_ERROR INTEGER, IS_COMPLETED INTEGER, KEY TEXT, PUSH_KEY TEXT, REF_PATH TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData( String title,String file_path,
                               int progress,Long size,
                               String date,String time,
                               int error, int completed,
                               String key,
                               String push_key,
                               String refPath
    )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(FILE_PATH,file_path);
        contentValues.put(PROGRESS,progress);
        contentValues.put(SIZE,size);
        contentValues.put(DATE,date);
        contentValues.put(TIME,time);
        contentValues.put(IS_ERROR,error);
        contentValues.put(IS_COMPLETED,completed);
        contentValues.put(KEY,key);
        contentValues.put(PUSH_KEY,push_key);
        contentValues.put(REF_PATH,refPath);
        long result = db.insert(TABLE_NAME,null,contentValues);
        return result != -1;
    }


    public Cursor getData(){
        SQLiteDatabase db = getWritableDatabase();

        try{
            return   db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID ASC LIMIT 1",null);
        } catch (SQLiteException e)
        {
            return null;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = getWritableDatabase();

        try{
            return   db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
        } catch (SQLiteException e)
        {
            return null;
        }
    }

    public boolean deleteData(String file_path){
        SQLiteDatabase db = getWritableDatabase();
        int deleted =  db.delete(TABLE_NAME,"FILE_PATH = ?",new String[] {file_path});
        return deleted > 0;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}

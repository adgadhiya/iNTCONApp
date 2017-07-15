package com.myapp.unknown.iNTCON.SQLiteDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by UNKNOWN on 2/8/2017.
 */
public class SqliteDataBaseDownloading extends SQLiteOpenHelper {

    private static final String DATABASE_NAME   = "downloading.db";
    private static final String TABLE_NAME      = "downloading_list";
    private static final String TITLE           = "TITLE";
    private static final String DOWNLOAD_PATH   = "DOWNLOAD_PATH";
    private static final String PROGRESS        = "PROGRESS";
    private static final String SIZE            = "SIZE";
    private static final String DATE            = "DATE";
    private static final String TIME            = "TIME";
    private static final String KEY             = "KEY";
    private static final String IS_ERROR        = "IS_ERROR";
    private static final String IS_COMPLETED    = "IS_COMPLETED";
    private static final String GROUP_KEY       = "GROUP_KEY";

    public SqliteDataBaseDownloading(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TITLE TEXT, DOWNLOAD_PATH TEXT, " +
                "PROGRESS INTEGER, SIZE LONG, " +
                "DATE TEXT, TIME TEXT, " +
                "IS_ERROR INTEGER, IS_COMPLETED INTEGER, KEY TEXT, GROUP_KEY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String key, String time,
                              String date, Long size,
                              String download_path, String title,
                              String group_key)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(DOWNLOAD_PATH,download_path);
        contentValues.put(PROGRESS, 0);
        contentValues.put(SIZE,size);
        contentValues.put(DATE,date);
        contentValues.put(TIME,time);
        contentValues.put(IS_ERROR, 0);
        contentValues.put(IS_COMPLETED, 0);
        contentValues.put(KEY,key);
        contentValues.put(GROUP_KEY,group_key);
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

    public boolean updateDownloadProgress(String download_path, int progress){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS,progress);
        int affected =  db.update(TABLE_NAME,contentValues,"DOWNLOAD_PATH = ?",new String[] {download_path});
        return affected > 0;
    }

    public boolean deleteData(String download_path){
        SQLiteDatabase db = getWritableDatabase();
        int deleted =  db.delete(TABLE_NAME,"DOWNLOAD_PATH = ?",new String[] {download_path});
        return deleted > 0;
    }


    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);

    }

}

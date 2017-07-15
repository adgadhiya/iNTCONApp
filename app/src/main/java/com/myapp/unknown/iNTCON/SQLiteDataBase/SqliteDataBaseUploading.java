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
public class SqliteDataBaseUploading extends SQLiteOpenHelper {

    private static final String DATABASE_NAME  = "uploading.db";
    private static final String TABLE_NAME      = "uploading_list";
    private static final String TITLE           = "TITLE";
    private static final String FILE_PATH       = "FILE_PATH";
    private static final String PROGRESS        = "PROGRESS";
    private static final String SIZE            = "SIZE";
    private static final String KEY             = "KEY";
    private static final String IS_ERROR        = "IS_ERROR";
    private static final String IS_COMPLETED    = "IS_COMPLETED";
    private static final String REF_PATH        = "REF_PATH";
    private static final String DATE            = "DATE";
    private static final String TIME            = "TIME";

    public SqliteDataBaseUploading(Context context)
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
                + "IS_ERROR INTEGER, IS_COMPLETED INTEGER, KEY TEXT, REF_PATH TEXT, DATE TEXT, TIME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String title, String file_path,
                              Long size,
                              String key, String refPath,
                              String date, String time
    )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(FILE_PATH,file_path);
        contentValues.put(PROGRESS, 0);
        contentValues.put(SIZE,size);
        contentValues.put(IS_ERROR, 0);
        contentValues.put(IS_COMPLETED, 0);
        contentValues.put(KEY,key);
        contentValues.put(REF_PATH,refPath);
        contentValues.put(DATE,date);
        contentValues.put(TIME,time);
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

    public Cursor getAllData()
    {
        SQLiteDatabase db = getWritableDatabase();

        try{
            return   db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
        } catch (SQLiteException e)
        {
            return null;
        }
    }

    public boolean updateDownloadProgress(String FILE_PATH, int progress){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS,progress);
        int affected =  db.update(TABLE_NAME,contentValues,"FILE_PATH = ?",new String[] {FILE_PATH});
        return affected > 0;
    }

    public boolean deleteData(String FILE_PATH)
    {
        SQLiteDatabase db = getWritableDatabase();
        int deleted =  db.delete(TABLE_NAME,"FILE_PATH = ?",new String[] {FILE_PATH});
        return deleted > 0;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }

}

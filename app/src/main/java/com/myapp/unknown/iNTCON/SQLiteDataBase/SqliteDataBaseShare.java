package com.myapp.unknown.iNTCON.SQLiteDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by UNKNOWN on 2/11/2017.
 */
public class SqliteDataBaseShare extends SQLiteOpenHelper {

    private static final String DATABASE_NAME   = "share.db";
    private static final String TABLE_NAME      = "share_list";
    private static final String TITLE           = "TITLE";
    private static final String DOWNLOAD_PATH   = "DOWNLOAD_PATH";
    private static final String SIZE            = "SIZE";

    public SqliteDataBaseShare(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT"
                + ",TITLE TEXT, DOWNLOAD_PATH TEXT, "
                + "SIZE LONG )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData( String title,String download_path,
                               Long size
    )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(DOWNLOAD_PATH,download_path);
        contentValues.put(SIZE,size);
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


    public boolean deleteData(String DOWNLOAD_PATH)
    {
        SQLiteDatabase db = getWritableDatabase();
        int deleted =  db.delete(TABLE_NAME,"DOWNLOAD_PATH = ?",new String[] {DOWNLOAD_PATH});
        return deleted > 0;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }


}
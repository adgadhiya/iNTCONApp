package com.myapp.unknown.iNTCON.SQLiteDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by UNKNOWN on 11/13/2016.
 */
public class SQLiteDataBaseCheckChange extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "checked_change.db";
    private static final String TABLE_NAME = "imp_or_not";

    public SQLiteDataBaseCheckChange(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,KEY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String key){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("KEY",key);
        long result = db.insert(TABLE_NAME,null,contentValues);
        return result != -1;
    }

    public Cursor getData(String key){
        SQLiteDatabase db = getWritableDatabase();

        try{
            return   db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE KEY ='" + key + "'",null);
        } catch (SQLiteException e)
        {
            return null;
        }
    }

    public boolean updateData(String key){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int affected =  db.update(TABLE_NAME,contentValues,"KEY = ?",new String[] {key});

        return affected > 0;
    }

    public boolean deleteData(String key){
        SQLiteDatabase db = getWritableDatabase();
        int deleted =  db.delete(TABLE_NAME,"KEY = ?",new String[] {key});
        return deleted > 0;
    }

}

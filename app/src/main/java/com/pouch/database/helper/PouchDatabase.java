package com.pouch.database.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pouch.common.SignUpActivity;

/**
 * Created by USER on 2016-07-20.
 */
public class PouchDatabase {
    public static String TAG="PouchDatabase";
    public static String DATABASE_NAME = "user.db";
    public static String TABLE_USER_INFO = "USER_INFO";
    public static String TABLE_PRODUCT_INFO = "PRODUCT_INFO";
    public static String TABLE_DETAIL_OF_PRODUCT_INFO = "DETAIL_OF_PRODUCT_INFO";
    public static int DATABASE_VERSION = 1;


    private static PouchDatabase database;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private Context context;
    private Activity activity;
    boolean isTest = true;

    private PouchDatabase(Context context) {
        this.context = context;
        activity = null;
    }
    private PouchDatabase(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public static PouchDatabase getInstance(Context context) {
        if (database == null) {
            database = new PouchDatabase(context);
        }
        return database;
    }

    public boolean open(){
        if(isTest)
            Log.v(TAG,"OPEN() "+DATABASE_NAME);
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null){
            return true;
        }
        else
            return false;
    }

    public void close() {
          db.close();
        database = null;
    }

    public Cursor rawQuery(String SQL){

        Cursor cur1= null;
        try {
            cur1 = db.rawQuery(SQL,null);
        }catch(Exception e){
            Log.e(TAG,"Exception in execute query");
            e.printStackTrace();
        }

        return cur1;
    }

    public boolean execSQL(String SQL) {


        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);

        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.v(TAG,context.toString());
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            if (isTest) {
                Log.v(TAG, "create table [" + TABLE_USER_INFO + "]");
            }

                String DROP_SQL = "drop table if exists " + TABLE_USER_INFO;
                try {
                    db.execSQL(DROP_SQL);
                } catch (Exception e) {
                    Log.e(TAG, "Exception DROPTABLE");
                }


            String create_USER_SQL = "create table "+ TABLE_USER_INFO + "("
                    + " ID INTEGER NOT NULL PRIMARY KEY,"
                    + " NICKNAME TEXT,"
                    + " PROFILE_URL TEXT)";
            try{
                db.execSQL(create_USER_SQL);
            }catch(Exception e){
                Log.e(TAG,"Exception in CREATE_SQL");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

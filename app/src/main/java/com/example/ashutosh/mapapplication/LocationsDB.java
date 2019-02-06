package com.example.ashutosh.mapapplication;

/**
 * Created by Ashutosh on 26-08-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashutosh on 15-08-2016.
 */
public class LocationsDB extends SQLiteOpenHelper {

    private static String DBNAME="LocationDatabase1";
    private static int VERSION=1;

    public static final String FIELD_pid="pid";
    public static final String  FIELD_lat="lat";
    public static final String FIELD_lon="lon";
    public static final String FIELD_gid="gid";
    public static final String FIELD_name="name";
    public static final String FIELD_zoom="zoom";


    private static final String DATABASE_TABLE="locations";

    private SQLiteDatabase mdb;

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql="  CREATE TABLE  " +  DATABASE_TABLE + "(" + FIELD_pid + " INTEGER PRIMARY KEY   NOT NULL," + FIELD_lat + " DOUBLE NOT NULL ," + FIELD_lon + " DOUBLE NOT NULL ," + FIELD_gid + " INTEGER ," + FIELD_name + " TEXT ," + FIELD_zoom + " DOUBLE " + ");";
        db.execSQL(sql);
    }

    public long insert(ContentValues contentValues){
        long rowID = mdb.insert(DATABASE_TABLE, null, contentValues);
        return rowID;
    }
    public int del(){
        int cnt = mdb.delete(DATABASE_TABLE, null, null);
        return cnt;
    }

    public Cursor getAllLocations(){
        return mdb.query(DATABASE_TABLE, new String[] { FIELD_pid,  FIELD_lat , FIELD_lon, FIELD_gid, FIELD_zoom } , null, null, null, null, null);
    }

    public LocationsDB(Context context)
    {
        super(context,DBNAME,null,VERSION);
        this.mdb=getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);


        onCreate(db);


    }


    public void addposition()
    {



    }
}



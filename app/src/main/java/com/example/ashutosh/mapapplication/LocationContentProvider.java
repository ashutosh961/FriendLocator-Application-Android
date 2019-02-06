package com.example.ashutosh.mapapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.sql.SQLException;

/**
 * Created by Ashutosh on 15-08-2016.
 */
public  class LocationContentProvider extends ContentProvider {

    public static final int LOCATIONS=1;
    public static final String PROVIDER_NAME = "com.example.ashutosh.mapapplication.providerLocationDatabase1";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations" );

    private static UriMatcher uriMatcher;

    static{
        uriMatcher=new UriMatcher(uriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"locations",LOCATIONS);
    }

    LocationsDB mLocationsDB;

    @Override
    public boolean onCreate() {

        mLocationsDB=new LocationsDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(uriMatcher.match(uri)==LOCATIONS){
            return mLocationsDB.getAllLocations();
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mLocationsDB.insert(values);
        Uri _uri=null;
        if(rowID>0){
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        }else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = 0;
        cnt = mLocationsDB.del();
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

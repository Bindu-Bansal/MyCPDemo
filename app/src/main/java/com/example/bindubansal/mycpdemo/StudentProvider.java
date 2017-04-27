package com.example.bindubansal.mycpdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by BINDU BANSAL on 24/04/2017.
 */

public class StudentProvider extends ContentProvider {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tabName = uri.getLastPathSegment();
        int i = sqLiteDatabase.delete(tabName,selection,null);
        return i;

        //throw new UnsupportedOperationException("Not Yet Implemented");
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.

        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String tabName = uri.getLastPathSegment();
        long l = sqLiteDatabase.insert(tabName,null,values);
        Uri dummy = Uri.parse("kuchbhi/" +l);

        return dummy;
    }

    @Override
    public boolean onCreate() {

        dbHelper = new DBHelper(getContext(),Util.DB_NAME,null,Util.DB_VERSION);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String tabNAme = uri.getLastPathSegment();
        Cursor cursor = sqLiteDatabase.query(tabNAme,projection,null,null,null,null,null);

        return cursor;

       // throw  new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

       String tabName = uri.getLastPathSegment();
        int i = sqLiteDatabase.update(tabName,values,selection,null);

        return i;
       //throw  new UnsupportedOperationException("not yet implemented");
    }

    class DBHelper extends SQLiteOpenHelper{

        public  DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){

            super(context, name, factory, version); //Create the database

        }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Util.CREATE_TAB_QUERY); //create the table

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
}
package com.lucagiorgetti.collectionhelper.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by Utente on 17/04/2017.
 */

public class Double implements BaseColumns, Serializable {

    public static final String TABLE_NAME = "double";
    public static final String COLUMN_USER_ID= "user_id";
    public static final String COLUMN_SURPRISE_ID = "surprise_id";

    private int id;
    private int user_id;
    private int surprise_id;

    public Double(int user_id, int surprise_id) {
        this.user_id = user_id;
        this.surprise_id = surprise_id;
    }

    public Double(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(Double._ID));
        this.user_id = cursor.getInt(cursor.getColumnIndex(Double.COLUMN_USER_ID));
        this.surprise_id = cursor.getInt(cursor.getColumnIndex(Double.COLUMN_SURPRISE_ID));
    }

    public int getId(){ return id;}
    public int getUserId(){ return user_id;}
    public int getSurpriseId() {return surprise_id;}


    @Override
    public String toString(){
        return String.valueOf(user_id) + " - " + String.valueOf(surprise_id);
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID, user_id);
        cv.put(COLUMN_SURPRISE_ID, surprise_id);

        return cv;
    }
}
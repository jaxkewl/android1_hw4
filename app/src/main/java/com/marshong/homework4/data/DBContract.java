package com.marshong.homework4.data;

import android.provider.BaseColumns;

/**
 * Created by martin on 3/4/2015.
 */
public class DBContract implements BaseColumns {

    public static final String DB_NAME = "hw4DB";
    public final static int DB_VERSION = 1;

    public static final String TABLE_NAME = "taskTable";
    public static final String COL_NAME_1 = "taskName";
    public static final String COL_NAME_2 = "taskDescr";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COL_NAME_1 + " TEXT, " + COL_NAME_2 + " TEXT) ";

    // Define projection for Version table
    public static final String[] PROJECTION = new String[]{
                /*0*/ "_id",
                /*1*/ COL_NAME_1,
                /*2*/ COL_NAME_2
    };

}

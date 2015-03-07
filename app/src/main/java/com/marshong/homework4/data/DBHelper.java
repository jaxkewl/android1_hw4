package com.marshong.homework4.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.marshong.homework4.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 3/4/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = DBHelper.class.getSimpleName();


    public DBHelper(Context context) {

        super(context, DBContract.DB_NAME, null, DBContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "creating DB: " + DBContract.CREATE_TABLE);
        db.execSQL(DBContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertTask(String taskName, String taskDescr) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.COL_NAME_1, taskName);
        contentValues.put(DBContract.COL_NAME_2, taskDescr);

        // Insert data to table
        db.insert(DBContract.TABLE_NAME, // table name
                null,
                contentValues);

        // Remember to close the db
        db.close();

    }

    public void deleteTask(String keyID) {
        Log.d(TAG, "deleting task: " + keyID);
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = DBContract._ID + "=" + keyID;
        db.delete(DBContract.TABLE_NAME, whereClause, null);
    }

    public Task getTask(String keyID) {
        Log.d(TAG, "getting single Task: " + keyID);

        SQLiteDatabase db = getReadableDatabase();

        String[] tableColumns = new String[]{DBContract._ID, DBContract.COL_NAME_1, DBContract.COL_NAME_2};
        String whereClause = DBContract._ID + "=?";
        String[] whereArgs = new String[]{keyID};


        Cursor cursor = db.query(DBContract.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();
        Task task = null;
        //while (!cursor.isAfterLast()) {
        task = createTaskFromCursor(cursor);
        //}

        cursor.close(); // close the cursor
        db.close();     // close the db

        Log.d(TAG, "Found task: " + task);

        return task;
    }

    public List<Task> getTasks() {
        Log.d(TAG, "gettingTasks...");


        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new ArrayList<Task>();

        Cursor cursor = db.query(DBContract.TABLE_NAME,
                DBContract.PROJECTION,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Task task = createTaskFromCursor(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }

        cursor.close(); // close the cursor
        db.close();     // close the db

        return tasks;
    }

    private Task createTaskFromCursor(Cursor c) {
        int id = c.getInt(0);

        int taskNameColIdx = c.getColumnIndexOrThrow(DBContract.COL_NAME_1);
        int taskDescrColIdx = c.getColumnIndexOrThrow(DBContract.COL_NAME_2);
        String taskName = c.getString(taskNameColIdx);
        String taskDescr = c.getString(taskDescrColIdx);

        Task task = new Task(taskName, taskDescr);
        task.setId(id);
        return task;
    }

}

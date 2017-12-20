package com.thincovate.taskmanager.smartqs.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class TaskDBDAO {

    protected SQLiteDatabase database;
    private DBHelper dbHelper;
    private final Context mContext;

    public static final String TASK_TABLE = "tasks";

    public TaskDBDAO(Context context) {
        this.mContext = context;
        dbHelper = DBHelper.getHelper(mContext);
        open();

    }

    private void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DBHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database = null;
    }

    public void reset () throws SQLException {
        database.execSQL ("delete from "+TASK_TABLE);
    }

}

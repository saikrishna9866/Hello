package com.thincovate.taskmanager.smartqs.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {


    private static final String TAG = DBHelper.class.getSimpleName();
    //public static final String DATABASE_NAME = "tasksdb.db";
    public static final String DATABASE_NAME = "tasksdb";
    private static final int DATABASE_VERSION = 1;

    public static final String TASK_TABLE = "tasks";


    //Tasks
    public static final String TASK_ID = "task_id";
    public static final String TASK_FOR_DATE = "task_for_date";
    public static final String TASK_FOR_TIME = "task_for_time";
    public static final String TASK_DESC = "task_desc";
    public static final String TASK_CATEGORIES = "task_categories";
    public static final String TASK_PRIORITY = "task_priority";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_DATE_COMPLETED = "task_date_completed";
    public static final String TASK_DATE_CREATED = "task_date_created";
    public static final String TASK_DATE_MODIFIED = "task_date_modified";




    public static final String CREATE_TASKS_TABLE = "CREATE TABLE "
            + TASK_TABLE + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK_FOR_DATE + " DATETIME, " + TASK_FOR_TIME + " TEXT, " + TASK_DESC + " TEXT, "
            + TASK_CATEGORIES + " TEXT, " + TASK_PRIORITY + " INT, "
            + TASK_STATUS + " INT, " + TASK_DATE_COMPLETED + " TEXT, "
            + TASK_DATE_CREATED + " TEXT, " + TASK_DATE_MODIFIED + " TEXT"
            + ")";



    private static DBHelper instance;

    public static synchronized DBHelper getHelper(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASKS_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TASKS_TABLE);

        onCreate(db);
    }


}
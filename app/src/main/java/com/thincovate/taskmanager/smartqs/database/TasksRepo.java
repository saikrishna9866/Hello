package com.thincovate.taskmanager.smartqs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.thincovate.taskmanager.smartqs.beans.TasksBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TasksRepo extends TaskDBDAO {

    private static final String TAG = TasksRepo.class.getSimpleName();

    public TasksRepo(Context context) {
        super(context);
    }

    //Insert Tasks
    public long save(TasksBean tasksBean) {
        ContentValues values = new ContentValues();
        //values.put(DBHelper.TASK_ID, tasksBean.getTaskId());
        values.put(DBHelper.TASK_FOR_DATE, tasksBean.getTasksForDate());
        values.put(DBHelper.TASK_FOR_TIME, tasksBean.getTasksForTime());
        values.put(DBHelper.TASK_DESC, tasksBean.getTasks());
        values.put(DBHelper.TASK_CATEGORIES, tasksBean.getTasksCategory());
        values.put(DBHelper.TASK_PRIORITY, tasksBean.getTasksPriority());
        values.put(DBHelper.TASK_STATUS, tasksBean.getTasksStatus());
        values.put(DBHelper.TASK_DATE_COMPLETED, tasksBean.getTasksDateCompleted());
        values.put(DBHelper.TASK_DATE_CREATED, tasksBean.getTasksDateCreated());
        values.put(DBHelper.TASK_DATE_MODIFIED, tasksBean.getTasksDateModified());

        Log.e("valuess", String.valueOf(values));
        return database.insertWithOnConflict(DBHelper.TASK_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    //get list of tasks
    public ArrayList<TasksBean> getAllTasks() {
        ArrayList<TasksBean> listOfTaskItems = new ArrayList<>();
        String query = "SELECT " + DBHelper.TASK_FOR_DATE + "," + DBHelper.TASK_FOR_TIME
                + "," + DBHelper.TASK_DESC + "," + DBHelper.TASK_CATEGORIES + "," + DBHelper.TASK_PRIORITY
                + "," + DBHelper.TASK_STATUS + "," + DBHelper.TASK_DATE_COMPLETED
                + "," + DBHelper.TASK_DATE_CREATED + "," + DBHelper.TASK_DATE_MODIFIED
                + " FROM "
                + DBHelper.TASK_TABLE;
        Log.e("query", query);
        try {
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TasksBean taskItem = new TasksBean();
                taskItem.setTasksForDate(cursor.getString(0));
                taskItem.setTasksForTime(cursor.getString(1));
                taskItem.setTasks(cursor.getString(2));
                taskItem.setTasksCategory(cursor.getString(3));
                taskItem.setTasksPriority(cursor.getInt(4));
                taskItem.setTasksStatus(cursor.getInt(5));
                taskItem.setTasksDateCompleted(cursor.getString(6));
                taskItem.setTaskDateCreated(cursor.getString(7));
                taskItem.setTasksDateModified(cursor.getString(8));
                listOfTaskItems.add(taskItem);
            }
            cursor.close();
        } catch (SQLiteException se) {
            se.printStackTrace();
        }
        return listOfTaskItems;
    }

    public ArrayList<TasksBean> getPreviousDayTasksBasedOnStatus() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //19-11-2016
        final String currentDate = dateFormat.format(date);


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        final String currentDate2 = sdfDate.format(cal.getTime());

        Log.e(TAG, "getPreviousDayTasksBasedOnStatus: currentDate2 " + currentDate2);
        ArrayList<TasksBean> listOfTaskItems = new ArrayList<>();
        //String query = "update tasks set task_for_date = strftime('%d-%m-%Y', Date('now')),task_date_modified=strftime('%d-%m-%Y', Date('now'))  WHERE task_status = 1 AND task_for_date < strftime('%d-%m-%Y', Date('now'))";
        //String query = "update tasks set task_for_date = strftime('%d-%m-%Y', " + currentDate2 + "),task_date_modified=strftime('%d-%m-%Y', " + currentDate2 + ")  WHERE task_status = 1 AND task_for_date < strftime('%d-%m-%Y', " + currentDate2 + ")";
        String query2 = "SELECT " + DBHelper.TASK_FOR_DATE + "," + DBHelper.TASK_FOR_TIME
                + "," + DBHelper.TASK_DESC + "," + DBHelper.TASK_CATEGORIES + "," + DBHelper.TASK_PRIORITY
                + "," + DBHelper.TASK_STATUS + "," + DBHelper.TASK_DATE_COMPLETED
                + "," + DBHelper.TASK_DATE_CREATED + "," + DBHelper.TASK_DATE_MODIFIED + "," + DBHelper.TASK_ID
                + " FROM "
                + DBHelper.TASK_TABLE + " " + "WHERE task_status = 1 AND  task_for_date <= 'GETDATE() localtime'";
       // "WHERE task_status = 1 AND  task_for_date <= 'GETDATE() dd/mm/yyyy localtime'";
       // "WHERE task_status = 1 AND  task_for_date <= 'GETDATE() localtime'";
      // "WHERE task_status = 1 AND  task_for_date <= 'currentDate2 localtime'";
        // task_for_date <= strftime('%d-%m-%Y', Date('now','localtime'))";

        //"WHERE task_status = 1 AND task_for_date < strftime('%d-%m-%Y', Date('now','localtime'))";
        //task_for_date >= '2017-10-27 15:00:00'";
       // "WHERE task_status = 1 AND  task_for_date <= 'task_date_created localtime'";

        // Log.e("query", query);
        Log.e("query 2", query2);
        try {
            // database.execSQL(query);
            Cursor cursor = database.rawQuery(query2, null);
            while (cursor.moveToNext()) {
                Log.e(TAG, "getPreviousDayTasksBasedOnStatus: cursour " );
                TasksBean taskItem = new TasksBean();
                taskItem.setTasksForDate(currentDate2);
                taskItem.setTasksForTime(cursor.getString(1));
                taskItem.setTasks(cursor.getString(2));
                taskItem.setTasksCategory(cursor.getString(3));
                taskItem.setTasksPriority(cursor.getInt(4));
                taskItem.setTasksStatus(cursor.getInt(5));
                taskItem.setTasksDateCompleted(cursor.getString(6));
                taskItem.setTaskDateCreated(cursor.getString(7));

                Log.e(TAG, "getPreviousDayTasksBasedOnStatus: " + taskItem.getTasksForDate());
                taskItem.setTasksDateModified(currentDate2);
                taskItem.setTaskId(cursor.getInt(9));
                updatePreviousDayTasks(taskItem);
                listOfTaskItems.add(taskItem);
            }
            cursor.close();

        } catch (SQLiteException se) {
            se.printStackTrace();
        }
        return listOfTaskItems;
    }


    //get list of Tasks based on Priority
    public ArrayList<TasksBean> getAllTasksBasedOnPriority(int taskPriority) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date date = new Date();
        Log.e(TAG, "today date " + dateFormat.format(new Date())); //19-11-2016
        ArrayList<TasksBean> listOfTasksPriority = new ArrayList<>();
        String query = "SELECT " + DBHelper.TASK_FOR_DATE + "," + DBHelper.TASK_FOR_TIME
                + "," + DBHelper.TASK_DESC + "," + DBHelper.TASK_CATEGORIES
                + "," + DBHelper.TASK_STATUS + "," + DBHelper.TASK_DATE_COMPLETED
                + "," + DBHelper.TASK_DATE_CREATED + "," + DBHelper.TASK_DATE_MODIFIED
                + "," + DBHelper.TASK_ID + "," + DBHelper.TASK_PRIORITY
                + " FROM "
                //+ DBHelper.TASK_TABLE + " WHERE task_priority = " + taskPriority + " AND task_for_date >= Date(" + dateFormat.format(date) + ")";
                //+ DBHelper.TASK_TABLE + " WHERE task_priority = " + taskPriority + " AND task_for_date >= Date(\'now\')";
                + DBHelper.TASK_TABLE + " WHERE task_priority = " + taskPriority + " AND task_for_date = strftime('%d-%m-%Y', Date(\'now\','localtime'))";
        //select * from tasks where task_for_date = strftime('%d-%m-%Y', Date('now'))
        Log.e("query...", query);
        try {
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TasksBean taskItem = new TasksBean();
                taskItem.setTasksForDate(cursor.getString(0));
                taskItem.setTasksForTime(cursor.getString(1));
                taskItem.setTasks(cursor.getString(2));
                taskItem.setTasksCategory(cursor.getString(3));
                taskItem.setTasksStatus(cursor.getInt(4));
                taskItem.setTasksDateCompleted(cursor.getString(5));
                taskItem.setTaskDateCreated(cursor.getString(6));
                taskItem.setTasksDateModified(cursor.getString(7));
                taskItem.setTaskId(cursor.getInt(8));
                taskItem.setTasksPriority(cursor.getInt(9));
                listOfTasksPriority.add(taskItem);
            }
            cursor.close();
        } catch (SQLiteException se) {
            se.printStackTrace();
        }
        return listOfTasksPriority;
    }

    //get list of Tasks based on For_Date
    public ArrayList<TasksBean> getAllTasksBasedOnForDate(int taskPriority, String forDate) {
        ArrayList<TasksBean> listOfTasksForDate = new ArrayList<>();
        String query = "SELECT " + DBHelper.TASK_FOR_DATE + "," + DBHelper.TASK_FOR_TIME
                + "," + DBHelper.TASK_DESC + "," + DBHelper.TASK_CATEGORIES
                + "," + DBHelper.TASK_STATUS + "," + DBHelper.TASK_DATE_COMPLETED
                + "," + DBHelper.TASK_DATE_CREATED + "," + DBHelper.TASK_DATE_MODIFIED
                + "," + DBHelper.TASK_ID + "," + DBHelper.TASK_PRIORITY
                + " FROM "
                + DBHelper.TASK_TABLE + " WHERE  task_priority =" + taskPriority + " AND task_for_date = strftime('%d-%m-%Y', '" + forDate + "')";
        //+ DBHelper.TASK_TABLE + " WHERE task_priority = " + taskPriority + " AND task_for_date = strftime('%d-%m-%Y', Date(\'now\', \'1 days\'))";
        //select * from tasks where task_for_date = strftime('%d-%m-%Y', Date('now'))
        Log.e("for Date query", query);
        try {
            Cursor cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                TasksBean taskItem = new TasksBean();
                taskItem.setTasksForDate(cursor.getString(0));
                taskItem.setTasksForTime(cursor.getString(1));
                taskItem.setTasks(cursor.getString(2));
                taskItem.setTasksCategory(cursor.getString(3));
                taskItem.setTasksStatus(cursor.getInt(4));
                taskItem.setTasksDateCompleted(cursor.getString(5));
                taskItem.setTaskDateCreated(cursor.getString(6));
                taskItem.setTasksDateModified(cursor.getString(7));
                taskItem.setTaskId(cursor.getInt(8));
                taskItem.setTasksPriority(cursor.getInt(9));
                listOfTasksForDate.add(taskItem);
            }
            cursor.close();
        } catch (SQLiteException se) {
            se.printStackTrace();
        }
        Log.e(TAG, "results : " + listOfTasksForDate.size());
        return listOfTasksForDate;
    }

    /*
     * Updating a Tasks
     */
    public int updateTasks(TasksBean tasksBean) {

        ContentValues updatedTasks = new ContentValues();
        updatedTasks.put(DBHelper.TASK_FOR_DATE, tasksBean.getTasksForDate());
        updatedTasks.put(DBHelper.TASK_FOR_TIME, tasksBean.getTasksForTime());
        updatedTasks.put(DBHelper.TASK_DESC, tasksBean.getTasks());
        updatedTasks.put(DBHelper.TASK_CATEGORIES, tasksBean.getTasksCategory());
        updatedTasks.put(DBHelper.TASK_PRIORITY, tasksBean.getTasksPriority());
        //updatedTasks.put(DBHelper.TASK_STATUS, tasksBean.getTasksStatus());
        //updatedTasks.put(DBHelper.TASK_DATE_MODIFIED, tasksBean.getTasksDateModified());

        // updating row
        Log.e(TAG, "updateddd : " + updatedTasks);
        return database.update(TASK_TABLE, updatedTasks, DBHelper.TASK_ID + " = ?",
                new String[]{String.valueOf(tasksBean.getTaskId())});
    }

    public int updatePreviousDayTasks(TasksBean tasksBean) {

        ContentValues updatedTasks = new ContentValues();
        updatedTasks.put(DBHelper.TASK_FOR_DATE, tasksBean.getTasksForDate());
        updatedTasks.put(DBHelper.TASK_FOR_TIME, tasksBean.getTasksForTime());
        updatedTasks.put(DBHelper.TASK_DESC, tasksBean.getTasks());
        updatedTasks.put(DBHelper.TASK_CATEGORIES, tasksBean.getTasksCategory());    //
        updatedTasks.put(DBHelper.TASK_PRIORITY, tasksBean.getTasksPriority());
        updatedTasks.put(DBHelper.TASK_STATUS, tasksBean.getTasksStatus());
        updatedTasks.put(DBHelper.TASK_DATE_COMPLETED, tasksBean.getTasksDateCompleted());
        updatedTasks.put(DBHelper.TASK_DATE_CREATED, tasksBean.getTasksDateCreated());
        updatedTasks.put(DBHelper.TASK_DATE_MODIFIED, tasksBean.getTasksDateModified());
        // updating row

        Log.e(TAG, "previous_updateddd : " + updatedTasks);
        return database.update(TASK_TABLE, updatedTasks, DBHelper.TASK_ID + " = ?",
                new String[]{String.valueOf(tasksBean.getTaskId())});


    }

    public int updateTaskCompleteStatus(TasksBean tasksBean) {
        ContentValues updateTaskCompleteStatus = new ContentValues();
        updateTaskCompleteStatus.put(DBHelper.TASK_DATE_COMPLETED, tasksBean.getTasksDateCompleted());
        updateTaskCompleteStatus.put(DBHelper.TASK_STATUS, 0);
        // updating row
        return database.update(TASK_TABLE, updateTaskCompleteStatus, DBHelper.TASK_ID + " = ?",
                new String[]{String.valueOf(tasksBean.getTaskId())});
    }


    //delete a task
    public int deleteTask(TasksBean tasksBean) {
        return database.delete(DBHelper.TASK_TABLE, DBHelper.TASK_ID + " = ?",
                new String[]{tasksBean.getTaskId() + ""});
    }

    public int deleteAllTasks() {
        return database.delete(DBHelper.TASK_TABLE, null, null);
    }

    public TasksBean cursorToTasks(Cursor cursor) {
        TasksBean tasksBean = new TasksBean();
        tasksBean.setTaskId(cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_ID)));
        tasksBean.setTasksForDate(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_FOR_DATE)));
        tasksBean.setTasksForTime(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_FOR_TIME)));
        tasksBean.setTasks(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_DESC)));
        tasksBean.setTasksCategory(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_CATEGORIES)));
        tasksBean.setTasksPriority(cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_PRIORITY)));
        tasksBean.setTasksStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_STATUS)));
        tasksBean.setTasksDateCompleted(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_DATE_COMPLETED)));
        tasksBean.setTaskDateCreated(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_DATE_CREATED)));
        tasksBean.setTasksDateModified(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_DATE_MODIFIED)));

        return tasksBean;
    }


}

package com.thincovate.taskmanager.smartqs.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;


public class TasksBean implements Parcelable {
    private int taskId;
    private String tasksForDate;
    private String tasksForTime;
    private String tasks;
    private String tasksCategory;
    private int tasksPriority;
    private int tasksStatus;
    private String tasksDateCompleted;
    private String tasksDateCreated;
    private String tasksDateModified;

    public TasksBean() {
    }

    public TasksBean(int taskId, String tasksForDate, String tasksForTime, String tasks, String tasksCategory, int tasksPriority, int tasksStatus, String tasksDateCompleted, String taskDateCreated, String tasksDateModified) {
        this.taskId = taskId;
        this.tasksForDate = tasksForDate;
        this.tasksForTime = tasksForTime;
        this.tasks = tasks;
        this.tasksCategory = tasksCategory;
        this.tasksPriority = tasksPriority;
        this.tasksStatus = tasksStatus;
        this.tasksDateCompleted = tasksDateCompleted;
        this.tasksDateCreated = taskDateCreated;
        this.tasksDateModified = tasksDateModified;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTasksForDate() {
        return tasksForDate;
    }

    public void setTasksForDate(String tasksForDate) {
        this.tasksForDate = tasksForDate;
    }

    public String getTasksForTime() {
        return tasksForTime;
    }

    public void setTasksForTime(String tasksForTime) {
        this.tasksForTime = tasksForTime;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks)  {
        this.tasks = tasks;
    }

    public String getTasksCategory() {
        return tasksCategory;
    }

    public void setTasksCategory(String tasksCategory) {
        this.tasksCategory = tasksCategory;
    }


    public String getTasksDateCompleted() {
        return tasksDateCompleted;
    }

    public void setTasksDateCompleted(String tasksDateCompleted) {
        this.tasksDateCompleted = tasksDateCompleted;
    }

    public String getTaskDateCreated() {
        return tasksDateCreated;
    }

    public void setTaskDateCreated(String taskDateCreated) {
        this.tasksDateCreated = taskDateCreated;
    }

    public String getTasksDateModified() {
        return tasksDateModified;
    }

    public void setTasksDateModified(String tasksDateModified) {
        this.tasksDateModified = tasksDateModified;
    }

    public int getTasksPriority() {
        return tasksPriority;
    }

    public void setTasksPriority(int tasksPriority) {
        this.tasksPriority = tasksPriority;
    }

    public int getTasksStatus() {
        return tasksStatus;
    }

    public void setTasksStatus(int tasksStatus) {
        this.tasksStatus = tasksStatus;
    }

    public String getTasksDateCreated() {
        return tasksDateCreated;
    }

    public void setTasksDateCreated(String tasksDateCreated) {
        this.tasksDateCreated = tasksDateCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(taskId);
        parcel.writeString(tasksForDate);
        parcel.writeString(tasksForTime);
        parcel.writeString(tasks);
        parcel.writeString(tasksCategory);
        parcel.writeInt(tasksPriority);
        parcel.writeInt(tasksStatus);
        parcel.writeString(tasksDateCompleted);
        parcel.writeString(tasksDateCreated);
        parcel.writeString(tasksDateModified);
    }

    private TasksBean(Parcel in) {
        taskId = in.readInt();
        tasksForDate = in.readString();
        tasksForTime = in.readString();
        tasks = in.readString();
        tasksCategory = in.readString();
        tasksPriority=in.readInt();
        tasksStatus=(in.readInt());
        tasksDateCompleted = in.readString();
        tasksDateCreated = in.readString();
        tasksDateModified = in.readString();
    }

    public static final Creator<TasksBean> CREATOR = new Creator<TasksBean>() {
        public TasksBean createFromParcel(Parcel in) {
            return new TasksBean(in);
        }

        @Override
        public TasksBean[] newArray(int size) {
            return new TasksBean[size];
        }
    };


}

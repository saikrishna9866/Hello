package com.thincovate.taskmanager.smartqs.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thincovate.taskmanager.smartqs.R;
import com.thincovate.taskmanager.smartqs.beans.TasksBean;
import com.thincovate.taskmanager.smartqs.common.DialogUtils;
import com.thincovate.taskmanager.smartqs.database.TasksRepo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    private static final String TAG = NewTaskActivity.class.getSimpleName();
    //private final String[] tasksList = {"Urgent, Important", "Urgent, Not Important", "Not Urgent, Important", "Not Urgent, Not Important"};
    private int selectedRequest;
    private TasksBean incomingTasksBean;
    private EditText edTaskDesc;
    private EditText editTxtForDate, editTxtForTime;
    private Context context;
    private TasksRepo tasksRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Avoid NPE we use  getDelegate() method or if(getSupportActionBar()!=null )
        // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        //assert getSupportActionBar() != null;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_new_task);
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        context = this;
        tasksRepo = new TasksRepo(getApplicationContext());

        //Backup tasks using import and export
        /*backupData = new BackupData(context);
        backupData.setOnBackupListener(this);*/

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //19-11-2016
        final String currentDate = dateFormat.format(date);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
          cal.add(Calendar.HOUR, 3);
        Log.e(TAG, "current time " + sdf.format(cal.getTime()));
        final String currentTime = sdf.format(cal.getTime());
        final String currentDate2 = sdfDate.format(cal.getTime());
        Log.e(TAG, "For Date string " + currentDate + " For Time string " + currentTime + " currentDate2 " + currentDate2);

        Spinner spinnerTask = (Spinner) findViewById(R.id.spinnerTasks);
        edTaskDesc = (EditText) findViewById(R.id.edTaskDesc);
        ImageView imageViewDatePicker = (ImageView) findViewById(R.id.imgViewDatePicker);
        ImageView imageViewTimePicker = (ImageView) findViewById(R.id.imgViewTimePicker);
        editTxtForDate = (EditText) findViewById(R.id.edForDate);
        editTxtForTime = (EditText) findViewById(R.id.edForTime);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnCreate = (Button) findViewById(R.id.btnCreate);

        editTxtForDate.setText(currentDate2);
        editTxtForTime.setText(currentTime);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tasksList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        ArrayList<String> tasksList = new ArrayList<>();
        tasksList.add("Q1 - Urgent, Important");
        tasksList.add("Q2 - Urgent, Not Important");
        tasksList.add("Q3 - Not Urgent, Important");
        tasksList.add("Q4 - Not Urgent, Not Important");
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(NewTaskActivity.this, tasksList);
        spinnerTask.setAdapter(customSpinnerAdapter);
        spinnerTask.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedRequest = position + 1;
                        // Showing selected spinner item
                        Log.e(TAG, " selected priority " + position + " request item " + selectedRequest);
                        //Toast.makeText(parent.getContext(), "Selected: " + selectedRequest, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );
        edTaskDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.e(TAG, "onfocus change ");
                    closeKeypad();
                }
            }
        });

        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        imageViewTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        editTxtForDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTxtForDate.setInputType(InputType.TYPE_NULL);
                showDatePicker();
            }
        });

        editTxtForTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTxtForTime.setInputType(InputType.TYPE_NULL);
                showTimePicker();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(NewTaskActivity.this, MainActivity.class);
                backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backToMain);
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {

                                             //closeKeypad();
                                             String mTaskDesc = edTaskDesc.getText().toString();
                                             String mForDate = editTxtForDate.getText().toString();
                                             String mForTime = editTxtForTime.getText().toString();


                                             boolean isTaskDescEmpty = mTaskDesc.isEmpty();
                                             boolean isForDateEmpty = mForDate.isEmpty();
                                             boolean isForTimeEmpty = mForTime.isEmpty();
                                             boolean isTaskEmpty = selectedRequest == 0;

                                             Log.e(TAG, mTaskDesc + " for date " + mForDate + " for Time " + mForTime);
                                             String fields = "";
                                             if (isTaskDescEmpty && isForDateEmpty && isForTimeEmpty && isTaskEmpty) {
                                                 userValidation(getString(R.string.plz_fill_all_fields));
                                             } else {
                                                 if (isTaskDescEmpty)
                                                     fields = fields + getString(R.string.task_desp_msg) + ", ";
                                                 if (isForDateEmpty) {
                                                     //fields = fields + getString(R.string.for_date_msg) + ", ";
                                                     mForDate = currentDate;

                                                 }
                                                 if (isForTimeEmpty) {
                                                     //fields = fields + getString(R.string.for_time_msg) + ", ";
                                                     mForTime = currentTime;
                                                 }
                                                 if (isTaskEmpty)
                                                     fields = fields + getString(R.string.task_priority_msg) + ", ";

                                                 if (!fields.isEmpty()) {
                                                     String list = fields.substring(0, fields.length() - 2);
                                                     userValidation(getString(R.string.please_fill) + " " + list);
                                                 } else {


                                                     TasksBean tasksBean = new TasksBean();
                                                     tasksBean.setTasksForDate(mForDate);
                                                     tasksBean.setTasksForTime(mForTime);
                                                     tasksBean.setTasks(mTaskDesc);
                                                     tasksBean.setTasksCategory("Office 2");
                                                     tasksBean.setTasksPriority(selectedRequest);
                                                     tasksBean.setTasksStatus(1);
                                                     // tasksBean.setTasksDateCompleted("29-11-2016");
                                                     tasksBean.setTaskDateCreated(currentDate);
                                                     //tasksBean.setTasksDateModified("22-11-2016");

                                                     if (incomingTasksBean != null) {
                                                         tasksBean.setTaskId(incomingTasksBean.getTaskId());
                                                         //tasksBean.setTasksPriority(incomingTasksBean.getTasksPriority());
                                                         tasksRepo.updateTasks(tasksBean);
                                                     } else
                                                         tasksRepo.save(tasksBean);
                                                     Log.e(TAG, "New Task task id " + tasksBean.getTaskId() + " Task For Date " + tasksBean.getTasksForDate() + " Task For Time " + tasksBean.getTasksForTime() + " Task " + tasksBean.getTasks() + " Task Category " + tasksBean.getTasksCategory() + " Task Priority " + tasksBean.getTasksPriority() + " Task Completed " + tasksBean.getTasksDateCompleted() + " Task Created " + tasksBean.getTaskDateCreated() + "Task Modified " + tasksBean.getTasksDateModified());
                                                     // Toast.makeText(getApplicationContext(), "Selected: " + selectedRequest, Toast.LENGTH_LONG).show();
                                                     Intent backToMain = new Intent(NewTaskActivity.this, MainActivity.class);
                                                     backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                     backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                     startActivity(backToMain);
                                                 }

                                             }

                                         }
                                     }

        );

        int taskId = getIntent().getIntExtra("taskId", 0);
        String taskName = getIntent().getStringExtra("taskName");
        String taskDate = getIntent().getStringExtra("taskDate");
        String taskTime = getIntent().getStringExtra("taskTime");
        int taskPriority = getIntent().getIntExtra("taskPriority", 0);
        String taskCategory = getIntent().getStringExtra("taskCategory");
        Log.e(TAG, "geting priority  " + getIntent().getIntExtra("taskPriority", 0));

        /*spinnerTask.getSelectedItem();
        for (int arrayIndex = 0; arrayIndex < tasksList.length; arrayIndex++) {
            if (tasksList[arrayIndex].equals(getIntent().getIntExtra("taskPriority", 0)))
                spinnerTask.setSelection(arrayIndex);
        }*/
        edTaskDesc.setText(taskName);
        if (taskPriority != 0) {
            spinnerTask.setSelection(taskPriority - 1);
            editTxtForDate.setText(taskDate);
            editTxtForTime.setText(taskTime);
        }
        Log.e("getTasksPriority ==> ", "" + taskPriority);

        if (taskId != 0) {
            incomingTasksBean = new TasksBean();
            incomingTasksBean.setTaskId(taskId);
            incomingTasksBean.setTasks(taskName);
            incomingTasksBean.setTasksForDate(taskDate);
            incomingTasksBean.setTasksForTime(taskTime);
            incomingTasksBean.setTasksPriority(taskPriority);
            incomingTasksBean.setTasksCategory(taskCategory);

            assert toolbar != null;
            toolbar.setTitle(R.string.edit_task);
        }


    }

    private void userValidation(String alertMeaasege) {
        DialogUtils.showDialogPositiveButton(this, "Alert Message", alertMeaasege, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showTimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        populateSetTime(hourOfDay, minute);
                        //editTxtForTime.setText(hourOfDay + ":" + minute);
                        //Log.e(TAG, "For Time " + hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void populateSetTime(int hours, int minutes) {
        String formattedHours = (String.valueOf(hours));
        String formattedMinutes = (String.valueOf(minutes));

        if (hours < 10) {
            formattedHours = "0" + hours;
        }

        if (minutes < 10) {
            formattedMinutes = "0" + minutes;
        }

        editTxtForTime.setText(formattedHours + ":" + formattedMinutes);
        Log.e(TAG, "For Time " + formattedHours + ":" + formattedMinutes);
    }

    private void showDatePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        populateSetDate(year, monthOfYear + 1, dayOfMonth);
                        /*editTxtForDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        Log.e(TAG, "For Date " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);*/
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

    }

    private void populateSetDate(int year, int month, int day) {
        String formattedDay = (String.valueOf(day));
        String formattedMonth = (String.valueOf(month));

        if (day < 10) {
            formattedDay = "0" + day;
        }

        if (month < 10) {
            formattedMonth = "0" + month;
        }

        editTxtForDate.setText(formattedDay + "-" + formattedMonth + "-" + year);
    }


    private void closeKeypad() {
        /*InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);*/

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "OnBack pressed");
        //closeKeypad();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
    }


    private class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private final ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }


        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(NewTaskActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#727272"));
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(NewTaskActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#727272"));
            return txt;
        }

    }
}

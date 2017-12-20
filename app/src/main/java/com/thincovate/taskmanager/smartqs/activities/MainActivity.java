package com.thincovate.taskmanager.smartqs.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thincovate.taskmanager.smartqs.R;
import com.thincovate.taskmanager.smartqs.beans.TasksBean;
import com.thincovate.taskmanager.smartqs.common.DialogUtils;
import com.thincovate.taskmanager.smartqs.common.NDConstants;
import com.thincovate.taskmanager.smartqs.common.OnBackupListener;
import com.thincovate.taskmanager.smartqs.database.DBHelper;
import com.thincovate.taskmanager.smartqs.database.TasksRepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener, OnBackupListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TasksRepo tasksRepo;
    private ArrayList<TasksBean> urgentImpList;
    private ArrayList<TasksBean> urgentNotImpList;
    private ArrayList<TasksBean> notUrgentImpList;
    private ArrayList<TasksBean> notUrgentNotImpList;
    private static final String KEY_TODAY_DATE_ANDTIME = "TimeToday";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private String formattedDate;
    private SimpleDateFormat dateFormat1;
    private NotUrgentNotImpTaskAdapter notUrgentNotImpAdapter;
    private UrgentImportantTaskAdapter adapter;
    private UrgentNotImportantTaskAdapter urgentNotImpAdapter;
    private NotUrgentImpTaskAdapter notUrgentImpAdapter;
    private static final String overLay_Preference = "overlay Preference";
    private static SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private RecyclerView urgentImpRecyclerView;
    private RecyclerView urgentNotImpRecyclerView;
    private RecyclerView notUrgentImpRecyclerView;
    private RecyclerView notUrgentNotImpRecyclerView;
    public CardView cardView;
    private Calendar calendar1;
    private String formattedDatee;
    private SimpleDateFormat dateFormatt;


    // url for database
    private final String dataPath = "//data//com.thincovate.taskmanager.project_mi//databases//";

    // name of main data
    private final String dataName = DBHelper.DATABASE_NAME;

    // data main
    private final String data = dataPath + dataName;

    // name of temp data
    private final String dataTempName = DBHelper.DATABASE_NAME + "_temp";

    // temp data for copy data from sd then copy data temp into main data
    private final String dataTemp = dataPath + dataTempName;

    // folder on sd to backup data
    private final String folderSD = Environment.getExternalStorageDirectory() + "/MyTasks";

    private static final int PICKFILE_RESULT_CODE = 1;
    private EditText edChooseFileName;
    private AlertDialog alert;
    private ImageView checkView;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        checkRuntimePermissions();
        setContentView(R.layout.activity_main);
        checkView = (ImageView) findViewById(R.id.check);
        sharedPreferences = getSharedPreferences(overLay_Preference, Activity.MODE_PRIVATE);
        boolean showOverlay = sharedPreferences.getBoolean("MainActivity", true);
        Log.e("OVERLAY", "Overlay..........." + showOverlay);
        if (showOverlay) {
            showInstructionsOverlay();
        }
        tasksRepo = new TasksRepo(getApplicationContext());

        //the app is being launched for first time in a Day, do something
        getAllPreviousDayTasksBasedOnStatus();


        hideSoftKeyboard();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(getApplicationContext(),
                        NewTaskActivity.class);
                newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newTask);
            }
        });
        ArrayList<TasksBean> taskList1 = tasksRepo.getAllTasks();
        Log.e(TAG, "get All tasks " + taskList1.size());
        if (taskList1.size() == 0) {
            DialogUtils.showDialogTwoActions(MainActivity.this, "Alert Message", getString(R.string.task_list_empty_dialog_msg), getString(R.string.plz_add_btn_dialog), getString(R.string.no_btn_dialog));
        }

        ImageView imageDatabase = (ImageView) findViewById(R.id.imgViewDatabase);
        imageDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                contextDailog.setContentView(R.layout.layout_import_export_dialog);
                contextDailog.setCancelable(true);
                contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));


                TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                dialogTitle.setText(R.string.title_backup_tasks);
                contextDailog.show();

                TextView tvBackUpData = (TextView) contextDailog.findViewById(R.id.action1);
                TextView tvImportData = (TextView) contextDailog.findViewById(R.id.action2);
                tvBackUpData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contextDailog.dismiss();
                        exportToSD();
                    }
                });
                tvImportData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contextDailog.dismiss();
                        importFromSD();
                    }
                });


            }
        });
        //   Urgent,Important Q1
        urgentImpRecyclerView = (RecyclerView) findViewById(R.id.urgentImpList);
        urgentImpRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        urgentImpRecyclerView.setLayoutManager(layoutManager);
        adapter = new UrgentImportantTaskAdapter();
        urgentImpRecyclerView.setAdapter(adapter);
        urgentImpList = tasksRepo.getAllTasksBasedOnPriority(1);
        adapter.notifyDataSetChanged();

        //Urgent,NotImportant Q2
        urgentNotImpRecyclerView = (RecyclerView) findViewById(R.id.urgentNotImpList);
        urgentNotImpRecyclerView.setHasFixedSize(true);
        LinearLayoutManager urgentNotImpManager = new LinearLayoutManager(getApplicationContext());
        urgentNotImpRecyclerView.setLayoutManager(urgentNotImpManager);
        urgentNotImpAdapter = new UrgentNotImportantTaskAdapter();
        urgentNotImpRecyclerView.setAdapter(urgentNotImpAdapter);
        urgentNotImpList = tasksRepo.getAllTasksBasedOnPriority(2);
        urgentNotImpAdapter.notifyDataSetChanged();
        Log.e(TAG, "Q1 size " + urgentImpList.size());
        Log.e(TAG, "Q2 size " + urgentNotImpList.size());

        //Not Urgent,Important Q3
        notUrgentImpRecyclerView = (RecyclerView) findViewById(R.id.notUrgentImpList);
        notUrgentImpRecyclerView.setHasFixedSize(true);
        LinearLayoutManager notUrgentImpManager = new LinearLayoutManager(getApplicationContext());
        notUrgentImpRecyclerView.setLayoutManager(notUrgentImpManager);
        notUrgentImpAdapter = new NotUrgentImpTaskAdapter(getApplicationContext());
        notUrgentImpRecyclerView.setAdapter(notUrgentImpAdapter);
        notUrgentImpList = tasksRepo.getAllTasksBasedOnPriority(3);
        notUrgentImpAdapter.notifyDataSetChanged();
        Log.e(TAG, "Q3 size " + notUrgentImpList.size());
        cardView = (CardView) findViewById(R.id.q3card);

        notUrgentImpRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ===========");
                startActivity(new Intent(getApplicationContext(), NewTaskActivity.class));
            }
        });
        //Not Urgent, Not Important Q4
        notUrgentNotImpRecyclerView = (RecyclerView) findViewById(R.id.notUrgentNoImpList);
        notUrgentNotImpRecyclerView.setHasFixedSize(true);
        LinearLayoutManager notUrgentNotImpManager = new LinearLayoutManager(getApplicationContext());
        notUrgentNotImpRecyclerView.setLayoutManager(notUrgentNotImpManager);
        notUrgentNotImpAdapter = new NotUrgentNotImpTaskAdapter();
        notUrgentNotImpRecyclerView.setAdapter(notUrgentNotImpAdapter);
        notUrgentNotImpList = tasksRepo.getAllTasksBasedOnPriority(4);
        notUrgentNotImpAdapter.notifyDataSetChanged();
        Log.e(TAG, "Q4 size " + notUrgentNotImpList.size());

        calendar = Calendar.getInstance();
        dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        formattedDate = dateFormat1.format(calendar.getTime());
        System.out.println("Current time => " + calendar.getTime() + " formattedDate " + dateFormat1);
        final TextView txt = (TextView) findViewById(R.id.date_text);

        SimpleDateFormat writeFormat = new SimpleDateFormat("MMM dd,yyyy");
        //for Tostdate format
        calendar1 = Calendar.getInstance();
        dateFormatt = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        formattedDatee = dateFormatt.format(calendar.getTime());

        SimpleDateFormat dateMonth = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
       String  formattedDateMonth = dateMonth.format(calendar.getTime());
        Log.e(TAG, "onCreate: dateMonth " + formattedDateMonth);
        txt.setText(formattedDateMonth);


        ImageView imgViewPreviousTasks = (ImageView) findViewById(R.id.imgViewPreviousTasks);
        imgViewPreviousTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, -1);
                formattedDate = dateFormat1.format(calendar.getTime());

                calendar1.add(Calendar.DATE, -1);
                formattedDatee = dateFormatt.format(calendar.getTime());
               //date for center textview
                SimpleDateFormat dateMonth = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
               String formattedDateMonthpre = dateMonth.format(calendar1.getTime());

                Toast.makeText(MainActivity.this, getString(R.string.toast_feature_day_tasks) + formattedDatee, Toast.LENGTH_SHORT).show();
                txt.setText(formattedDateMonthpre);
                Log.e("PREVIOUS DATE : ", formattedDate);
                urgentImpList = tasksRepo.getAllTasksBasedOnForDate(1, formattedDate);
                urgentNotImpList = tasksRepo.getAllTasksBasedOnForDate(2, formattedDate);
                notUrgentImpList = tasksRepo.getAllTasksBasedOnForDate(3, formattedDate);
                notUrgentNotImpList = tasksRepo.getAllTasksBasedOnForDate(4, formattedDate);
                adapter.notifyDataSetChanged();
                urgentNotImpAdapter.notifyDataSetChanged();
                notUrgentImpAdapter.notifyDataSetChanged();
                notUrgentNotImpAdapter.notifyDataSetChanged();

            }
        });
        ImageView imgViewFeatureTasks = (ImageView) findViewById(R.id.imgViewFeatureTasks);
        imgViewFeatureTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, 1);
                calendar1.add(Calendar.DATE, 1);
                formattedDatee = dateFormatt.format(calendar.getTime());
                formattedDate = dateFormat1.format(calendar.getTime());

                SimpleDateFormat dateMonth = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
                String formattedDateMonthnext = dateMonth.format(calendar1.getTime());

                Toast.makeText(MainActivity.this, getString(R.string.toast_feature_day_tasks) + formattedDatee, Toast.LENGTH_SHORT).show();
                txt.setText(formattedDateMonthnext);

                Log.e("NEXT DATE : ", formattedDate);
                urgentImpList = tasksRepo.getAllTasksBasedOnForDate(1, formattedDate);
                urgentNotImpList = tasksRepo.getAllTasksBasedOnForDate(2, formattedDate);
                notUrgentImpList = tasksRepo.getAllTasksBasedOnForDate(3, formattedDate);
                notUrgentNotImpList = tasksRepo.getAllTasksBasedOnForDate(4, formattedDate);
                adapter.notifyDataSetChanged();
                urgentNotImpAdapter.notifyDataSetChanged();
                notUrgentImpAdapter.notifyDataSetChanged();
                notUrgentNotImpAdapter.notifyDataSetChanged();
                Log.e(TAG, "featureTasks size " + urgentImpList.size());
            }
        });
        Log.e(TAG, "urgentImpList " + urgentImpList.size());


    }

    private void getAllPreviousDayTasksBasedOnStatus() {
        try {
            dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = new Date();

            String todayDate_String = dateFormat.format(date);
            SharedPreferences configPreferences = getSharedPreferences(NDConstants.CONFIG_NAME, 0);
            String Val = configPreferences.getString(KEY_TODAY_DATE_ANDTIME, "");
            Log.e("Stored Val", Val);
            Log.e("Today date", todayDate_String);
            if (!Val.equals(todayDate_String)) {
                try {
                    Log.e("Comments", "FirstTime Started");
                    //the app is being launched for first time in a Day, do something
                    Log.e("Comments", "First Time");
                    ArrayList<TasksBean> taskList = tasksRepo.getPreviousDayTasksBasedOnStatus();


                    Log.e(TAG, "dataa" + taskList);
                    SharedPreferences.Editor editor = configPreferences.edit();
                    // Storing name in pref
                    editor.putString(KEY_TODAY_DATE_ANDTIME, todayDate_String).apply();
                    String Value = configPreferences.getString(KEY_TODAY_DATE_ANDTIME, null);
                    Log.e(TAG, "after Insertion data Date" + Value);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(" CATEGORY COUNT ", "EXCEPTION in reading Data from Web Async task ......!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("", "Exception here Run Service.");
        }

    }


    private void showInstructionsOverlay() {
        final Dialog dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_main_activity);

        RelativeLayout layout = (RelativeLayout) dialog
                .findViewById(R.id.overlayLayout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                editor = sharedPreferences.edit();// 0 - for private mode
                editor.putBoolean("MainActivity", false).apply();

            }
        });
        dialog.show();
    }

    private boolean checkRuntimePermissions() {

        int permissionExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void successDialog() {

        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_success_dialog);
        checkView = (ImageView) dialog.findViewById(R.id.check);

        Button dialogButton = (Button) dialog.findViewById(R.id.bttn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onFinishExport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Saved to Internal storage/MyTasks. ";
        }
        Toast.makeText(MainActivity.this, notify, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinishImport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Import success";
            updateTaskList();
        }
        Toast.makeText(MainActivity.this, notify, Toast.LENGTH_SHORT).show();
    }

    private void updateTaskList() {

        ArrayList<TasksBean> taskList = tasksRepo.getPreviousDayTasksBasedOnStatus();
        Log.e(TAG, " import data size updateTaskList " + taskList.size());
        //tasksRepo.save(tasksBean);
    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "READ & WRiTE services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Read and Write storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkRuntimePermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            /*Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();*/
                            //proceed with logic by disabling the related features or quit the app.
                            DialogUtils.showSettingsDialogPositiveButton(MainActivity.this, "Network Error", "Go to settings and enable permissions", "ENABLE NOW");
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok, okListener)
                .setNegativeButton(R.string.btn_cancel, okListener)
                .create()
                .show();
    }

    private void hideSoftKeyboard() {
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
    }


    //Urgent.Important Q1  Adapter
    private class UrgentImportantTaskAdapter extends RecyclerView.Adapter<UrgentImportantTaskAdapter.ViewHolder> {
        private final String TAG = UrgentImportantTaskAdapter.class.getSimpleName();
        private CheckBox checkBox;


        public UrgentImportantTaskAdapter() {
            Log.e(TAG, "UrgentImportantTaskAdapter  ");
        }


        //    private final View.OnClickListener mOnClickListener = new MyOnClickListener();
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            //inflate your layout and pass it to view holder
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.task_items, viewGroup, false);
            Log.e(TAG, "oncreateviewholder   ");

            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final UrgentImportantTaskAdapter.ViewHolder viewHolder, final int position) {
            final TasksBean tasks = urgentImpList.get(position);
            //setting data to view holder elements
            Log.e(TAG, "UI size " + urgentImpList.size());
            dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = new Date();
            Log.e(TAG, "today date " + dateFormat.format(date)); //19-11-2016
            //if (tasks.getTasksForDate().equalsIgnoreCase(dateFormat.format(date))) {
            Log.e(TAG, "today tasks " + tasks.getTasksForDate());

            //Log.e(TAG, "today date only ");
            viewHolder.txtTaskName.setText(tasks.getTasks());
            Log.e(TAG, "ui" + tasks.getTaskId() + " - " + tasks.getTasksStatus());
            Log.e(TAG, "Task priority " + tasks.getTasksPriority());
            if (tasks.getTasksStatus() == 0) {
                Log.e(TAG, "UI Status() == 0");
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(true);
                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(false);
                viewHolder.txtTaskName.setTextColor(Color.BLACK);
                viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.e(TAG, "UI onClick");
                    boolean b = viewHolder.checkBox.isChecked();
                    if (b) {
                        Log.e(TAG, "UI ckecked");
                        final Dialog alertDialog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        alertDialog.setContentView(R.layout.layout_twoaction_dialog);

                        alertDialog.setCancelable(false); //none-dismiss when touching outside Dialog
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        RelativeLayout dialogHeaderColor = (RelativeLayout) alertDialog.findViewById(R.id.md_styled_header_color);
                        // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));

                        TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.md_styled_dialog_title);
                        dialogTitle.setText(R.string.dialog_alert);
                        TextView msg = (TextView) alertDialog.findViewById(R.id.dialogMessage);
                        msg.setText(R.string.have_ucompleted_the_task_msg_dialog);

                        Button positive = (Button) alertDialog.findViewById(R.id.dialogButtonPositive);
                        positive.setText(R.string.yep_dialog_btn);
                        Log.e(TAG, "UI Title " + dialogTitle + " coleor " + dialogHeaderColor + " msg " + msg);

                        Button negative = (Button) alertDialog.findViewById(R.id.dialogButtonNegative);
                        negative.setText(R.string.not_yet_dialog_btn);

                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setChecked(false);
                            }
                        });

                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setEnabled(false);
                                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                urgentImpList.get(position).setTasksStatus(0);
                                tasksRepo.updateTaskCompleteStatus(urgentImpList.get(position));

                                //oast.makeText(getApplicationContext(), "Good work !! ", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                successDialog();
                                //DialogUtils.successDialog(MainActivity.this);

                            }
                        });

                        alertDialog.show();
                    } else {
                        Log.e(TAG, "UI unchecked");
                        viewHolder.txtTaskName.setTextColor(Color.BLACK);
                        viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                        viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }

            });


            viewHolder.txtTaskName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (tasks.getTasksStatus() == 1) {
                        Log.e(TAG, "UI onlong");
                        final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        contextDailog.setContentView(R.layout.layout_move_items);
                        contextDailog.setCancelable(true);
                        contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                        //dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                        dialogTitle.setText(getString(R.string.move_task_title) + urgentImpList.get(position).getTasks() + getString(R.string.move_task_title_to));
                        contextDailog.show();

                        TextView txtQuadrant2 = (TextView) contextDailog.findViewById(R.id.tvQuadrant2);
                        txtQuadrant2.setText(R.string.txt_q2);
                        txtQuadrant2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant2 ");
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(2);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));

                                urgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);
                                urgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                urgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });


                        TextView txtQuadrant3 = (TextView) contextDailog.findViewById(R.id.tvQuadrant3);
                        txtQuadrant3.setText(R.string.txt_q3);
                        txtQuadrant3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant3");
                                contextDailog.dismiss();

                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(3);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentImpList.add(tasksBean);
                                notifyItemChanged(position);
                                urgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentImpAdapter.notifyDataSetChanged();
                            }
                        });


                        TextView txtQuadrant4 = (TextView) contextDailog.findViewById(R.id.tvQuadrant4);
                        txtQuadrant4.setText(R.string.txt_q4);
                        txtQuadrant4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant3 ");
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(4);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);

                                urgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    return true;

                }
            });


            viewHolder.txtTaskName.setOnClickListener(new View.OnClickListener() {
                private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds

                long lastClickTime = 0;

                @Override
                public void onClick(View view) {
                    if (tasks.getTasksStatus() == 1) {
                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            Log.e("***", "onDoubleClick");
                            //onDoubleClick(v);
                            final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                            contextDailog.setContentView(R.layout.layout_context_dialog);
                            contextDailog.setCancelable(true);
                            contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                            // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                            dialogTitle.setText(urgentImpList.get(position).getTasks());
                            contextDailog.show();

                            TextView edit = (TextView) contextDailog.findViewById(R.id.action1);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "Edit Edit buttoin");
                                    contextDailog.dismiss();

                                    Intent newTask = new Intent(getApplicationContext(),
                                            NewTaskActivity.class);
                                    newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Log.e(TAG, "getTasksPriority bb " + urgentImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskId", urgentImpList.get(position).getTaskId());
                                    newTask.putExtra("taskName", urgentImpList.get(position).getTasks());
                                    newTask.putExtra("taskDate", urgentImpList.get(position).getTasksForDate());
                                    newTask.putExtra("taskTime", urgentImpList.get(position).getTasksForTime());
                                    newTask.putExtra("taskPriority", urgentImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskCategory", urgentImpList.get(position).getTasksCategory());

                                    startActivity(newTask);

                                }
                            });


                            TextView delete = (TextView) contextDailog.findViewById(R.id.action2);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "delete buttoin");
                                    contextDailog.dismiss();
                                    tasksRepo.deleteTask(urgentImpList.get(position));

                                    urgentImpList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }
                            });

                            TextView done = (TextView) contextDailog.findViewById(R.id.action3);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "done buttoin");
                                    contextDailog.dismiss();

                                    urgentImpList.get(position).setTasksStatus(0);
                                    tasksRepo.updateTaskCompleteStatus(urgentImpList.get(position));
                                    viewHolder.checkBox.setEnabled(false);
                                    viewHolder.checkBox.setChecked(true);
                                    viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                    viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                    viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                    //DialogUtils.successDialog(MainActivity.this);
                                    successDialog();
                                }
                            });
                        } else {
                            Log.e("***", "onSingleClick");
                            //onSingleClick(v);
                        }
                        lastClickTime = clickTime;

                    }
                }
            });

            //registerForContextMenu(viewHolder.itemView);
            Log.e(TAG, "task name " + tasks.getTasks());
        }

        @Override
        public int getItemCount() {
            return (null != urgentImpList ? urgentImpList.size() : 0);
        }


        protected class ViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox checkBox;
            private final TextView txtTaskName;


            public ViewHolder(View view) {
                super(view);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                txtTaskName = (TextView) view.findViewById(R.id.tvTaskName);

            }


        }

    }


    //Urgent, Not Important Q2 Adapter
    private class UrgentNotImportantTaskAdapter extends RecyclerView.Adapter<UrgentNotImportantTaskAdapter.ViewHolder> {
        private final String TAG = UrgentNotImportantTaskAdapter.class.getSimpleName();

        public UrgentNotImportantTaskAdapter() {
            Log.e(TAG, "  ");
        }

        @Override
        public UrgentNotImportantTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            //inflate your layout and pass it to view holder
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.task_items, viewGroup, false);
            return new UrgentNotImportantTaskAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final UrgentNotImportantTaskAdapter.ViewHolder viewHolder, final int position) {

            final TasksBean tasks = urgentNotImpList.get(position);

            //setting data to view holder elements
            Log.e(TAG, "UI size " + urgentNotImpList.size());
            viewHolder.txtTaskName.setText(tasks.getTasks());
            Log.e(TAG, "ui " + tasks.getTaskId() + " status " + tasks.getTasksStatus());
            Log.e(TAG, "Task priority " + tasks.getTasksPriority());
            if (tasks.getTasksStatus() == 0) {
                Log.e(TAG, "UI Status() == 0");
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(true);
                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(false);
                viewHolder.txtTaskName.setTextColor(Color.BLACK);
                viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.e(TAG, "UI onClick");
                    boolean b = viewHolder.checkBox.isChecked();
                    if (b) {
                        Log.e(TAG, "UI ckecked");
                        final Dialog alertDialog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        alertDialog.setContentView(R.layout.layout_twoaction_dialog);
                        //dialog.setTitle(title);
                        alertDialog.setCancelable(false); //none-dismiss when touching outside Dialog
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        RelativeLayout dialogHeaderColor = (RelativeLayout) alertDialog.findViewById(R.id.md_styled_header_color);
                        //dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));

                        TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.md_styled_dialog_title);
                        dialogTitle.setText(R.string.dialog_alert);

                        TextView msg = (TextView) alertDialog.findViewById(R.id.dialogMessage);
                        msg.setText(R.string.have_ucompleted_the_task_msg_dialog);

                        Button positive = (Button) alertDialog.findViewById(R.id.dialogButtonPositive);
                        positive.setText(R.string.yep_dialog_btn);

                        Button negative = (Button) alertDialog.findViewById(R.id.dialogButtonNegative);
                        negative.setText(R.string.not_yet_dialog_btn);

                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setChecked(false);
                            }
                        });

                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setEnabled(false);
                                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                urgentNotImpList.get(position).setTasksStatus(0);
                                tasksRepo.updateTaskCompleteStatus(urgentNotImpList.get(position));

                                //   Toast.makeText(getApplicationContext(), "Good work !! ", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                //DialogUtils.successDialog(MainActivity.this);
                                successDialog();
                            }
                        });

                        alertDialog.show();
                    } else {
                        Log.e(TAG, "UI  unchecked");
                        viewHolder.txtTaskName.setTextColor(Color.BLACK);
                        viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                        viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }

            });

            viewHolder.txtTaskName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (tasks.getTasksStatus() != 0) {
                        Log.e(TAG, "status == 1: ");
                        Log.e(TAG, "UI onlong");
                        final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        contextDailog.setContentView(R.layout.layout_move_items);
                        contextDailog.setCancelable(true);
                        contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        RelativeLayout dialogHeaderColor = (RelativeLayout) contextDailog.findViewById(R.id.md_styled_header_color);
                        ImageView dialogHeader = (ImageView) contextDailog.findViewById(R.id.md_styled_header);
                        TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                        // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                        dialogTitle.setText(getString(R.string.move_task_title) + urgentNotImpList.get(position).getTasks() + getString(R.string.move_task_title_to));

                        contextDailog.show();

                        TextView txtQuadrant1 = (TextView) contextDailog.findViewById(R.id.tvQuadrant2);
                        txtQuadrant1.setText(R.string.txt_q1);
                        txtQuadrant1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "Q1 ");
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(1);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                urgentImpList.add(tasksBean);
                                notifyItemChanged(position);

                                urgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                adapter.notifyDataSetChanged();
                            }
                        });


                        TextView txtQuadrant3 = (TextView) contextDailog.findViewById(R.id.tvQuadrant3);
                        txtQuadrant3.setText(R.string.txt_q3);
                        txtQuadrant3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant3");
                                contextDailog.dismiss();

                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(3);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentImpList.add(tasksBean);
                                notifyItemChanged(position);

                                urgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentImpAdapter.notifyDataSetChanged();
                            }
                        });

                        TextView txtQuadrant4 = (TextView) contextDailog.findViewById(R.id.tvQuadrant4);
                        txtQuadrant4.setText(R.string.txt_q4);
                        txtQuadrant4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(4);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);
                                urgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                    return true;
                }

            });

            viewHolder.txtTaskName.setOnClickListener(new View.OnClickListener() {
                private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds

                long lastClickTime = 0;

                @Override
                public void onClick(View view) {
                    if (tasks.getTasksStatus() != 0) {

                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            Log.e("***", "onDoubleClick");
                            final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                            contextDailog.setContentView(R.layout.layout_context_dialog);
                            contextDailog.setCancelable(true);
                            contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                            // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                            dialogTitle.setText(urgentNotImpList.get(position).getTasks());
                            contextDailog.show();

                            TextView edit = (TextView) contextDailog.findViewById(R.id.action1);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "Edit  buttoin");
                                    contextDailog.dismiss();

                                    Intent newTask = new Intent(getApplicationContext(),
                                            NewTaskActivity.class);
                                    newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Log.e(TAG, "getTasksPriority bb " + urgentNotImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskId", urgentNotImpList.get(position).getTaskId());
                                    newTask.putExtra("taskName", urgentNotImpList.get(position).getTasks());
                                    newTask.putExtra("taskDate", urgentNotImpList.get(position).getTasksForDate());
                                    newTask.putExtra("taskTime", urgentNotImpList.get(position).getTasksForTime());
                                    newTask.putExtra("taskPriority", urgentNotImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskCategory", urgentNotImpList.get(position).getTasksCategory());
                                    startActivity(newTask);

                                }
                            });


                            TextView delete = (TextView) contextDailog.findViewById(R.id.action2);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "delete  buttoin");
                                    contextDailog.dismiss();
                                    tasksRepo.deleteTask(urgentNotImpList.get(position));
                                    urgentNotImpList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }
                            });

                            TextView done = (TextView) contextDailog.findViewById(R.id.action3);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "done  buttoin");
                                    contextDailog.dismiss();
                                    urgentNotImpList.get(position).setTasksStatus(0);
                                    tasksRepo.updateTaskCompleteStatus(urgentNotImpList.get(position));
                                    viewHolder.checkBox.setEnabled(false);
                                    viewHolder.checkBox.setChecked(true);
                                    viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                    viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                    viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                    //DialogUtils.successDialog(MainActivity.this);
                                    successDialog();
                                }
                            });
                        } else {
                            Log.e("***", "onSingleClick");
                            //onSingleClick(v);
                        }
                        lastClickTime = clickTime;

                    }
                }
            });

            //registerForContextMenu(viewHolder.itemView);
            Log.e(TAG, "task name " + tasks.getTasks());
        }

        @Override
        public int getItemCount() {
            return (null != urgentNotImpList ? urgentNotImpList.size() : 0);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox checkBox;
            private final TextView txtTaskName;


            public ViewHolder(View view) {
                super(view);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                txtTaskName = (TextView) view.findViewById(R.id.tvTaskName);

            }
        }
    }

    //Not Urgent,Important Q3  Adapter
    private class NotUrgentImpTaskAdapter extends RecyclerView.Adapter<NotUrgentImpTaskAdapter.ViewHolder> {
        private final String TAG = NotUrgentImpTaskAdapter.class.getSimpleName();

        public NotUrgentImpTaskAdapter(Context applicationContext) {
            Log.e(TAG, "  ");
        }

        @Override
        public NotUrgentImpTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            //inflate your layout and pass it to view holder
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.task_items, viewGroup, false);

            return new NotUrgentImpTaskAdapter.ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final NotUrgentImpTaskAdapter.ViewHolder viewHolder, final int position) {
            final TasksBean tasks = notUrgentImpList.get(position);
            Log.e(TAG, "UI size " + notUrgentImpList.size());
            viewHolder.txtTaskName.setText(tasks.getTasks());
            Log.e(TAG, "ui " + tasks.getTaskId() + " - " + tasks.getTasksStatus());
            Log.e(TAG, "Task prioruty " + tasks.getTasksPriority());
            if (tasks.getTasksStatus() == 0) {
                Log.e(TAG, "UI Status() == 0");
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(true);
                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(false);
                viewHolder.txtTaskName.setTextColor(Color.BLACK);
                viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            try {

            } catch (Exception e) {

            }


            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.e(TAG, "UI onClick");
                    boolean b = viewHolder.checkBox.isChecked();
                    if (b) {
                        Log.e(TAG, "UI ckecked");
                        final Dialog alertDialog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        alertDialog.setContentView(R.layout.layout_twoaction_dialog);
                        //dialog.setTitle(title);
                        alertDialog.setCancelable(false); //none-dismiss when touching outside Dialog
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                        // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                        TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.md_styled_dialog_title);
                        dialogTitle.setText(R.string.dialog_alert);

                        TextView msg = (TextView) alertDialog.findViewById(R.id.dialogMessage);
                        msg.setText(R.string.have_ucompleted_the_task_msg_dialog);

                        Button positive = (Button) alertDialog.findViewById(R.id.dialogButtonPositive);
                        positive.setText(R.string.yep_dialog_btn);

                        Button negative = (Button) alertDialog.findViewById(R.id.dialogButtonNegative);
                        negative.setText(R.string.not_yet_dialog_btn);

                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setChecked(false);
                            }
                        });

                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setEnabled(false);
                                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                notUrgentImpList.get(position).setTasksStatus(0);
                                tasksRepo.updateTaskCompleteStatus(notUrgentImpList.get(position));

                                //         Toast.makeText(getApplicationContext(), "Good work !! ", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                //DialogUtils.successDialog(MainActivity.this);
                                successDialog();
                            }
                        });

                        alertDialog.show();
                    } else {
                        Log.e(TAG, "UI unchecked");
                        viewHolder.txtTaskName.setTextColor(Color.BLACK);
                        viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                        viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }

            });

            viewHolder.txtTaskName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (tasks.getTasksStatus() != 0) {
                        Log.e(TAG, "UI onlong");
                        final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        contextDailog.setContentView(R.layout.layout_move_items);
                        contextDailog.setCancelable(true);
                        contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                        // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                        dialogTitle.setText(getString(R.string.move_task_title) + notUrgentImpList.get(position).getTasks() + getString(R.string.move_task_title_to));

                        contextDailog.show();

                        TextView txtQuadrant1 = (TextView) contextDailog.findViewById(R.id.tvQuadrant2);
                        txtQuadrant1.setText(R.string.txt_q1);
                        txtQuadrant1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(1);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                urgentImpList.add(tasksBean);
                                notifyItemChanged(position);

                                notUrgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                adapter.notifyDataSetChanged();
                            }
                        });


                        TextView txtQuadrant2 = (TextView) contextDailog.findViewById(R.id.tvQuadrant3);
                        txtQuadrant2.setText(R.string.txt_q2);
                        txtQuadrant2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant2");
                                contextDailog.dismiss();

                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(2);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                urgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);
                                notUrgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                urgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });

                        TextView txtQuadrant4 = (TextView) contextDailog.findViewById(R.id.tvQuadrant4);
                        txtQuadrant4.setText(R.string.txt_q4);
                        txtQuadrant4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant3 ");
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(4);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);
                                notUrgentImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    return true;
                }
            });

            viewHolder.txtTaskName.setOnClickListener(new View.OnClickListener() {
                private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds
                long lastClickTime = 0;

                @Override
                public void onClick(View view) {
                    if (tasks.getTasksStatus() != 0) {
                        Log.e(TAG, "status == 1: " + tasks.getTasksStatus());

                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            Log.e("***", "onDoubleClick");
                            final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                            contextDailog.setContentView(R.layout.layout_context_dialog);
                            contextDailog.setCancelable(true);
                            contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                            // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                            dialogTitle.setText(notUrgentImpList.get(position).getTasks());
                            contextDailog.show();
                            TextView edit = (TextView) contextDailog.findViewById(R.id.action1);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "Edit  buttoin");
                                    contextDailog.dismiss();

                                    Intent newTask = new Intent(getApplicationContext(),
                                            NewTaskActivity.class);
                                    newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Log.e(TAG, "getTasksPriority bb " + notUrgentImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskId", notUrgentImpList.get(position).getTaskId());
                                    newTask.putExtra("taskName", notUrgentImpList.get(position).getTasks());
                                    newTask.putExtra("taskDate", notUrgentImpList.get(position).getTasksForDate());
                                    newTask.putExtra("taskTime", notUrgentImpList.get(position).getTasksForTime());
                                    newTask.putExtra("taskPriority", notUrgentImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskCategory", notUrgentImpList.get(position).getTasksCategory());

                                    startActivity(newTask);
                                }
                            });


                            TextView delete = (TextView) contextDailog.findViewById(R.id.action2);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "delete  buttoin");
                                    contextDailog.dismiss();
                                    tasksRepo.deleteTask(notUrgentImpList.get(position));

                                    notUrgentImpList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }
                            });

                            TextView done = (TextView) contextDailog.findViewById(R.id.action3);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "done buttoin");
                                    contextDailog.dismiss();

                                    notUrgentImpList.get(position).setTasksStatus(0);
                                    tasksRepo.updateTaskCompleteStatus(notUrgentImpList.get(position));
                                    viewHolder.checkBox.setEnabled(false);
                                    viewHolder.checkBox.setChecked(true);
                                    viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                    viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                    viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                    //DialogUtils.successDialog(MainActivity.this);
                                    successDialog();
                                }
                            });
                        } else {
                            Log.e("***", "onSingleClick");
                            //onSingleClick(v);
                        }
                        lastClickTime = clickTime;
                    }
                }

            });

            //registerForContextMenu(viewHolder.itemView);
            Log.e(TAG, "task name " + tasks.getTasks());

        }

        @Override
        public int getItemCount() {
            return (null != notUrgentImpList ? notUrgentImpList.size() : 0);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            private final CheckBox checkBox;
            private final TextView txtTaskName;

            public ViewHolder(View view) {
                super(view);

                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                txtTaskName = (TextView) view.findViewById(R.id.tvTaskName);

            }


        }

    }

    //Not Urgent, Not Important Q4 Adapter
    private class NotUrgentNotImpTaskAdapter extends RecyclerView.Adapter<NotUrgentNotImpTaskAdapter.ViewHolder> {
        private final String TAG = NotUrgentNotImpTaskAdapter.class.getSimpleName();

        public NotUrgentNotImpTaskAdapter() {
            Log.e(TAG, "  ");
        }

        @Override
        public NotUrgentNotImpTaskAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            //inflate your layout and pass it to view holder
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.task_items, viewGroup, false);
            return new NotUrgentNotImpTaskAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final NotUrgentNotImpTaskAdapter.ViewHolder viewHolder, final int position) {
            final TasksBean tasks = notUrgentNotImpList.get(position);
            Log.e(TAG, "UI size " + notUrgentNotImpList.size());
            viewHolder.txtTaskName.setText(tasks.getTasks());
            Log.e(TAG, "ui " + tasks.getTaskId() + " - " + tasks.getTasksStatus());
            Log.e(TAG, "Task prioruty " + tasks.getTasksPriority());
            if (tasks.getTasksStatus() == 0) {
                Log.e(TAG, "UI tatus() == 0");
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setChecked(true);
                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(false);
                viewHolder.txtTaskName.setTextColor(Color.BLACK);
                viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.e(TAG, "UI onClick");
                    boolean b = viewHolder.checkBox.isChecked();
                    if (b) {
                        Log.e(TAG, "UI ckecked");
                        final Dialog alertDialog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        alertDialog.setContentView(R.layout.layout_twoaction_dialog);
                        //dialog.setTitle(title);
                        alertDialog.setCancelable(false); //none-dismiss when touching outside Dialog
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.md_styled_dialog_title);
                        dialogTitle.setText(R.string.dialog_alert);

                        TextView msg = (TextView) alertDialog.findViewById(R.id.dialogMessage);
                        msg.setText(R.string.have_ucompleted_the_task_msg_dialog);

                        Button positive = (Button) alertDialog.findViewById(R.id.dialogButtonPositive);
                        positive.setText(R.string.yep_dialog_btn);

                        Button negative = (Button) alertDialog.findViewById(R.id.dialogButtonNegative);
                        negative.setText(R.string.not_yet_dialog_btn);

                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setChecked(false);
                            }
                        });

                        positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                viewHolder.checkBox.setEnabled(false);
                                viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                notUrgentNotImpList.get(position).setTasksStatus(0);
                                tasksRepo.updateTaskCompleteStatus(notUrgentNotImpList.get(position));
                                //        Toast.makeText(getApplicationContext(), "Good work !! ", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                notifyItemChanged(position);
                                //DialogUtils.successDialog(MainActivity.this);
                                successDialog();
                            }
                        });

                        alertDialog.show();
                    } else {
                        Log.e(TAG, "UI unchecked");
                        viewHolder.txtTaskName.setTextColor(Color.BLACK);
                        viewHolder.txtTaskName.setTypeface(null, Typeface.NORMAL);
                        viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }

            });
            viewHolder.txtTaskName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (tasks.getTasksStatus() != 0) {
                        Log.e(TAG, "UI onlong");
                        final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                        contextDailog.setContentView(R.layout.layout_move_items);
                        contextDailog.setCancelable(true);
                        contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                        // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                        dialogTitle.setText(getString(R.string.move_task_title) + notUrgentNotImpList.get(position).getTasks() + getString(R.string.move_task_title_to));
                        contextDailog.show();

                        TextView txtQuadrant1 = (TextView) contextDailog.findViewById(R.id.tvQuadrant2);
                        txtQuadrant1.setText(R.string.txt_q1);
                        txtQuadrant1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "Q1 ");
                                contextDailog.dismiss();
                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(1);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                urgentImpList.add(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                adapter.notifyDataSetChanged();
                            }
                        });


                        TextView txtQuadrant2 = (TextView) contextDailog.findViewById(R.id.tvQuadrant3);
                        txtQuadrant2.setText(R.string.txt_q2);
                        txtQuadrant2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant2");
                                contextDailog.dismiss();

                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(2);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                urgentNotImpList.add(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                urgentNotImpAdapter.notifyDataSetChanged();
                            }
                        });

                        TextView txtQuadrant3 = (TextView) contextDailog.findViewById(R.id.tvQuadrant4);
                        txtQuadrant3.setText(R.string.txt_q3);
                        txtQuadrant3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e(TAG, "txtQuadrant3 ");
                                contextDailog.dismiss();

                                TasksBean tasksBean = new TasksBean();
                                tasksBean.setTaskId(tasks.getTaskId());
                                tasksBean.setTasksForDate(tasks.getTasksForDate());
                                tasksBean.setTasksForTime(tasks.getTasksForTime());
                                tasksBean.setTasks(tasks.getTasks());
                                tasksBean.setTasksCategory("Office 2");
                                tasksBean.setTasksPriority(3);
                                tasksBean.setTasksStatus(1);
                                tasksBean.setTasksDateModified(dateFormat.format(new Date()));
                                notUrgentImpList.add(tasksBean);
                                notifyItemChanged(position);
                                notUrgentNotImpList.remove(position);
                                notifyItemRemoved(position);
                                tasksRepo.updateTasks(tasksBean);
                                notifyItemChanged(position);
                                notUrgentImpAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    return true;
                }

            });

            viewHolder.txtTaskName.setOnClickListener(new View.OnClickListener() {
                private static final long DOUBLE_CLICK_TIME_DELTA = 500;//milliseconds

                long lastClickTime = 0;

                @Override
                public void onClick(View view) {
                    if (tasks.getTasksStatus() != 0) {

                        long clickTime = System.currentTimeMillis();
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            final Dialog contextDailog = new Dialog(MainActivity.this, R.style.Dialog_Theme);
                            contextDailog.setContentView(R.layout.layout_context_dialog);
                            contextDailog.setCancelable(true);
                            contextDailog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            TextView dialogTitle = (TextView) contextDailog.findViewById(R.id.md_styled_dialog_title);
                            // dialogHeaderColor.setBackgroundColor(Color.parseColor("#1873cd"));
                            dialogTitle.setText(notUrgentNotImpList.get(position).getTasks());
                            contextDailog.show();

                            TextView edit = (TextView) contextDailog.findViewById(R.id.action1);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "Edit  buttoin");
                                    contextDailog.dismiss();

                                    Intent newTask = new Intent(getApplicationContext(),
                                            NewTaskActivity.class);
                                    newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Log.e(TAG, "getTasksPriority bb " + notUrgentNotImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskId", notUrgentNotImpList.get(position).getTaskId());
                                    newTask.putExtra("taskName", notUrgentNotImpList.get(position).getTasks());
                                    newTask.putExtra("taskDate", notUrgentNotImpList.get(position).getTasksForDate());
                                    newTask.putExtra("taskTime", notUrgentNotImpList.get(position).getTasksForTime());
                                    newTask.putExtra("taskPriority", notUrgentNotImpList.get(position).getTasksPriority());
                                    newTask.putExtra("taskCategory", notUrgentNotImpList.get(position).getTasksCategory());

                                    startActivity(newTask);
                                }
                            });


                            TextView delete = (TextView) contextDailog.findViewById(R.id.action2);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "delete  buttoin");
                                    contextDailog.dismiss();
                                    tasksRepo.deleteTask(notUrgentNotImpList.get(position));

                                    notUrgentNotImpList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }
                            });

                            TextView done = (TextView) contextDailog.findViewById(R.id.action3);
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG, "done buttoin");
                                    contextDailog.dismiss();

                                    notUrgentNotImpList.get(position).setTasksStatus(0);
                                    tasksRepo.updateTaskCompleteStatus(notUrgentNotImpList.get(position));
                                    viewHolder.checkBox.setEnabled(false);
                                    viewHolder.checkBox.setChecked(true);
                                    viewHolder.txtTaskName.setTextColor(Color.DKGRAY);
                                    viewHolder.txtTaskName.setTypeface(null, Typeface.ITALIC);
                                    viewHolder.txtTaskName.setPaintFlags(viewHolder.txtTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                    notifyItemChanged(position);
                                    notifyDataSetChanged();
                                    //DialogUtils.successDialog(MainActivity.this);
                                    successDialog();

                                }
                            });
                        } else {
                            Log.e("***", "onSingleClick");
                            //onSingleClick(v);
                        }
                        lastClickTime = clickTime;

                    }
                }
            });


            //registerForContextMenu(viewHolder.itemView);
            Log.e(TAG, "task name " + tasks.getTasks());
            //closeKeypad();
        }

        @Override
        public int getItemCount() {
            return (null != notUrgentNotImpList ? notUrgentNotImpList.size() : 0);
        }


        protected class ViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox checkBox;
            private final TextView txtTaskName;


            public ViewHolder(View view) {
                super(view);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                txtTaskName = (TextView) view.findViewById(R.id.tvTaskName);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSoftKeyboard();
        Log.i(TAG, "onResume: getPreviousDayTasks ");
        getAllPreviousDayTasksBasedOnStatus();

        //closeKeypad();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        Log.e(TAG, "onRestart ");
        startActivity(getIntent());
        //ArrayList<TasksBean> taskList = tasksRepo.getPreviousDayTasksBasedOnStatus();
        //Log.e(TAG, "onRestart: taskList " + taskList.size());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "OnBack pressed");
        hideSoftKeyboard();
    }

    // create folder if it not exist
    private void createFolder() {
        File sd = new File(folderSD);
        if (!sd.exists()) {
            sd.mkdir();
            Log.e(TAG, "create folder" + folderSD);
        } else {
            Log.e(TAG, "exits" + folderSD);
        }
    }

    /**
     * Copy database to sd card
     * name of file = database name + time when copy
     * When finish, we call onFinishExport method to send notify for activity
     */
    private void exportToSD() {

        String error = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = new Date();
            System.out.println(dateFormat.format(date)); //19-11-2016
            final String currentDate = dateFormat.format(date);
            createFolder();

            File sd = new File(folderSD);
            Log.e(TAG, "folder SD card " + folderSD);
            if (sd.canWrite()) {
                Log.e(TAG, "write data");
                SimpleDateFormat formatTime = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss", Locale.ENGLISH);
                String backupDBPath = dataName + "_" + formatTime.format(new Date()) + ".txt";

                File currentDB = new File(Environment.getDataDirectory(), data);
                File backupDB = new File(sd, backupDBPath);
                Log.e(TAG, "exportToSD: file " + backupDBPath + " backupDB File " + backupDB);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    Log.e(TAG, "db not exist");
                }
                Log.e(TAG, "exportToSD: " + backupDB);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                //intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                //intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, " ");
                intent.putExtra(Intent.EXTRA_SUBJECT, "SmartQs db Backup " + currentDate);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupDB));
                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "cannt write the file ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = "Error backup";
        }

        onFinishExport(error);
    }


    /**
     * import data from file backup on sd card
     * we must create a temp database for copy file on sd card to it.
     * Then we copy all row of temp database into main database.
     * It will keep struct of curren database not change when struct backup database is old
     *
     * @param fileNameOnSD name of file database backup on sd card
     */
    private void importData(String fileNameOnSD) {

        File sd = new File(folderSD);
        Log.e(TAG, "importData() " + folderSD);
        // create temp database
        SQLiteDatabase dbBackup = openOrCreateDatabase(dataTempName,
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        String error = null;

        if (sd.canWrite()) {
            Log.e(TAG, "importData() 2");
            File currentDB = new File(Environment.getDataDirectory(), dataTemp);
            File backupDB = new File(sd, fileNameOnSD);
            Log.e(TAG, "importData() 3 currentDB " + currentDB + " backupDB " + backupDB);
            if (currentDB.exists()) {
                FileChannel src;
                try {
                    src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    error = "Error load file";
                } catch (IOException e) {
                    error = "Error import";
                }
            }
        }
        /**
         *when copy old database into temp database success
         * we copy all row of table into main database
         */

        if (error == null) {
            new CopyDataAsyncTask(dbBackup).execute();
        } else {
            onFinishImport(error);
        }
    }

    /**
     * show dialog for select backup database before import database
     * if user select yes, we will export curren database
     * then show dialog to select old database to import
     * else we onoly show dialog to select old database to import
     */
    private void importFromSD() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.backup_data).setIcon(R.drawable.ic_importfile)
                .setMessage(R.string.backup_before_import);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialogListFile(folderSD);
            }
        });
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialogListFile(folderSD);
                exportToSD();
            }
        });
        builder.show();
    }

    /**
     * show dialog list all backup file on sd card
     *
     * @param forderPath folder conatain backup file
     */
    private void showDialogListFile(String forderPath) {
        createFolder();

        File forder = new File(forderPath);
        File[] listFile = forder.listFiles();

        final String[] listFileName = new String[listFile.length];
        for (int i = 0, j = listFile.length - 1; i < listFile.length; i++, j--) {
            listFileName[j] = listFile[i].getName();
        }

        if (listFileName.length > 0) {

            // get layout for list
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View convertView = inflater.inflate(R.layout.list_backup_file, null);

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);

            // set view for dialog
            builder.setView(convertView);
            builder.setTitle(R.string.select_file).setIcon(R.drawable.ic_importfile);

            alert = builder.create();
            edChooseFileName = (EditText) convertView.findViewById(R.id.edChooseFileName);
            ImageView btnChooseFile = (ImageView) convertView.findViewById(R.id.btnChooseFile);
            btnChooseFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file*//**//*");
                    try {
                        startActivityForResult(intent, PICKFILE_RESULT_CODE);
                    } catch (ActivityNotFoundException e) {
                        // maybe you should show a toast to the user here?
                        Toast.makeText(MainActivity.this, "You need to install a file picker app ", Toast.LENGTH_SHORT).show();
                        // or maybe redirect to a 3rd party app that you know works

                    }*/
                    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                    if (isKitKat) {
                        Intent intent = new Intent();
                        //intent.setType("file*/**");
                        intent.setType("file/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        Log.e(TAG, " pre lollipop devices ");
                        try {
                            startActivityForResult(intent, PICKFILE_RESULT_CODE);
                        } catch (ActivityNotFoundException e) {
                            // maybe you should show a toast to the user here?
                            Toast.makeText(MainActivity.this, "You need to install a file picker app ", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Log.e(TAG, "lollipop devices ");
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        //intent.setType("*/*");
                        intent.setType("file/*");
                        try {
                            startActivityForResult(intent, PICKFILE_RESULT_CODE);
                        } catch (ActivityNotFoundException e) {
                            // maybe you should show a toast to the user here?
                            Toast.makeText(MainActivity.this, "You need to install a file picker app ", Toast.LENGTH_SHORT).show();
                            // or maybe redirect to a 3rd party app that you know works

                        }
                    }

                }
            });


            ListView lv = (ListView) convertView.findViewById(R.id.lv_backup);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, listFileName);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    alert.dismiss();
                    importData(listFileName[position]);
                }
            });
            alert.show();

        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.delete).setIcon(R.drawable.ic_importfile)
                    .setMessage(R.string.backup_empty);
            builder.show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    boolean fileName = FilePath.contains("tasksdb");
                    if (fileName) {

                        Log.e(TAG, "onActivityResult: FilePath " + FilePath);
                        edChooseFileName.setText(FilePath);
                        // create temp database
                        SQLiteDatabase dbBackup = openOrCreateDatabase(dataTempName,
                                SQLiteDatabase.CREATE_IF_NECESSARY, null);

                        String error = null;


                        Log.e(TAG, "importData() 2 " + Environment.getDataDirectory());
                        File currentDB = new File(Environment.getDataDirectory(), dataTemp);
                        File backupDB = new File(FilePath);
                        Log.e(TAG, "importData() 3 currentDB " + currentDB + " backupDB " + backupDB);
                        if (currentDB.exists()) {
                            FileChannel src;
                            try {
                                src = new FileInputStream(backupDB).getChannel();
                                FileChannel dst = new FileOutputStream(currentDB)
                                        .getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                                alert.dismiss();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                error = "Error load file";
                            } catch (IOException e) {
                                error = "Error import";
                            }
                        }

                        /**
                         *when copy old database into temp database success
                         * we copy all row of table into main database
                         */

                        if (error == null) {
                            new CopyDataAsyncTask(dbBackup).execute();
                        } else {
                            onFinishImport(error);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please choose correct file ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    /**
     * AsyncTask for copy data
     */
    private class CopyDataAsyncTask extends AsyncTask<Void, Void, Void> {
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        final SQLiteDatabase db;

        public CopyDataAsyncTask(SQLiteDatabase dbBackup) {
            this.db = dbBackup;
        }

        /**
         * will call first
         */

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress.setMessage("Importing...");
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            copyData(db);
            return null;
        }

        /**
         * end process
         */
        @Override
        protected void onPostExecute(Void error) {
            // TODO Auto-generated method stub
            super.onPostExecute(error);

            if (progress.isShowing()) {
                progress.dismiss();
            }

            onFinishImport(null);

            urgentImpList = tasksRepo.getAllTasksBasedOnPriority(1);
            urgentNotImpList = tasksRepo.getAllTasksBasedOnPriority(2);
            notUrgentImpList = tasksRepo.getAllTasksBasedOnPriority(3);
            notUrgentNotImpList = tasksRepo.getAllTasksBasedOnPriority(4);
            adapter.notifyDataSetChanged();
            urgentNotImpAdapter.notifyDataSetChanged();
            notUrgentImpAdapter.notifyDataSetChanged();
            notUrgentNotImpAdapter.notifyDataSetChanged();
        }
    }

    /**
     * copy all row of temp database into main database
     *
     * @param dbBackup
     */
    private void copyData(SQLiteDatabase dbBackup) {

        //TasksRepo tasksRepo = new TasksRepo(getApplicationContext());
        //db.deleteNote(null);
        //tasksRepo.deleteTask(null);
        tasksRepo.deleteAllTasks();
        Log.e(TAG, "delete all tasks " + tasksRepo.deleteAllTasks());
        Log.e(TAG, "copyData() ");
        /** copy all row of subject table */
        Cursor cursor = dbBackup.query(true, DBHelper.TASK_TABLE, null, null, null, null, null, null, null);
        Log.e(TAG, "copyData()2 cursor  " + cursor);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TasksBean tasks = tasksRepo.cursorToTasks(cursor);
            //Log.e(TAG, "copyData()3 task row " + tasks);
            tasksRepo.save(tasks);
            //Log.e(TAG, "copyData()4 ");
            cursor.moveToNext();
        }

        cursor.close();


        deleteDatabase(dataTempName);
    }


}



package com.thincovate.taskmanager.smartqs.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thincovate.taskmanager.smartqs.R;
import com.thincovate.taskmanager.smartqs.activities.NewTaskActivity;


public class DialogUtils {


    public static void showDialogTwoActions(final Context ctx, String title, String message, String buttonPositive, String buttonNegative) {
        final Dialog dialog = new Dialog(ctx, R.style.Dialog_Theme);
        dialog.setContentView(R.layout.layout_twoaction_dialog);
        //dialog.setTitle(title);
        dialog.setCancelable(false); //none-dismiss when touching outside Dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        RelativeLayout dialogHeaderColor = (RelativeLayout) dialog.findViewById(R.id.md_styled_header_color);
        ImageView dialogHeader = (ImageView) dialog.findViewById(R.id.md_styled_header);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.md_styled_dialog_title);
        //dialogHeaderColor.setBackgroundColor(color);
        dialogTitle.setText(title);

        TextView msg = (TextView) dialog.findViewById(R.id.dialogMessage);
        msg.setText(message);

        Button positive = (Button) dialog.findViewById(R.id.dialogButtonPositive);
        if (buttonPositive != null)
            positive.setText(buttonPositive);


        Button negative = (Button) dialog.findViewById(R.id.dialogButtonNegative);
        if (buttonNegative != null)
            negative.setText(buttonNegative);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newTask = new Intent(ctx, NewTaskActivity.class);
                newTask.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newTask.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                ctx.startActivity(newTask);

            }
        });

        dialog.show();
    }

    public static void showSettingsDialogPositiveButton(final Context ctx, String title, String message, String buttonText) {

        final Dialog dialog = new Dialog(ctx, R.style.AppCompatAlertDialogStyle);

        dialog.setContentView(R.layout.layout_settings_dialog);
        //dialog.setTitle(title);
        //dialog.setCancelable(false); //none-dismiss when touching outside Dialog
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                dialog.dismiss();
                return false;
            }
        });
        /*TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
        dialogTitle.setText(title);*/
        TextView msg = (TextView) dialog.findViewById(R.id.dialogMessage);
        msg.setText(message);

        Button okButton = (Button) dialog.findViewById(R.id.buttonOk);
        if (buttonText != null)
            okButton.setText(buttonText);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
                intent.setData(uri);
                ctx.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showDialogPositiveButton(Context ctx, String title, String message, String buttonText) {

        final Dialog dialog = new Dialog(ctx, R.style.Dialog_Theme);

        dialog.setContentView(R.layout.layout_positivebtn_dialog);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                dialog.dismiss();
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        /*TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
        dialogTitle.setText(title);*/
        TextView msg = (TextView) dialog.findViewById(R.id.dialogMessage);
        msg.setText(message);
        RelativeLayout dialogHeaderColor = (RelativeLayout) dialog.findViewById(R.id.md_styled_header_color);
        ImageView dialogHeader = (ImageView) dialog.findViewById(R.id.md_styled_header);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.md_styled_dialog_title);
        //dialogHeaderColor.setBackgroundColor(color);
        dialogTitle.setText(title);
        Button okButton = (Button) dialog.findViewById(R.id.buttonOk);
        if (buttonText != null)
            okButton.setText(buttonText);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}

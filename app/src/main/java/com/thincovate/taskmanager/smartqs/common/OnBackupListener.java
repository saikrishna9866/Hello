package com.thincovate.taskmanager.smartqs.common;

/**
 * Created by home on 3/1/2017.
 */

public interface OnBackupListener {

    void onFinishExport(String error);

    void onFinishImport(String error);
}

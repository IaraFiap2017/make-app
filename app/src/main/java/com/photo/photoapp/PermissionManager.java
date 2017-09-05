package com.photo.photoapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionManager {

    Activity activity;
    Context mContext;

    public PermissionManager(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
    }

    public void managePermissions(int code) {
        if(!checkPermissions()) {
            requestPermissions(code);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public boolean checkPermissions() {
        if(!checkPermissionForCamera() || !checkPermissionForExternalStorage()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissions(int code){
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        ){
            Toast.makeText(
                    mContext.getApplicationContext(),
                    new ResultBuilderPTBR().addPermissionMessage().build(),
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    code
            );
        }
    }

}

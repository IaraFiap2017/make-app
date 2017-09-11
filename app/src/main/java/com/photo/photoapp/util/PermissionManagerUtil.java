package com.photo.photoapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.photo.photoapp.builder.ResultBuilderPTBR;

public class PermissionManagerUtil {

    Activity activity;
    Context mContext;

    public PermissionManagerUtil(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
    }

    public void managePermissions(int code) {
        if(!checkPermissions()) {
            requestPermissions(code);

            // reset the app permission context to no broke the app when creating files
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public boolean checkPermissions() {
        if(
            !checkPermissionForCamera()
            ||
            !checkPermissionForExternalStorage()
            ||
            !checkPermissionForNetworkState()
        )
            return false;
        else
            return true;
    }

    private boolean checkPermissionForExternalStorage(){
        if (
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ==
            PackageManager.PERMISSION_GRANTED
        )
            return true;
        else
            return false;
    }

    private boolean checkPermissionForCamera(){
        if (
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            ==
            PackageManager.PERMISSION_GRANTED
        )
            return true;
        else
            return false;
    }

    private boolean checkPermissionForNetworkState() {
        if (
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE)
            ==
            PackageManager.PERMISSION_GRANTED
        )
            return true;
        else
            return false;
    }

    private void requestPermissions(int code){
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE)
        )
            Toast
                .makeText(
                    mContext.getApplicationContext(),
                    new ResultBuilderPTBR().addPermissionMessage().build(),
                    Toast.LENGTH_LONG
                )
                .show();
        else
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE},
                    code
            );
    }

}

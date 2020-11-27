package com.mobileinspectionapp;

import android.Manifest;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;
import com.facebook.react.ReactActivity;

public class MainActivity extends ReactActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CheckPermission
                .from(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setRationaleConfirmText("Request permissions")
                .setDeniedMsg("Permissions denied")
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        ToastUtils.showLong("Permission Granted");
                    }

                    @Override
                    public void permissionDenied() {
                        ToastUtils.showLong("Permission Denied");
                    }
                })
                .check();
    }

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "MobileInspectionApp";
    }
}

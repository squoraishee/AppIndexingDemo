package com.vmn.appindexingdemo.util;

import android.widget.Toast;

import com.vmn.appindexingdemo.AppIndexingApplication;
import com.vmn.appindexingdemo.BuildConfig;

/**
 * Created by quoraiss on 1/3/16.
 */
public class AppUtil {

    public static void shortToast(String message) {
        if(BuildConfig.DEBUG) {
            Toast.makeText(AppIndexingApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
        }
    }

}

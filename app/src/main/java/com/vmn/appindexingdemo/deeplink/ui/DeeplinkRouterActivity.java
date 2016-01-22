package com.vmn.appindexingdemo.deeplink.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vmn.appindexingdemo.BuildConfig;
import com.vmn.appindexingdemo.deeplink.DeeplinkManager;
import com.vmn.appindexingdemo.util.AppUtil;


/* Good ideas on deeplink best practices: http://tech.just-eat.com/2015/06/29/deep-linking-in-android-the-easy-way/*/
public class DeeplinkRouterActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savesInstanceState) {
        super.onCreate(savesInstanceState);
        try {
            DeeplinkManager.getInstance().dispatchIntent(this, getIntent());
        } catch (IllegalArgumentException e) {
            if(BuildConfig.DEBUG) {
                AppUtil.shortToast("Bad Url!");
            }
        } finally {
            finish();
        }
    }

}

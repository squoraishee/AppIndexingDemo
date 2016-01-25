package com.vmn.appindexingdemo.deeplink.util;

import android.content.Context;
import android.content.Intent;

import com.vmn.appindexingdemo.AppIndexingApplication;
import com.vmn.appindexingdemo.content.ui.ContentViewActivity;

/**
 * In order to construct multiple intents, you want something that will return the appropriate intent
 * for a specific input.
 *
 */
public class IntentHelper {

    public final static String CONTENT_ID = "CONTENT_ID";

    /**
     * For now, we only have a singular content intent
     *
     * @return
     */
    public static Intent contentIntent(Context context, String contentId) {
        Intent i = new Intent(context, ContentViewActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(CONTENT_ID, contentId);
        return i;
    }
}

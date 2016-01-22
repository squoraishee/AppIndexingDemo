package com.vmn.appindexingdemo.deeplink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.vmn.appindexingdemo.AppIndexingApplication;
import com.vmn.appindexingdemo.deeplink.util.IntentHelper;
import com.vmn.appindexingdemo.util.AppUtil;

/**
 * This class is responsible for mapping URIs to intents.  This is the URI Intent mapper pattern
 */
public class DeeplinkManager {

    private static DeeplinkManager instance;

    private DeeplinkManager() {}

    public static DeeplinkManager getInstance() {
        if(instance == null) {
            instance = new DeeplinkManager();
        }

        return instance;
    }

    /**
     * We need to pass the context here because there is a difference in the activity call chain if we
     * call an activity intent from an existing activity or outside the activity (for example, the applciation context)
     *
     * @param context
     * @param intent
     */
    public void dispatchIntent(Context context, Intent intent) {
        final Uri uri = intent.getData();
        Intent dispatchIntent = null;

        if (uri == null) throw new IllegalArgumentException("Uri cannot be null");

        dispatchIntent = getContentIntent(context, uri);

        /* Once we have the obtained scheme, then we need the proper intent to dispatch to */
        if(dispatchIntent != null) {
            context.startActivity(dispatchIntent);
        }
    }

    /**
     * Generate a content intent
     *
     * @param uri
     * @return
     */
    private Intent getContentIntent(Context context, Uri uri) {

        final String path = uri.getPath().toLowerCase();
        AppUtil.shortToast("Current path is: " + path);
        return IntentHelper.contentIntent(context, path);
    }


}

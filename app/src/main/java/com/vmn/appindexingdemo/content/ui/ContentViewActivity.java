package com.vmn.appindexingdemo.content.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vmn.appindexingdemo.AppIndexingApplication;
import com.vmn.appindexingdemo.AppIndexingDemoMainActivity;
import com.vmn.appindexingdemo.R;
import com.vmn.appindexingdemo.deeplink.util.IntentHelper;
import com.vmn.appindexingdemo.feeds.delegates.FeedListener;
import com.vmn.appindexingdemo.model.RemoteVideo;
import com.vmn.appindexingdemo.util.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ContentViewActivity extends AppCompatActivity implements FeedListener {

    ArrayList<RemoteVideo> remoteVideos = new ArrayList<>();
    private GoogleApiClient googleApiClient;

    final static String ROOT = "/mobileapps-experimental/clip_freaks_links/";
    final static String FEED_URL =
            "https://s3.amazonaws.com/mobileapps-experimental/clip_freaks/data_source.json";
    private final OkHttpClient client = new OkHttpClient();
    private final String TAG = ContentViewActivity.class.getCanonicalName();

    VideoView videoPlayer;
    Handler handler = new Handler();
    String currentContentId = AppIndexingApplication.EMPTY;
    ConcurrentHashMap<String, RemoteVideo> remoteVideoMap;

    TextView clipTitleText;
    TextView clipDescriptionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_app_indexing_demo_main);

        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        clipTitleText = (TextView)findViewById(R.id.clip_title_text);
        clipDescriptionText = (TextView)findViewById(R.id.description_text);

        remoteVideoMap = new ConcurrentHashMap<>();

        currentContentId = extractContentId(getIntent());

        initUI();
        try {
            loadFeeds(FEED_URL);
        } catch (Exception e) {
            Log.e(TAG, "Could not load feeds!");
        }
    }

    private String extractContentId(Intent intent) {
        String contentId = intent.getStringExtra(IntentHelper.CONTENT_ID);
        if(contentId != null) {
            contentId = contentId.replace(ROOT, AppIndexingApplication.EMPTY);
            return contentId;
        } else {
            return AppIndexingApplication.EMPTY;
        }
    }

    public void initUI() {
        videoPlayer = (VideoView)findViewById(R.id.clip_video);
    }

    public void playVideo(Uri uri) {
        videoPlayer.setVideoURI(uri);
        videoPlayer.start();
        videoPlayer.requestFocus();
    }

    public void loadFeeds(String feedUrl) throws Exception {
        Request request = new Request.Builder()
                .url(feedUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Feed request failed!");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e(TAG, "Feed request succeeded!");
                final String responseData = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFeedsLoaded(responseData);
                    }
                });
            }
        });
    }

    @Override
    public void onFeedsLoaded(String data) {
        try {
            JSONObject feedData = new JSONObject(data);
            Iterator<?> keys = feedData.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                remoteVideoMap.put(key, new RemoteVideo(
                        key,
                        feedData.getJSONObject(key).getString("src"),
                        feedData.getJSONObject(key).getString("src"),
                        feedData.getJSONObject(key).getString("title"),
                        feedData.getJSONObject(key).getString("description"),
                        feedData.getJSONObject(key).getString("video_link"),
                        feedData.getJSONObject(key).getString("deep_link")
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "No data found!");
        }
        if(!currentContentId.equals(AppIndexingApplication.EMPTY)) {

            final String title = remoteVideoMap.get(currentContentId).getTitle();
            String description = remoteVideoMap.get(currentContentId).getDescription();
            Uri uri = Uri.parse(remoteVideoMap.get(currentContentId).getDeepLink());

            clipTitleText.setText(title);
            clipDescriptionText.setText(description);

            PendingResult<Status> result = AppIndex.AppIndexApi.start(googleApiClient, getAction(title, description, uri));
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        AppUtil.shortToast("Successfully recorded page view for " + title);
                    } else {
                        AppUtil.shortToast("Error recording page view for " + title);
                    }
                }
            });

            playVideo(Uri.parse(remoteVideoMap.get(currentContentId).getVideoLink()));
        }
    }

    //very simple index event call
    public void indexEvent(String title, String description, String uri) {
        PendingResult<Status> result = AppIndex.AppIndexApi.start(googleApiClient, getAction(title, description, uri));
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    AppUtil.shortToast("Successfully recorded page view for " + title);
                } else {
                    AppUtil.shortToast("Error recording page view for " + title);
                }
            }
        });
    }


    public Action getAction(String title, String description, Uri url) {
        Thing object = new Thing.Builder()
                .setName(title)
                .setDescription(description)
                .setUrl(url)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        super.onStop();
    }

}

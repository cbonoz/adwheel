package com.adwheel.www.wheel.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class WebService extends IntentService {
    private static final String TAG = "WebService";
    public WebService() {
        super("WebService");
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        Log.d(TAG, "Post " + url + ": " + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        String body = intent.getStringExtra("body");
        Log.d(TAG, "WebService");
        String result = null;
        try {
            result = post(url, body);
        } catch (Exception e) {
            Log.e(TAG, "Error with post request: " + url);
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "Leaderboards Disabled", Toast.LENGTH_SHORT).show();
            result = null;
        } finally {
            Log.d(TAG, "Result " + url + ": " + result);
            if (result != null) {
                String username = null;
            }
        }
    }
}

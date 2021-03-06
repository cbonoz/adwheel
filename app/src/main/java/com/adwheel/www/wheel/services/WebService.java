package com.adwheel.www.wheel.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
    private static final String TAG = WebService.class.getSimpleName();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String BASE_URL = "http://blackshoalgroup.com:9005";
    public static final String AD_API = BASE_URL + "/ra/ad";

    private static OkHttpClient client = new OkHttpClient.Builder().build();

    public WebService() {
        super("WebService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String body = intent.getStringExtra("body");
            final String url = intent.getStringExtra("url");
            String result = "";
            try {
                result = post(url, body);
            } catch (Exception e) {
                result = e.toString();
            } finally {
                // No need to broadcast.
                Log.d(TAG, "Response: " + result);
            }
        }
    }

    private String post(String url, String json) throws IOException {
        Log.d(TAG, "post: " + url + ": " + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}

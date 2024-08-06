package com.example.foodapp.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CloudFunctions {
    private static final String TAG = "Phuc";

    private static final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    public static void callReadyFunction(String uid){
        String url = "https://us-central1-food-app-5fe11.cloudfunctions.net/sendReadyNotification/" + uid;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Log.i(TAG, "Ready function successful!");
                }
                else Log.i(TAG, "Ready function failed!");
            }
        });
    }

    public static void callNotificationFunction(String uid, String type){
        Map<String, Object> data = new HashMap<>();
        data.put("params1", uid);
        data.put("params2", type);

        mFunctions
                .getHttpsCallable("sendNotification")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.i(TAG, "Test function successful!");
                    }
                    else Log.i(TAG, "Test function failed!");
                });
    }
}

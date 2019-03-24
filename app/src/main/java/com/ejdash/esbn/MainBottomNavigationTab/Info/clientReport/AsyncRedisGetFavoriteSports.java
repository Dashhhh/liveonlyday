package com.ejdash.esbn.MainBottomNavigationTab.Info.clientReport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ejdash.esbn.utils.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ej on 2017-10-26.
 */

public class AsyncRedisGetFavoriteSports extends AsyncTask<Void, Integer, JSONObject> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String userId;

    public AsyncRedisGetFavoriteSports(Context mContext) {
        this.mContext = mContext;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jsonObject = new JSONObject();
        String result = null;
        try {
            SharedPreferenceUtil pref = new SharedPreferenceUtil(mContext);

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("userid", pref.getSharedData("userId"))
                    .build();
            Request request = new Request.Builder()
                    .url("http://13.124.128.18/esbn/redis/favoriteScore.php")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            result = response.body().string();
            JSONObject castJSON = new JSONObject(result);
            jsonObject = castJSON;
            Log.i("redisCheck", "AsyncRedisActiveUpCount > getRedisKeyData > result > " + result);
            Log.i("redisCheck", "AsyncRedisActiveUpCount > getRedisKeyData > castJSON > " + castJSON);
            Log.i("redisCheck", "AsyncRedisActiveUpCount > getRedisKeyData > jsonObject > " + jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

    }

}

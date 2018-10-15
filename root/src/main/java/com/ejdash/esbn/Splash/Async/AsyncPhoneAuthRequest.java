package com.ejdash.esbn.Splash.Async;

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

public class AsyncPhoneAuthRequest  extends AsyncTask<Void, Integer, JSONObject> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private String userId;
        private String phoneNumber;

        public AsyncPhoneAuthRequest(Context mContext, String phoneNumber) {
            this.mContext = mContext;
            this.userId = userId;

            this.phoneNumber = phoneNumber;
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
                        .add("action", "go")
                        .add("rphone", phoneNumber)
                        .add("sphone1", "010")
                        .add("sphone2", "3209")
                        .add("sphone3", "4509")
                        .build();
                Request request = new Request.Builder()
                        .url("http://222.122.203.55/sms/smsRequest.php")
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

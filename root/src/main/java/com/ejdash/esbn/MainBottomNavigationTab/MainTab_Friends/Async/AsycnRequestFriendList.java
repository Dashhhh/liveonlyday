package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsycnRequestFriendList extends AsyncTask<Void, Integer, JSONObject> {

    String sourceId;

        public AsycnRequestFriendList(String sourceId) {
            this.sourceId = sourceId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            String result = null;
            try {

                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("sourceId", sourceId)
                        .build();

                Request request = new Request.Builder()
                        .url("http://13.124.128.18/esbn/friends/responseFriendList.php")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                /*


                */
                result = response.body().string();
                Log.i("AsycnRequestFriendList", "AsycnRequestFriendList > result > " + result);
                JSONObject castJSON = new JSONObject(result);
                jsonObject = castJSON;
                Log.i("AsycnRequestFriendList", "AsycnRequestFriendList > castJSON > " + castJSON);
                Log.i("AsycnRequestFriendList", "AsycnRequestFriendList > jsonObject > " + jsonObject);
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

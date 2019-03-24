package com.ejdash.esbn.MainBottomNavigationTab.Friends.Async;

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

public class AsycnAddingFriendsRequest extends AsyncTask<Void, Integer, JSONObject> {

    String sourceId;
    String targetId;


        public AsycnAddingFriendsRequest(String sourceId, String targetId) {
            this.sourceId = sourceId;
            this.targetId = targetId;
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
                        .add("targetId", targetId)
                        .build();
                Request request = new Request.Builder()
                        .url("http://13.124.128.18/esbn/friends/addingFriends.php")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();

                /*



                */

                result = response.body().string();
                JSONObject castJSON = new JSONObject(result);
                jsonObject = castJSON;
                Log.i("FriendsRequest", "AsycnAddingFriendsRequest > result > " + result);
                Log.i("FriendsRequest", "AsycnAddingFriendsRequest > castJSON > " + castJSON);
                Log.i("FriendsRequest", "AsycnAddingFriendsRequest > jsonObject > " + jsonObject);

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

package com.ejdash.esbn.WebRTC.Live_Viewer.Async;

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

public class AsyncSendPointToStreamer extends AsyncTask<Void, Integer, JSONObject> {

    private String message;
    private String presenterId;
    private String viewerId;
    private String sendPoint;

    public AsyncSendPointToStreamer(String presenterId, String viewerId, String sendPoint, String message) {
        this.presenterId = presenterId;
        this.viewerId = viewerId;
        this.sendPoint = sendPoint;
        this.message = message;
    }

    public AsyncSendPointToStreamer() {
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
                    .add("presenterId", presenterId)
                    .add("viewerId", viewerId)
                    .add("donationPoint", sendPoint)
                    .add("donationMessage", message)
                    .build();

            Request request = new Request.Builder()
                    .url("http://13.124.128.18/esbn/broadcast/sendPointToStreamer.php")
                    .post(formBody)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            Response response = client.newCall(request).execute();

            result = response.body().string();
            JSONObject castJSON = new JSONObject(result);
            jsonObject = castJSON;
            Log.i("sendPointToStreamer", "message > " + message);
            Log.i("sendPointToStreamer", "presenterId > " + presenterId);
            Log.i("sendPointToStreamer", "viewerId > " + viewerId);
            Log.i("sendPointToStreamer", "sendPoint > " + sendPoint);
            Log.i("sendPointToStreamer", "result > " + result);
            Log.i("sendPointToStreamer", "castJSON > " + castJSON);
            Log.i("sendPointToStreamer", "jsonObject > " + jsonObject);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

    }

}

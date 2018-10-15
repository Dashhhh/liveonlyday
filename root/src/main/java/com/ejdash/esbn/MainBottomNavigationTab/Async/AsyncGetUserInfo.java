package com.ejdash.esbn.MainBottomNavigationTab.Async;

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

public class AsyncGetUserInfo extends AsyncTask<Void, Integer, JSONObject> {

    private String userId;


    public AsyncGetUserInfo(String userId) {
        this.userId = userId;
    }

    public AsyncGetUserInfo() {
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
                    .add("id", userId)
                    .build();
            Request request = new Request.Builder()
                    .url("http://13.124.128.18/esbn/user/getUserInfo.php")
                    .post(formBody)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();

                /*
                        cid:TC0ONETIME
                        partner_order_id:partner_order_id
                        partner_user_id:partner_user_id
                        item_name:테스트아이템
                        quantity:22
                        total_amount:11
                        vat_amount:1
                        approval_url:https://developers.kakao.com/success
                        fail_url:https://developers.kakao.com/fail
                        cancel_url:https://developers.kakao.com/cancel
                        tax_free_amount:0


                */
            result = response.body().string();
            JSONObject castJSON = new JSONObject(result);
            jsonObject = castJSON;
            Log.i("userInfoCheck", "result > " + result);
            Log.i("userInfoCheck", "castJSON > " + castJSON);
            Log.i("userInfoCheck", "jsonObject > " + jsonObject);
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

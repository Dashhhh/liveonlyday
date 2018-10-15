package com.ejdash.esbn.Splash.TestActivity;

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

public class AsycnKakaoRequest extends AsyncTask<Void, Integer, JSONObject> {

        @SuppressLint("StaticFieldLeak")

        public AsycnKakaoRequest() {
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
                        .add("cid", "TC0ONETIME")
                        .add("partner_order_id", "partner_order_id")
                        .add("partner_user_id", "partner_user_id")
                        .add("item_name", "테스트아이템")
                        .add("quantity", "22")
                        .add("total_amount", "11")
                        .add("vat_amount", "1")
                        .add("approval_url", "https://developers.kakao.com/success")
                        .add("fail_url", "https://developers.kakao.com/fail")
                        .add("cancel_url", "https://developers.kakao.com/cancel")
                        .add("tax_free_amount", "0")
                        .build();
                Request request = new Request.Builder()
                        .url("https://kapi.kakao.com/v1/payment/ready")
                        .post(formBody)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Authorization", "KakaoAK 3bd2eda6b0b2bdf133496a4e8bb231ed")
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
                Log.i("kakaopayCheck", "kakaopayCheck > result > " + result);
                Log.i("kakaopayCheck", "kakaopayCheck > castJSON > " + castJSON);
                Log.i("kakaopayCheck", "kakaopayCheck > jsonObject > " + jsonObject);
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

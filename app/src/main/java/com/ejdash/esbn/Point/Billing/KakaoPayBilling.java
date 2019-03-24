package com.ejdash.esbn.Point.Billing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ejdash.esbn.MainBottomNavigationTab.MainActivity;
import com.ejdash.esbn.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.util.EncodingUtils;

import java.util.Objects;

public class KakaoPayBilling extends AppCompatActivity {

    WebView payWeb;
    BootstrapButton dialogKakaoBillingSuccessConfirm;
    BootstrapButton dialogKakaoBillingFailConfirm;
    private ProgressDialog progressDialog;
    private String TAG = "KakaoCheck";
    private boolean payingFlag;

    @Override
    protected void onResume() {
        super.onResume();
        if(payingFlag){
            payingFlag = false;
            payingFlag = true;
            Dialog dialog = new Dialog(KakaoPayBilling.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_kakaopay_billing_success);
            WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            lp.dimAmount = 0.8f;
            dialog.show();

            dialogKakaoBillingSuccessConfirm = dialog.findViewById(R.id.dialogKakaoBillingSuccessConfirm);
            dialogKakaoBillingSuccessConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
                Intent intent = new Intent(KakaoPayBilling.this, MainActivity.class);
                startActivity(intent);

            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_pay);
        init();
        webViewSetting();


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webViewSetting() {
        /**
         * Desktop 모드로 접속
         */
        String desktopMode = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
        payWeb.getSettings().setUserAgentString(desktopMode);
        payWeb.setInitialScale(200);
        progressDialog = new ProgressDialog(this);

        WebSettings settings = payWeb.getSettings();
        /*
            // HTML 코드 아래 내용과 같은 형태 사용 할 수 있음 (meta 중 viewport 사용 가능)
            <meta name="viewport" content="width=320, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

        */
        payWeb.getSettings().setUseWideViewPort(true);


        settings.setJavaScriptEnabled(true);    // Javascript 허용 할지 말지 설정
        settings.setSupportZoom(false);          // 손가락으로 확대 / 축소 여부
        settings.setBuiltInZoomControls(false);  // Android에서 제공하는 Zoom Icon 사용할지 설정
        settings.setAllowFileAccess(true);
        settings.setTextZoom(110);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);     // Ajax와 같은 DOM 모델을 사용 할 지 > Local에서 Ajax돌릴 때에 의미 있는 것 같음

        Intent intent = getIntent();

        payWeb.invokeZoomPicker();
        String amount = intent.getStringExtra("amount");             // 충전할 별풍선의 양
        String buyer_email = intent.getStringExtra("buyer_email");        // Email 주소 (DB Table > user 정보 중 email)
        String buyer_name = intent.getStringExtra("buyer_name");         // 사용자 이름 (DB Table > user 정보 중 id)
        String buyer_tel = intent.getStringExtra("buyer_tel");          // 사용자 번호 (DB Table > user 정보 중 phone)
        String buyer_addr = "미 기재";         // '미 기재'로 넘김
        String buyer_postcode = "000-000";     // '000-000'으로 넘김
        String redirectUrl = "http://13.124.128.18/esbn/kakao/pay/pointAdd.php";     // '000-000'으로 넘김


        Log.i(TAG, "amount          > " + amount);
        Log.i(TAG, "buyer_email     > " + buyer_email);
        Log.i(TAG, "buyer_name      > " + buyer_name);
        Log.i(TAG, "buyer_tel       > " + buyer_tel);
        String url = "http://13.124.128.18/esbn/kakao/pay/paying.php?";

        /*
         *  Post Parameter
         */
        String body = "amount=" + amount + "&" +
                "buyer_email=" + buyer_email + "&" +
                "buyer_name=" + buyer_name + "&" +
                "buyer_tel=" + buyer_tel + "&" +
                "buyer_addr=" + buyer_addr + "&" +
                "buyer_postcode=" + buyer_postcode + "&" +
                "m_redirect_url=" + redirectUrl;

        payWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.setMessage("Loading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                Log.i(TAG, "setWebViewClient > onPageStarted > 발생");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG, "setWebViewClient > shouldOverrideUrlLoading > 발생");
                return super.shouldOverrideUrlLoading(view, request);
//                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "setWebViewClient > onPageFinished > 발생");
                progressDialog.dismiss();
            }
        });


        /*
            팝업 창과 같은 요소를 보여주기 위해 설정 (ex. alert())
         */
        payWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "setWebChromeClient > onProgressChanged > 발생");
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.i(TAG, "setWebChromeClient > onJsPrompt 발생");
                Log.i(TAG, "setWebChromeClient > onJsPrompt > url > " + url);
                Log.i(TAG, "setWebChromeClient > onJsPrompt > message > " + message);
                Log.i(TAG, "setWebChromeClient > onJsPrompt > " + result.toString());
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }


            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                Log.i(TAG, "onJsConfirm: 호출!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i(TAG, "setWebChromeClient > onConsoleMessage > consoleMessage.message() > " + consoleMessage.message());

                String type = null;

                try {
                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(consoleMessage.message());

                    type = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("type").getAsString();
                    String billingId = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("billingId").getAsString();
                    String shopBillingId = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("shopBillingId").getAsString();
                    String amount = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("amount").getAsString();
                    Log.i(TAG, "onConsoleMessage > type > " + type);
                    Log.i(TAG, "onConsoleMessage > billingId > " + billingId);
                    Log.i(TAG, "onConsoleMessage > shopBillingId > " + shopBillingId);
                    Log.i(TAG, "onConsoleMessage > amount > " + amount);


                    if (type != null && type.equals("success")) {

                        payingFlag = true;
                        Dialog dialog = new Dialog(KakaoPayBilling.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_kakaopay_billing_success);
                        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                        lp.dimAmount = 0.8f;
                        dialog.show();

                        dialogKakaoBillingSuccessConfirm = dialog.findViewById(R.id.dialogKakaoBillingSuccessConfirm);
                        dialogKakaoBillingSuccessConfirm.setOnClickListener(v -> {
                            dialog.dismiss();
                            finish();
                            Intent intent = new Intent(KakaoPayBilling.this, MainActivity.class);
                            startActivity(intent);

                        });
                    }

                } catch (Exception e) {

                    Dialog dialog = new Dialog(KakaoPayBilling.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_kakaopay_billing_fail);
                    WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                    lp.dimAmount = 0.8f;
                    dialog.show();
                    dialogKakaoBillingFailConfirm = dialog.findViewById(R.id.dialogKakaoBillingFailConfirm);
                    dialogKakaoBillingFailConfirm.setOnClickListener(v -> {
                        dialog.dismiss();
                        finish();
                    });

                    Toast.makeText(KakaoPayBilling.this, "", Toast.LENGTH_SHORT).show();

                }

                return true;
//                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Log.i(TAG, "setWebChromeClient > onJsAlert > 발생");

                Log.i(TAG, "setWebChromeClient > onJsAlert > url > " + url);
                Log.i(TAG, "setWebChromeClient > onJsAlert > message > " + message);
                Log.i(TAG, "setWebChromeClient > onJsAlert > result > " + result.toString());
                return true;
//                return super.onJsAlert(view, url, message, result);
            }


        });


        payWeb.postUrl(url, EncodingUtils.getBytes(body, "BASE64"));

    }

    private void init() {
        payWeb = findViewById(R.id.payWeb);
    }
}


        /*
          KakaoPay 직접 구현할 때 Webview를 통해 서버에 넘겨야 하는 파라메터 예제임
           1. Header - header에 https 관련 + Kakao Admin Key 관련 인증 값 추가
           2. Body   - Kakaopay 결제 관련 파라메터임 - 전부 필수 값이고 + post body 함께 넘겨야함
         */

        /*
        Header 부분
         */
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/x-www-form-urlencoded");
//        header.put("Authorization", "3bd2eda6b0b2bdf133496a4e8bb231ed");

        /*
        Body 부분
         */
//        String url = "https://kapi.kakao.com/v1/payment/ready?";
//        String body = "cid=TC0ONETIME&partner_order_id=partner_order_id" +
//                "&partner_user_id=partner_user_id" +
//                "&item_name=테스트아이템&quantity=22" +
//                "&total_amount=11&vat_amount=1" +
//                "&approval_url=https://developers.kakao.com/success" +
//                "&fail_url=https://developers.kakao.com/fail" +
//                "&cancel_url=https://developers.kakao.com/cancel" +
//                "&tax_free_amount=0";
/*
    * KakaoPay 결제 최초 요청 날릴때 (Ready)

        JSONObject json = new JSONObject();
        try {
            json = new AsyncGetUserInfo().execute().get(10000, TimeUnit.MILLISECONDS);
            Log.i("kakaopayCheck", "onCreate > json > " + json.toString());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
*/
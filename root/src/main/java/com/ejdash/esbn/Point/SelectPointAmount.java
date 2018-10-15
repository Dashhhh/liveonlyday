package com.ejdash.esbn.Point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ejdash.esbn.Point.Billing.KakaoPayBilling;
import com.ejdash.esbn.R;
import com.ejdash.esbn.utils.SharedPreferenceUtil;


/**
 * 별풍선 개념의 포인트를 충전하기 위해서 만듦
 * 실제 결제 전 단계인 액티비티이며, 이 곳에서 금액을 선택하고 pointLetsPaying 버튼 누르면 결제 시작
 */
public class SelectPointAmount extends AppCompatActivity {

    android.support.v7.widget.AppCompatEditText payCustomAmount;
    android.support.v7.widget.CardView payingSmall, payingMid, payingBig;
    TextView pointNow, pointFuture, pointPrice;


    int nowPoint = 0;       // 현재 보유 포인트
    int futurePoint = 0;    // 충전 후 보유 포인트, 충전 금액에 맞추어서 동적으로 변함
    int payingPrice = 0;
    com.beardedhen.androidbootstrap.BootstrapButton pointLetsPaying;
    private String TAG = "selectPayAmount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_point_amount);
        initView();
        selectPayingIcon();
        viewListener();

    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void viewListener() {



        /*
         *  글자 하나를 입력 받은 직후 아래 순서로 메서드 호출 됨
         *      1. beforeTextChanged()
         *      2. onTextChanged()
         *      3. afterTextChanged()
         *
         *  '결제 예상 금액'에 실시간으로 금액 반영
         */


        payCustomAmount.setOnTouchListener((v, event) -> {
            Log.i(TAG, "viewListener >  호출");
            payingSmall.setCardBackgroundColor(0xffffffff);
            payingMid.setCardBackgroundColor(0xffffffff);
            payingBig.setCardBackgroundColor(0xffffffff);
            return false;
        });

        payCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (payCustomAmount.getText().toString().trim().length() != 0) {

                    int price = Integer.parseInt(payCustomAmount.getText().toString());

                    /*
                     *  EditText 에 4글자 이상이고 3천원 이상인지 검사
                     */
                    if (payCustomAmount.getText().toString().trim().length() > 3 && price < 3000) {
                        pointPrice.setText("결제 예상 금액 >>\n3,000원 이상 부터 결제 할 수 있습니다.");
                    } else if (payCustomAmount.getText().toString().trim().length() > 3 && price >= 3000) {

                        futurePoint = price;

                        int sum = nowPoint + futurePoint;
                        pointFuture.setText("결제 후 보유 포인트 >> " + sum);
                        pointPrice.setText("결제 예상 금액 >> " + price + "원");
                        payingPrice = Integer.parseInt(payCustomAmount.getText().toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*
         * 5,000원 충전 선택
         */
        payingSmall.setOnClickListener(v -> {

            payCustomAmount.clearFocus();       // EditText Focus 없애기
            payCustomAmount.setText("");

            payingSmall.setCardBackgroundColor(0x33303F9F);
            payingMid.setCardBackgroundColor(0xffffffff);
            payingBig.setCardBackgroundColor(0xffffffff);

            futurePoint = 5000;     // 포인트와 결제금액은 1:1
            payingPrice = 5000;     // 결제 금액

            int sum = nowPoint + futurePoint;

            pointNow.setText("현재 보유 포인트 >> " + nowPoint + "포인트");
            pointFuture.setText("결제 후 보유 포인트 >> " + sum + "포인트");
            pointPrice.setText("결제 예상 금액 >> 5,000원");


        });

        /*
         * 30,000원 충전 선택
         */
        payingMid.setOnClickListener(v1 -> {

            payCustomAmount.clearFocus();       // EditText Focus 없애기
            payCustomAmount.setText("");

            payingSmall.setCardBackgroundColor(0xffffffff);
            payingMid.setCardBackgroundColor(0x33303F9F);
            payingBig.setCardBackgroundColor(0xffffffff);

            futurePoint = 30000;    // 포인트와 결제금액은 1:1
            payingPrice = 30000;    // 결제 금액
            int sum = nowPoint + futurePoint;

            pointNow.setText("현재 보유 포인트 >> " + nowPoint + "포인트");
            pointFuture.setText("결제 후 보유 포인트 >> " + sum +"포인트");
            pointPrice.setText("결제 예상 금액 >> 30,000원");

        });

        /*
         * 50,000원 충전 선택
         */
        payingBig.setOnClickListener(v2 -> {

            payCustomAmount.clearFocus();       // EditText Focus 없애기
            payCustomAmount.setText("");

            payingSmall.setCardBackgroundColor(0xffffffff);
            payingMid.setCardBackgroundColor(0xffffffff);
            payingBig.setCardBackgroundColor(0x33303F9F);
            futurePoint = 50000;    // 포인트와 결제금액은 1:1
            payingPrice = 50000;    // 결제 금액
            int sum = nowPoint + futurePoint;

            pointNow.setText("현재 보유 포인트 >> " + nowPoint + "포인트");
            pointFuture.setText("결제 후 보유 포인트 >> " + sum + "포인트");
            pointPrice.setText("결제 예상 금액 >> 50,000원");
        });

        pointLetsPaying.setOnClickListener(v -> {

            SharedPreferenceUtil pref = new SharedPreferenceUtil(this);
            Intent intent = new Intent(this, KakaoPayBilling.class);
            intent.putExtra("amount", String.valueOf(payingPrice));
            intent.putExtra("buyer_name", pref.getSharedData("userId"));
            intent.putExtra("buyer_tel", pref.getSharedData("userTel"));
            intent.putExtra("buyer_email", pref.getSharedData("userEmail"));
            startActivity(intent);
        });
    }

    /**
     * 5000원 충전, 30000원 충전, 50000원 충전 버튼 중 하나를 선택하면 나머지 두개는 선택 해제 시켜야함
     */
    private void selectPayingIcon() {

        // TODO 가격 getText() > Intent KakaoPay Activity


        if (payCustomAmount.getText().toString().trim().length() != 0) {

        }


    }

    private void initView() {

        payCustomAmount = findViewById(R.id.payCustomAmount);
        payingSmall = findViewById(R.id.payingSmall);
        payingMid = findViewById(R.id.payingMid);
        payingBig = findViewById(R.id.payingBig);
        pointNow = findViewById(R.id.pointNow);
        pointFuture = findViewById(R.id.pointFuture);
        pointPrice = findViewById(R.id.pointPrice);
        pointLetsPaying = findViewById(R.id.pointLetsPaying);

    }
}

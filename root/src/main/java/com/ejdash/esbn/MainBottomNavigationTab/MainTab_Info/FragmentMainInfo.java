/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.bumptech.glide.Glide;
import com.ejdash.esbn.ERC20EthereumToken.TransToken;
import com.ejdash.esbn.MainBottomNavigationTab.Async.AsyncGetUserInfo;
import com.ejdash.esbn.MainBottomNavigationTab.MainTab_Info.clientReport.ClientReport;
import com.ejdash.esbn.Point.SelectPointAmount;
import com.ejdash.esbn.R;
import com.ejdash.esbn.utils.SharedPreferenceUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;

public class FragmentMainInfo extends Fragment {
    private static String TAG;
    BootstrapCircleThumbnail userInfoThumbnail;
    TextView userInfoId;        // 유저 아이디
    TextView userInfoEmail;     // 유저 이메일 주소
    TextView userInfoPoint;     // 포인트 보유 현황
    TextView userInfoPointLoading;     // 포인트 보유 현황 우측 로딩 텍스트뷰 (애니메이션으로 돌림)
    Button clientReportByChart; // 컨텐츠 이용현황으로 넘어가는 버튼
    Button clientSendingToken;  // 이더리움 토큰 관련 컨텐츠로 넘어가는 버튼
    Button clientPayForPoint;   // 포인트 충전 > 별풍선 개념의 포인트임
    String userPoint;
    LinearLayout userInfoPointLoadingLayout;
    private OnFragmentInteractionListener mListener;
    private Context context;
    private JSONObject json;
    private Handler mHandler;

    public FragmentMainInfo() {
        //생성자
    }

    public static FragmentMainInfo newInstance() {
        FragmentMainInfo fragment = new FragmentMainInfo();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_info, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();

        SharedPreferenceUtil pref = new SharedPreferenceUtil(context);
        userInfoId = view.findViewById(R.id.userInfoId);
        userInfoEmail = view.findViewById(R.id.userInfoEmail);
        userInfoId.setText(pref.getSharedData("userId"));
        userInfoEmail.setText(pref.getSharedData("userEmail"));

        userInfoThumbnail = view.findViewById(R.id.userInfoThumbnail);
        clientReportByChart = view.findViewById(R.id.clientReportByChart);
        clientSendingToken = view.findViewById(R.id.clientSendingToken);
        userInfoPoint = view.findViewById(R.id.userInfoPoint);
        clientPayForPoint = view.findViewById(R.id.clientPayForPoint);
        userInfoPointLoading = view.findViewById(R.id.userInfoPointLoading);
        userInfoPointLoadingLayout = view.findViewById(R.id.userInfoPointLoadingLayout);
        // TODO @var userInfoPoint DB 조회 후 setText()
        // TODO userInfoPoint 관련 (포인트 관련) DB Table 만들기


        getUserInfo();
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate); // 로딩 화살표 360도 무한 반복
        userInfoPointLoading.setAnimation(animation);

        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            userInfoPoint.setText("보유 마일리지 : " + userPoint);
            userInfoPointLoadingLayout.setVisibility(View.GONE);
        }, 2000);

        /*
            유저 썸네일
         */
        Glide.with(context).load(pref.getSharedData("userPhotoUrl")).into(userInfoThumbnail);
        userInfoThumbnail.setOnClickListener(v -> {
//                Toast.makeText(context, "체크", Toast.LENGTH_SHORT).show();
        });

        /*
            사용자 이용 통계로 이동
         */
        clientReportByChart.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClientReport.class);
            startActivity(intent);
        });

        /*
            이더리움 토큰 지갑 및 거래 액티비티로 이동
         */
        clientSendingToken.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransToken.class);
            startActivity(intent);
        });

        /*
            별풍선 개념의 포인트 충전 액티비티로 이동
         */
        clientPayForPoint.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectPointAmount.class);
            startActivity(intent);
        });


        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    /**
     * PHP를 이용해 사용자의 방송 및 방송 시청 이력을 카운팅한 정보를 가져온다
     * Key는 "AppName:UserID:favoritesports"
     */
    private void getUserInfo() {

        json = new JSONObject();
        SharedPreferenceUtil pref = new SharedPreferenceUtil(context);


        try {
            json = new AsyncGetUserInfo(pref.getSharedData("userId")).execute().get(10000, TimeUnit.MILLISECONDS);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(String.valueOf(json));
            Log.i(TAG, "getUserInfo > parser data > element > " + element.toString());

            userPoint = element.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("point").getAsString();

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(context, "통신 상태가 양호하지 않습니다! 네트워크 연결 상태를 확인해 주세요!", Toast.LENGTH_SHORT).show();
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.Async.AsycnRequestFriendList;
import com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.FriendsAdapter.MainFriendsFragmentAdapter;
import com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.FriendsAdapter.MainFriendsFragmentSet;
import com.ejdash.esbn.R;
import com.ejdash.esbn.utils.SharedPreferenceUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentMainFriends extends Fragment {
    public ArrayList<MainFriendsFragmentSet> friendsList = new ArrayList<>();
    SwipeRefreshLayout friendsListRefresh;
    RecyclerView friendsRecyclerView;
    MainFriendsFragmentAdapter friendsListAdapter;
    Handler mHandler;
    Unbinder unbinder;

    LinearLayout notHaveFriendsRootLayout;
    LinearLayout friendsListRoot;

    BootstrapButton addingFriendsButtonIntoNotHaveFriendsLayout;


    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton friendsVideoCalling, friendsAddingFriends, friendsMainFab;


    private OnFragmentInteractionListener mListener;
    private Context context;

    public FragmentMainFriends() {
        // Required empty public constructor
    }

    public static FragmentMainFriends newInstance() {
        FragmentMainFriends fragment = new FragmentMainFriends();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(logTAG, "onCreate : CALL ");
        if (getArguments() != null) {
            Log.i("프래그확인", "Friends CREATE~~~~ ");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("프래그확인", "Friends 들어왔당 ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_friends, container, false);
        context = view.getContext();



        fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_close);

        friendsMainFab = view.findViewById(R.id.friendsMainFab);
        friendsAddingFriends = view.findViewById(R.id.friendsAddingFriends);
        friendsVideoCalling = view.findViewById(R.id.friendsVideoCalling);

        friendsMainFab.setOnClickListener(v -> {
            anim();
        });
        friendsAddingFriends.setOnClickListener(v1 -> {
           Intent intent = new Intent(context, AddingFriends.class);
           startActivity(intent);
        });
        friendsVideoCalling.setOnClickListener(v2 -> {
            /*
                영상 통화쪽으로 앱 넘기기
             */
        });
        notHaveFriendsRootLayout = view.findViewById(R.id.notHaveFriendsRootLayout);
        friendsListRoot = view.findViewById(R.id.friendsListRoot);
        addingFriendsButtonIntoNotHaveFriendsLayout = view.findViewById(R.id.addingFriendsButtonIntoNotHaveFriendsLayout);

        friendsRecyclerView = view.findViewById(R.id.friendsList);
        LinearLayoutManager roomListLayoutSet = new LinearLayoutManager(context);
        friendsListAdapter = new MainFriendsFragmentAdapter(friendsList, context);

        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(roomListLayoutSet);
        friendsRecyclerView.setAdapter(friendsListAdapter);

        friendsListRefresh = view.findViewById(R.id.friendsRefresh);
        // 1000ms 후 RecyclerView 새로고침
        friendsListRefresh.setOnRefreshListener(this::FriendsListRefreshHandler);
        FriendsListRefreshHandler();



        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void anim() {

        if (isFabOpen) {
            friendsAddingFriends.startAnimation(fab_close);
            friendsVideoCalling.startAnimation(fab_close);
            friendsAddingFriends.setClickable(false);
            friendsVideoCalling.setClickable(false);
            isFabOpen = false;
        } else {
            friendsAddingFriends.startAnimation(fab_open);
            friendsVideoCalling.startAnimation(fab_open);
            friendsAddingFriends.setClickable(true);
            friendsVideoCalling.setClickable(true);
            isFabOpen = true;
        }
    }

    private void FriendsListRefreshHandler() {

        friendsList.clear();
        friendsListAdapter.notifyDataSetChanged();


     mHandler = new Handler();
        // run() end
        mHandler.postDelayed(() -> {
//            getFriendsList();
            friendsListAdapter.notifyDataSetChanged();
//                mProgressFriends.progressiveStop();
        }, 1000);        //Runnable() end

        getFriendsList();
    }

    private void getFriendsList() {

        SharedPreferenceUtil pref = new SharedPreferenceUtil(context);
        String userId = pref.getSharedData("userId");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new AsycnRequestFriendList(userId).execute().get(10000, TimeUnit.MILLISECONDS);

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(String.valueOf(jsonObject));

            if (jsonElement.getAsJsonObject().get("count").getAsInt() > 0) {
                notHaveFriendsRootLayout.setVisibility(View.GONE);
                friendsListRoot.setVisibility(View.VISIBLE);

                for (int i = 0; i < jsonElement.getAsJsonObject().get("result").getAsJsonArray().size(); i++) {
                    String targetId = jsonElement
                            .getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .get(i)
                            .getAsJsonObject()
                            .get("targetId")
                            .getAsString();

                    String sourceId = jsonElement
                            .getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .get(i)
                            .getAsJsonObject()
                            .get("sourceId")
                            .getAsString();


                    String targetIdThumbnail = jsonElement
                            .getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .get(i)
                            .getAsJsonObject()
                            .get("targetIdThumbnail")
                            .getAsString();


                    int acceptState = jsonElement
                            .getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .get(i)
                            .getAsJsonObject()
                            .get("acceptState")
                            .getAsInt();

                    int isFriend = jsonElement
                            .getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .get(i)
                            .getAsJsonObject()
                            .get("isFriend")
                            .getAsInt();


                    MainFriendsFragmentSet addItem = new MainFriendsFragmentSet(
                            targetId,
                            sourceId,
                            targetIdThumbnail,
                            acceptState,
                            isFriend
                    );

                    if(isFriend == 0 && acceptState == 0){
                        // 이미 친구이고 친구신청 제안한 쪽 아이디가 로그인한 사용자인 경우에만 추가
                        friendsList.add(addItem);
                    }

                    Log.i("몇번나오냐", "getFriendsList 확인확인");
                    Log.i("몇번나오냐", "값 확인 + targetId           " + targetId);
                    Log.i("몇번나오냐", "값 확인 + sourceId           " + sourceId);
                    Log.i("몇번나오냐", "값 확인 + targetIdThumbnail  " + targetIdThumbnail);
                    Log.i("몇번나오냐", "값 확인 + acceptState        " + acceptState);
                    Log.i("몇번나오냐", "값 확인 + isFriend           " + isFriend);
                    Log.i("몇번나오냐", "값 확인 + userId             " + userId);
                }   //   end for();
            } else {

                notHaveFriendsRootLayout.setVisibility(View.VISIBLE);
                friendsListRoot.setVisibility(View.GONE);

                addingFriendsButtonIntoNotHaveFriendsLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(context, AddingFriends.class);
                    startActivity(intent);
                });

            }

            friendsListRefresh.setRefreshing(false);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
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
        friendsRecyclerView = null;
        mHandler.removeCallbacksAndMessages(null);   // 죽는지 확인

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_VOD;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ejdash.esbn.MainBottomNavigationTab.MainTab_VOD.VODAdapter.MainVODFragmentAdapter;
import com.ejdash.esbn.MainBottomNavigationTab.MainTab_VOD.VODAdapter.MainVODFragmentSet;
import com.ejdash.esbn.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentMainVOD extends Fragment {
    final String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final Random rnd = new Random();
    public ArrayList<MainVODFragmentSet> VODList = new ArrayList<>();
    SwipeRefreshLayout VODListRefresh;
    RecyclerView VODRecyclerView;
    MainVODFragmentAdapter VODListAdapter;
    //    fr.castorflex.android.circularprogressbar.CircularProgressBar mProgressVOD;
    Handler mHandler;

    private OnFragmentInteractionListener mListener;
    private Context context;

    public FragmentMainVOD() {
        // Required empty public constructor
    }

    public static FragmentMainVOD newInstance() {
        FragmentMainVOD fragment = new FragmentMainVOD();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(logTAG, "onCreate : CALL ");
        if (getArguments() != null) {
            Log.i("프래그확인", "VOD CREATE~~~~ ");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("프래그확인", "VOD 들어왔당 ");
        Log.i("프래그확인", "VOD 들어왔당 ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_vod, container, false);
        context = view.getContext();


        Log.i("프래그확인", "VOD CREATE VIEW!@@!@@ ");

//        Log.d(logTAG, "onCreateView : CALL ");
        // Inflate the layout for this fragment

//        mProgressVOD = view.findViewById(R.id.mProgressVOD);
        VODRecyclerView = view.findViewById(R.id.VODList);
        // 방 리스트가 들어갈
        // Recycler View Initializing
//        LinearLayoutManager roomListLayoutSet = new LinearLayoutManager(context);
        StaggeredGridLayoutManager roomListLayoutSet = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        VODListAdapter = new MainVODFragmentAdapter(VODList, context);
        VODRecyclerView.setHasFixedSize(true);
        VODRecyclerView.setLayoutManager(roomListLayoutSet);
        VODRecyclerView.setAdapter(VODListAdapter);

        VODListRefresh = view.findViewById(R.id.VODRefresh);
        VODListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRoomList();
                vodListRefreshHandler();    // 1000ms 후 RecyclerView 새로고침
            }
        });
        getRoomList();
        vodListRefreshHandler();
        return view;
    }


    private void vodListRefreshHandler() {

        VODList.clear();
        VODListAdapter.notifyDataSetChanged();

//        mProgressVOD.setIndeterminateDrawable(new CircularProgressDrawable
//                .Builder(context, true)
//                .sweepSpeed(1f)
//                .strokeWidth(0.5f)
//                .build()
//        );

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                VODListAdapter.notifyDataSetChanged();
//                mProgressVOD.progressiveStop();
            }       // run() end
        }, 1000);        //Runnable() end

    }


    private void getRoomList() {
        VODList.clear();

        try {
            RequestBody body = new FormBody.Builder()
                    .add("Id", "id")
                    .build();

            Request requestGetVODList = new Request.Builder()
                    .url("http://222.122.203.55/kurento/vodList.php")
                    .post(body)
                    .build();

            OkHttpClient getVODListClient = new OkHttpClient();

            getVODListClient.newCall(requestGetVODList).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("flowCheck", "onFailure: OkHttp Failure : " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    // MediaServer 측 roomLists get 시작

                    String result = response.body().string();
                    Log.i("flowCheck", "onResponse: response VOD List > " + result);

                    JsonParser parser = null;
                    try {
                        parser = new JsonParser();
                        JsonElement element = parser.parse(result);

                        Log.i("인덱스 카운트", "element.getAsJsonObject().get(\"vodlist\").getAsJsonArray().size() >" + element.getAsJsonObject().get("vodlist").getAsJsonArray().size());
                        for (int i = 0; i < element.getAsJsonObject().get("vodlist").getAsJsonArray().size(); i++) {
                            Log.i("인덱스 카운트", "i> " + i);


                            String url_VODArray = element.getAsJsonObject().get("vodlist").getAsJsonArray().get(i).getAsJsonObject().get("url").getAsString();
                            String index_VODArray = element.getAsJsonObject().get("vodlist").getAsJsonArray().get(i).getAsJsonObject().get("index").getAsString();
                            String user_VODArray = element.getAsJsonObject().get("vodlist").getAsJsonArray().get(i).getAsJsonObject().get("user").getAsString();
                            String time_VODArray = element.getAsJsonObject().get("vodlist").getAsJsonArray().get(i).getAsJsonObject().get("time").getAsString();
                            String thumbnail_VODArray = element.getAsJsonObject().get("vodlist").getAsJsonArray().get(i).getAsJsonObject().get("thumbnailURL").getAsString();
                            Log.i("방확인", "url_VODArray : " + url_VODArray);
                            Log.i("방확인", "index_VODArray : " + index_VODArray);
                            Log.i("방확인", "user_VODArray : " + user_VODArray);
                            Log.i("방확인", "time_VODArray : " + time_VODArray);
                            Log.i("방확인", "time_VODArray : " + thumbnail_VODArray);

                            MainVODFragmentSet addItem = new MainVODFragmentSet(
                                    url_VODArray,
                                    index_VODArray,
                                    user_VODArray,
                                    time_VODArray,
                                    thumbnail_VODArray
                            );
                            VODList.add(addItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    // MediaServer 측 roomLists get 종료
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        VODListRefresh.setRefreshing(false);
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
        VODRecyclerView = null;
        mHandler.removeCallbacksAndMessages(null);   // 죽는지 확인
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

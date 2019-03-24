/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.Live;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.MainBottomNavigationTab.Live.LIVEAdapter.FragmentAdapter;
import com.ejdash.esbn.MainBottomNavigationTab.Live.LIVEAdapter.FragmentSet;
import com.ejdash.esbn.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 방송 리스트 불러오는 곳
 * > 진입 경로 - 로그인 > MainActivity 내 BottomNavigation 중 'Live'
 * RecyclerView (방송목록) 하나와 방송하기 버튼 하나가 초기 구성
 * <p>
 */

public class LiveFragment extends Fragment {

    private static final String TAG_GET_ROOM = "방목록확인";
    public static String logTAG = "FlowCheck";
    final String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final Random rnd = new Random();
    public ArrayList<FragmentSet> roomList = new ArrayList<>();
    SwipeRefreshLayout mainRoomRefresh;
    RecyclerView roomListRV;
    FragmentAdapter roomListAdapter;
    private int roomListRequest;
    /**
     * @var roomListRequestSessionId
     * @var roomListRequestHandleId
     */
    private OnFragmentInteractionListener mListener;
    private Context context;
    private Handler mHandler;


    public LiveFragment() {
        // Required empty public constructor
    }

    public static LiveFragment newInstance() {
        Log.d("프래그확인", "newInstance : CALL ");
        LiveFragment fragment = new LiveFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("프래그확인", "onAttach : CALL ");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        super.onCreate(savedInstanceState);
        Log.d(logTAG, "onCreate : CALL ");
        if (getArguments() != null) {
            Log.i("프래그확인", "Live CREATE~~~~ ");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_live, container, false);
        context = view.getContext();
        Log.i("프래그확인", "Live CREATE VIEW!@@!@@ ");
        // Inflate the layout for this fragment

        roomListRV = view.findViewById(R.id.roomList);
        // 방 리스트가 들어갈 Recycler View Initializing
        LinearLayoutManager roomListLayoutSet = new LinearLayoutManager(context);
        roomListAdapter = new FragmentAdapter(roomList, context);
        roomListRV.setHasFixedSize(true);
        roomListRV.setLayoutManager(roomListLayoutSet);
        roomListRV.setAdapter(roomListAdapter);

        mainRoomRefresh = view.findViewById(R.id.mainRoomRefresh);
        mainRoomRefresh.setOnRefreshListener(() -> {
            getRoomList();
            roomListAdapter.notifyDataSetChanged();
        });

        // okHttp 통해 방 목록 불러오는 메서드
        getRoomList();

        // onDetach에서 removeCallbackAndMessage 할 것
        // 로딩이 채 되기 전에 다른 탭으로 넘어가면 어떤 일이 발생 할 지 예측이 안됨

        return view;
    }


    private void demoRoomList() {
        String
                parseFilename,
                parseUserName,
                parseTime,
                liveThumbnailURL,
                roomName,
                roomDesc,
                roomLocationName,
                roomLocationAddress,
                watcher;

        parseFilename = "";
        roomListRequest = 1;
        parseFilename = "fileName";
        parseUserName = "saymynamecrycry";
        parseTime = "2018-08-12";
        liveThumbnailURL = "http://222.122.203.55/kurentoThumbnail/demoimg/dummy_broadcast_5.png";
        roomName = "하늘위에서!!! 조종사 미쳤따;;;";
        roomDesc = "조종사 죽탱이 꽂아버리고 싶습니다ㅠㅠ 라이브로 감상하시죠;;;";
        roomLocationName = "Australia";
        roomLocationAddress = "Great Barrier Reef";
        watcher = "97";

        Log.i("방확인", "roomListRequest: " + roomListRequest);
        FragmentSet addItem3 = new FragmentSet(
                roomListRequest,
                parseFilename,
                parseUserName,
                parseTime,
                liveThumbnailURL,
                roomName,
                roomDesc,
                roomLocationName,
                roomLocationAddress,
                watcher

        );
        roomList.add(addItem3);

        parseFilename = "";
        roomListRequest = 1;
        parseFilename = "fileName";
        parseUserName = "pikachu333";
        parseTime = "2018-08-12";
        liveThumbnailURL = "http://222.122.203.55/kurentoThumbnail/demoimg/dummy_broadcast_3.png";
        roomName = "★ 캐나다 파운더리웨이) 초고속 다운힐★ 카빙으로 가즈아~";
        roomDesc = "허벅지 터지것슴다... 바로 와서 구경해보시죠~";
        roomLocationName = "Canada Blackcomb Mountain";
        roomLocationAddress = "Whistler Blackcomb Ski Resort";
        watcher = "810";

        Log.i("방확인", "roomListRequest: " + roomListRequest);
        FragmentSet addItem2 = new FragmentSet(
                roomListRequest,
                parseFilename,
                parseUserName,
                parseTime,
                liveThumbnailURL,
                roomName,
                roomDesc,
                roomLocationName,
                roomLocationAddress,
                watcher
        );
        roomList.add(addItem2);

        parseFilename = "";
        roomListRequest = 1;
        parseFilename = "fileName";
        parseUserName = "yoyos";
        parseTime = "2018-08-12";
        liveThumbnailURL = "http://222.122.203.55/kurentoThumbnail/demoimg/dummy_broadcast_4.png";
        roomName = "파도타고 놀자ㅋㅋ, 쑤아리 질르어";
        roomDesc = "입 벌리고 파도타면 고기 들어옵니다";
        roomLocationName = "강원도 속초";
        roomLocationAddress = "속초 해수욕장";
        watcher = "39";

        Log.i("방확인", "roomListRequest: " + roomListRequest);
        FragmentSet addItem4 = new FragmentSet(
                roomListRequest,
                parseFilename,
                parseUserName,
                parseTime,
                liveThumbnailURL,
                roomName,
                roomDesc,
                roomLocationName,
                roomLocationAddress,
                watcher
        );
        roomList.add(addItem4);

        parseFilename = "";
        roomListRequest = 1;
        parseFilename = "fileName";
        parseUserName = "runaway1031";
        parseTime = "2018-08-12";
        liveThumbnailURL = "http://222.122.203.55/kurentoThumbnail/demoimg/dummy_broadcast_6.jpg";
        roomName = "생방송) 오늘도 미친듯이 농구만";
        roomDesc = "현장 직관와보세요 !! 강서구 체육관입니다 :)";
        roomLocationName = "서울시 강서구 화곡동";
        roomLocationAddress = "우장산 근린공원";
        watcher = "230";

        Log.i("방확인", "roomListRequest: " + roomListRequest);
        FragmentSet addItem = new FragmentSet(
                roomListRequest,
                parseFilename,
                parseUserName,
                parseTime,
                liveThumbnailURL,
                roomName,
                roomDesc,
                roomLocationName,
                roomLocationAddress,
                watcher
        );
        roomList.add(addItem);

    }



    /**
     * 서버로 부터 방 얻어오는 메서드
     * Client 'OkHttp' > Server 'Kurento > server.js > 방 목록 얻기 > Client 'OkHttp, return JSON'
     * 위와 같이 동작한다
     */
    private void getRoomList() {

        final MediaType JSON = MediaType.parse("application/json; charset=utf=8");
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", "list");
            Log.i("방확인", "CHECK JSON, get sessionID : " + jsonObject);


            RequestBody requestBodyGetSessionID = RequestBody.create(JSON, jsonObject.toString());
            Request requestGetSessionID = new Request.Builder()
                    .url("http://222.122.203.55:8080/")
                    .post(requestBodyGetSessionID)
                    .build();

            OkHttpClient getSessionIDRequest = new OkHttpClient();

            getSessionIDRequest.newCall(requestGetSessionID).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG_GET_ROOM, "onFailure: OkHttp Failure : " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // MediaServer 측 roomLists get 시작

                    String result = response.body().string();
                    Log.i(TAG_GET_ROOM, "onResponse: response session Create > " + result);

                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result);

                    roomList.clear();

                    for (int i = 0; i < element.getAsJsonArray().size(); i++) {

                        String
                                parseFilename,
                                parseUserName,
                                parseTime,
                                liveThumbnailURL,
                                roomName,
                                roomDesc,
                                roomLocationName,
                                roomLocationAddress,
                                watcher;

                        roomListRequest = element.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        parseFilename = element.getAsJsonArray().get(i).getAsJsonObject().get("filename").getAsString();
                        parseUserName = element.getAsJsonArray().get(i).getAsJsonObject().get("username").getAsString();
                        parseTime = element.getAsJsonArray().get(i).getAsJsonObject().get("time").getAsString();
                        liveThumbnailURL = element.getAsJsonArray().get(i).getAsJsonObject().get("liveThumbnailURL").getAsString();
                        roomName = element.getAsJsonArray().get(i).getAsJsonObject().get("roomName").getAsString();
                        roomDesc = element.getAsJsonArray().get(i).getAsJsonObject().get("roomDesc").getAsString();
                        roomLocationName = element.getAsJsonArray().get(i).getAsJsonObject().get("roomLocationName").getAsString();
                        roomLocationAddress = element.getAsJsonArray().get(i).getAsJsonObject().get("roomLocationAddress").getAsString();
                        watcher = element.getAsJsonArray().get(i).getAsJsonObject().get("watcher").getAsString();


                        Log.i("방확인", "roomListRequest: " + roomListRequest);
                        FragmentSet addItem = new FragmentSet(
                                roomListRequest,
                                parseFilename,
                                parseUserName,
                                parseTime,
                                liveThumbnailURL,
                                roomName,
                                roomDesc,
                                roomLocationName,
                                roomLocationAddress,
                                watcher
                        );
                        roomList.add(addItem);
                    }
                    demoRoomList(); // Demo용 방 목록 추가
                    // MediaServer 측 roomLists get 종료
                }
            });
            roomListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            roomListAdapter.notifyDataSetChanged();
            mainRoomRefresh.setRefreshing(false);
        }, 1000);


    }


    public void onButtonPressed(Uri uri) {
        Log.i("프래그확인", "onButtonPressed()");
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("프래그확인", "onStart : CALL ");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("프래그확인", "Live 리쓰엄 ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("프래그확인", "Live 디타치! ");

        Log.d(logTAG, "onDetach : CALL ");
        mListener = null;
        roomListRV = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    public String randomString(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(str.charAt(rnd.nextInt(str.length())));
        }
        return sb.toString();
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

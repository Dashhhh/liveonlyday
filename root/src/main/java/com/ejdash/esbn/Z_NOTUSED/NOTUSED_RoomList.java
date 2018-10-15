package com.ejdash.esbn.Z_NOTUSED;

import android.support.v7.app.AppCompatActivity;


/**
 * Kurento Android Library가 MVP Pattern으로 작성 되어 있음
 * 방송 하는 부분이므로 그대로 씀 > 기획적으로 디테일을 잡을 필요가 없음
 * 방송정보 관련해서 추가로 정보를 넣어야 하는 경우 (방송 제목이나 방송자의 위치정보 등으로 방 목록에 나타내어질 정보)
 * KurentoPresenterRTCClient.class 중 sendOfferSdp() 내 obj.put으로 서버에 정보 전달 할 수 있음
 * 서버측에서는 http 서버 중 8080 포트로 연결되는 function에서 해당 정보를 추가해 주어 시청자에게 방송자의 정보를
 * 전달 할 수 있음
 */

public class NOTUSED_RoomList extends AppCompatActivity {

/*
    private static String TAG_GET_ROOM = "방확인";
    public ArrayList<MainLiveFragmentSet> roomListArray = new ArrayList<>();
    SwipeRefreshLayout mainRoomRefresh;
    RecyclerView roomList;
    MainLiveFragmentAdapter roomListAdapter;
    private int roomListRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_roomlist);

        init();
        getRoomList();    // Rest로 방 목록 가져오기

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//                    Intent intent = new Intent(this, BroadCasterActivity_.class);
//                    startActivity(intent);
                }
        );
    }

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

                    roomListArray.clear();

                    for (int i = 0; i < element.getAsJsonArray().size(); i++) {
                        roomListRequest = element.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        Log.i("방확인", "roomListRequest: " + roomListRequest);

                        String parseFilename, parseUserName, parseTime,parseLiveThumbnailURL;
                        roomListRequest = element.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        parseFilename = element.getAsJsonArray().get(i).getAsJsonObject().get("filename").getAsString();
                        parseUserName = element.getAsJsonArray().get(i).getAsJsonObject().get("username").getAsString();
                        parseTime = element.getAsJsonArray().get(i).getAsJsonObject().get("time").getAsString();
                        parseTime = element.getAsJsonArray().get(i).getAsJsonObject().get("time").getAsString();
                        parseLiveThumbnailURL = element.getAsJsonArray().get(i).getAsJsonObject().get("liveThumbnailURL").getAsString();

                        Log.i("방확인", "roomListRequest: " + roomListRequest);
                        MainLiveFragmentSet addItem = new MainLiveFragmentSet(
                                roomListRequest,
                                parseFilename,
                                parseUserName,
                                parseTime,
                                parseLiveThumbnailURL

                        );
                        roomListArray.add(addItem);
                    }
                    // MediaServer 측 roomLists get 종료
                }
            });
            roomListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mainRoomRefresh.setRefreshing(false);
    }

    private void init() {

        roomList = findViewById(R.id.roomList);

        // 방 리스트가 들어갈
        // Recycler View Initializing
        LinearLayoutManager roomListLayoutSet = new LinearLayoutManager(this);
        roomListAdapter = new MainLiveFragmentAdapter(roomListArray, this);
        roomList.setHasFixedSize(true);
        roomList.setLayoutManager(roomListLayoutSet);
        roomList.setAdapter(roomListAdapter);

        mainRoomRefresh = findViewById(R.id.mainRoomRefresh);
        mainRoomRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRoomList();
                roomListAdapter.notifyDataSetChanged();
            }
        });


    }

    */
}

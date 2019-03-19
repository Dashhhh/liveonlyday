package com.ejdash.esbn.WebRTC.Live_Presenter;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;
import com.ejdash.esbn.WebRTC.Live_Presenter.ChatRoomAdapter_Presenter.ChatRoomAdapter_Presenter;
import com.ejdash.esbn.WebRTC.Live_Presenter.ChatRoomAdapter_Presenter.ChatRoomSet_Presenter;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.KurentoPresenterRTCClient;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.CandidateModel;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.response.ServerResponse;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.response.TypeResponse;
import com.ejdash.esbn.utils.SharedPreferenceUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nhancv.npermission.NPermission;
import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.DefaultSocketService;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionClient;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionParameters;
import com.nhancv.webrtcpeer.rtc_peer.SignalingEvents;
import com.nhancv.webrtcpeer.rtc_peer.SignalingParameters;
import com.nhancv.webrtcpeer.rtc_peer.StreamMode;
import com.nhancv.webrtcpeer.rtc_peer.config.DefaultConfig;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;
import com.nhancv.webrtcpeer.rtc_plugins.RTCAudioManager;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nhancao on 7/20/17.
 */

public class LivePresenter extends AppCompatActivity
        implements NPermission.OnPermissionResult,
        SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceSelectionListener {
    // 시작 WebRTC Broadcast 관련 변수
    public static final String STREAM_HOST = "wss://222.122.203.55:8443/one2many";
    private static final String TAG = LivePresenter.class.getSimpleName();
    private static final String HOST = "222.122.203.55";
    private static final int PORT = 5001;
    protected SurfaceViewRenderer vGLSurfaceViewCall;
    GoogleApiClient mGoogleApiClient;   // 구글 Place API 붙이기 위한 변수
    MediaPlayer donationMusic;  // 후원 시 재생음악
    Handler donationVisibilityGone; // 15초 후 도네이션 없애기 > postDelay 1500
    Handler donationTTSHandler; // 15초 후 도네이션 없애기 > postDelay 1500
    NPermission nPermission;
    EglBase rootEglBase;
    ProxyRenderer localProxyRenderer;
    boolean isGranted;
    SocketService socketService;
    PeerConnectionClient peerConnectionClient;
    KurentoPresenterRTCClient rtcClient;
    PeerConnectionParameters peerConnectionParameters;
    DefaultConfig defaultConfig;
    RTCAudioManager audioManager;
    SignalingParameters signalingParameters;
    BaseSocketCallback socketCallback;
    boolean iceConnected;
    Handler handler;
    String data;
    SocketChannel socketChannel;
    String msg;
    String presenterSessionNumber;
    AppCompatEditText broadcastSetId;
    TextToSpeech donationTTS;   // Donation String Message를 음성으로 읽어줌
    AppCompatEditText broadcastSetDescription;
    BootstrapButton broadcastSetLocation;
    BootstrapButton broadcastSettingSubmit;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;    // Google Place startActivityForResult() 에서 확인하는 변수
    @BindView(R.id.broadcastDonationLottie)
    LottieAnimationView broadcastDonationLottie;
    @BindView(R.id.broadcastDonationTitle)
    TextView broadcastDonationTitle;
    @BindView(R.id.broadcastDonationMessage)
    TextView broadcastDonationMessage;
    @BindView(R.id.broadcastingAudioControll)
    ImageView broadcastingAudioControll;
    private RecyclerView chatRoomRV;
    private Gson gson;
    private ChatRoomAdapter_Presenter chatRoomAdapterPresenter;
    private ArrayList<ChatRoomSet_Presenter> chatRoomSetPresenters = new ArrayList<>();
    private HashMap<String, String> userInfo;
    private EditText chatRoomSendMessageEditText;
    private TextView chatRoomSendMessageSendBtn;
    private String getBroadCastStartTime;
    private String presenterSessionName;
    private String sessionFileName;
    private String sessionWatcherCount; // 시청자 수 받아 오는 변수
    // Android View Update 위한 UiThread
    private Runnable showUpdate = new Runnable() {
        // 버로 부터 받은 메세지
        public void run() {
//            String receive = "Coming word : " + data;
            Log.i("채팅응답", "data > " + data);

            String responseData = data;
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseData);

            String userType = null;
            String presenterSessionId = null;
            String chatText = null;

            // 채팅내용 추가
            userType = element.getAsJsonObject().get("userType").getAsString();
            chatText = element.getAsJsonObject().get("chatText").getAsString();
            presenterSessionId = element.getAsJsonObject().get("messageSendUserId").getAsString();

            ChatRoomSet_Presenter setData = new ChatRoomSet_Presenter(
                    userType,
                    presenterSessionId,
                    chatText
            );
            chatRoomSetPresenters.add(setData);
            chatRoomAdapterPresenter.notifyDataSetChanged();
            chatRoomRV.scrollToPosition(chatRoomSetPresenters.size() - 1);

        }
    };
    private Thread checkUpdate = new Thread() {

        /**
         *  2018-09-23 오후 5:51 ej Comment
         *   - 채팅 관련 Thread
         *   -
         */
        public void run() {
            try {
                String line;
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ImageView broadcastSetting, broadcastSwitchCamera;
    private AwesomeTextView broadcastWatcher;
    private String locationAddress = "미 설정";        // 방송 설정 다이얼로그에서 Google Place를 통해 받아온 위치 정보를 갖고 있음
    private String locationName = "미 설정";      // 방송 설정 다이얼로그에서 Google Place를 통해 받아온 위치 정보를 갖고 있음
    private boolean vGLSurfaceViewCallMirrorFlag;
    private boolean peerConnectionAudioMuteState = false;   // 방송 음소거 설정 > true는 음소거 false는 소리 재생

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webrtc_presenter);
        ButterKnife.bind(this);

        donationTTSInitialize();
        broadCastInit();
        chatInit();
        broadcastSettingInit();

        if (Build.VERSION.SDK_INT < 23 || isGranted) {
            Log.i("여기냐", "퍼미션확인1");
            startCall();
        } else {
            Log.i("여기냐", "퍼미션확인2");
            nPermission.requestPermission(this, Manifest.permission.CAMERA);
        }
    }

    /**
    2018-09-23 오후 5:50 ej : 
    
    
    */
    protected void donationTTSInitialize() {
        donationTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                //사용할 언어를 설정
                int result = donationTTS.setLanguage(Locale.KOREA);
                //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(LivePresenter.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    donationTTS.setPitch(0.8f);        //음성 톤
                    donationTTS.setSpeechRate(0.9f);   //읽는 속도
                }
            }
        });
    }

    // 네티용 메서드 시작

    /**
     * 액티비티 좌측 상단 아이콘 눌렀을때 나오는 메서드
     * 방송제목, 설명, 위치를 설정 할 수 있다
     * php 말고 kurento server로 바로 보내서 java script로 모두 처리한다
     */
    private void broadcastSettingInit() {

        broadcastSwitchCamera = findViewById(R.id.broadcastSwitchCamera);
        broadcastSwitchCamera.setOnClickListener(v -> switchCamera());  // Camera 전면, 후면 변환
        broadcastWatcher = findViewById(R.id.broadcastWatcher);         // 시청자 수를 표시해 주는 뷰
        broadcastSetting = findViewById(R.id.broadcastSetting);         // 클릭 시 가장 먼저 다이얼로그 호출하고 방송제목과 방송설명을 설정 할 수 있다

        broadcastSetting.setOnClickListener(v -> {
            TypefaceProvider.registerDefaultIconSets();
            Dialog dialog = new Dialog(LivePresenter.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_broadcast_setting);

            WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            lp.dimAmount = 0.3f;
            dialog.show();
            dialog.takeKeyEvents(true);

            broadcastSetId = dialog.findViewById(R.id.broadcastSetId);                     // 방송 리스트 중 제목 부분
            broadcastSetDescription = dialog.findViewById(R.id.broadcastSetDescription);   // 방송 리스트 중 설명 부분
            broadcastSetLocation = dialog.findViewById(R.id.broadcastSetLocation);         // 방송 리스트 중 위치 부분, Google Place Autocomplete 사용
            broadcastSetLocation.setOnClickListener(v12 -> {
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setOnPlaceSelectedListener(this);

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(LivePresenter.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.i(TAG, "broadcastSettingInit Error " + e.toString());
                }
            });

            broadcastSettingSubmit = dialog.findViewById(R.id.broadcastSettingSubmit);
            broadcastSettingSubmit.setOnClickListener(v1 -> {

                int editTextNullCheckId = broadcastSetId.getText().toString().trim().length();
                int editTextNullCheckDescription = broadcastSetDescription.getText().toString().trim().length();

                if (editTextNullCheckDescription == 0 || editTextNullCheckId == 0) {
                    Toast.makeText(LivePresenter.this, "방송 제목 및 설명은 공란일 수 없습니다", Toast.LENGTH_SHORT).show();
                } else {

                    // 적용 위치 - kurento > server.js
                    final MediaType JSON = MediaType.parse("application/json; charset=utf=8");
                    final JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("id", "broadCastSetting");
                        jsonObject.put("sessionId", presenterSessionNumber);         // 일반 고유 숫자 (0, 1, 2, ... n)
                        jsonObject.put("roomName", broadcastSetId.getText().toString());            // 방 제목 String
                        jsonObject.put("roomDesc", broadcastSetDescription.getText().toString());   // 방 설명 String
                        jsonObject.put("roomLocationName", locationName);  // Google place Autocomplete 통해 받아온 값
                        jsonObject.put("roomLocationAddress", locationAddress);  // Google place Autocomplete 통해 받아온 값

                        Log.i("방송 내용 변경", "CHECK JSON, get presenterSessionNumber : " + jsonObject);

                        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
                        Request requestBuilder = new Request.Builder()
                                .url("http://222.122.203.55:8080/")
                                .post(requestBody)
                                .build();

                        OkHttpClient fixRoomInfo = new OkHttpClient();

                        fixRoomInfo.newCall(requestBuilder).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("방송 내용 변경", "onFailure: OkHttp Failure : " + e);
                                runOnUiThread(() -> {
                                    Toast.makeText(LivePresenter.this, "네트워크 연결을 확인하고 재 시도해 주세요!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                            }       // onFailure end

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                // 방송 내용 변경 성공적으로 반영 될 경우 호출
                                String result = response.body().string();
                                Log.i("방송 내용 변경", "onResponse: response session Create > " + result);
                                runOnUiThread(() -> {
                                    Toast.makeText(LivePresenter.this, "설정 되었습니다.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });

                                // 적용 위치 - redisZincr.php > redis-server
                                final MediaType JSON2 = MediaType.parse("application/json; charset=utf=8");
                                final JSONObject jsonObject2 = new JSONObject();

                                try {
                                    jsonObject2.put("sessionId", presenterSessionNumber);         // 일반 고유 숫자 (0, 1, 2, ... n)

                                    Log.i("방송 내용 변경", "CHECK JSON, get presenterSessionNumber : " + jsonObject2);

                                    RequestBody requestBody = RequestBody.create(JSON2, jsonObject2.toString());
                                    Request requestBuilder2 = new Request.Builder()
                                            .url("http://13.124.128.18/redisTest.php")
                                            .post(requestBody)
                                            .build();

                                    OkHttpClient setRedisZincr = new OkHttpClient();

                                    //  redis sorted set 관련 요청
                                    setRedisZincr.newCall(requestBuilder2).enqueue(new Callback() {


                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            String result = e.toString();
                                            Log.i("redis", "onResponse > response > " + result);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result = response.body().string();
                                            Log.i("redis", "onResponse > response > " + result);
                                        }
                                    }); // redis Zincr Request end
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }       // OkHttp onResponse end
                        });     // OkHttp newCall end
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }       // else end
            });     // setOnClickListener end
        });     // broadCastSetting Button OnClickListener end
    }       // broadcastSettingInit() end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("PLACE확인", "onActivityResult > requestCode is... " + requestCode);
        Log.i("PLACE확인", "onActivityResult > resultCode is... " + resultCode);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("PLACE확인", "Place: " + place.getName());

                Log.i(TAG, "onActivityResult: place.getName() is ...  " + place.getName());
                Log.i(TAG, "onActivityResult: place.getAddress() is ...  " + place.getAddress());
                Log.i(TAG, "onActivityResult: place.getAttributions() is ...  " + place.getAttributions());
                Log.i(TAG, "onActivityResult: place.getId() is ...  " + place.getId());
                Log.i(TAG, "onActivityResult: place.getLatLng() is ...  " + place.getLatLng());
                Log.i(TAG, "onActivityResult: place.getLocale() is ...  " + place.getLocale());
                Log.i(TAG, "onActivityResult: place.getPhoneNumber() is ...  " + place.getPhoneNumber());
                Log.i(TAG, "onActivityResult: place.getPlaceTypes() is ...  " + place.getPlaceTypes());
                Log.i(TAG, "onActivityResult: place.getPriceLevel() is ...  " + place.getPriceLevel());
                Log.i(TAG, "onActivityResult: place.getViewport() is ...  " + place.getViewport());
                Log.i(TAG, "onActivityResult: place.getRating() is ...  " + place.getRating());
                Log.i(TAG, "onActivityResult: place.getWebsiteUri() is ...  " + place.getWebsiteUri());

                locationName = (String) place.getName();
                locationAddress = (String) place.getAddress();

                broadcastSetLocation.setMarkdownText("{ty_pin} 위치 [ " + locationName + " ]\n{ty_pin} 주소 [ " + locationAddress + " ]");

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // 에러 핸들링 필요 할 경우 이 부분 수정
                Log.i("PLACE확인", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * 네티를 이용한 채팅을 초기화 하기 위한 메서드
     * 이 곳에서 초기 정보를 JSON으로 담아서 서버에 던지면 네티 내 클라이언트용 핸들러에 아래 정보를 담아서
     * 같은 방의 사용자를 찾고 방 내 사용자에게 채팅을 뿌림
     */
    private void chatInit() {

        chatRoomSendMessageEditText = findViewById(R.id.chatRoomSendMessageEditText);       // 채팅 텍스트
        chatRoomSendMessageSendBtn = findViewById(R.id.chatRoomSendMessageSendBtn);     // 채팅 전송 버튼

        chatRoomRV = findViewById(R.id.chatRoomRV);     // 채팅내용 나오는 RecyclerView
        /*
         *   Recycler View 초기화 시작
         */
        LinearLayoutManager lm = new LinearLayoutManager(this);
        chatRoomRV.setLayoutManager(lm);
        chatRoomRV.setHasFixedSize(true);
        chatRoomAdapterPresenter = new ChatRoomAdapter_Presenter(chatRoomSetPresenters, this);
        chatRoomRV.setAdapter(chatRoomAdapterPresenter);
        /*
         *   Recycler View 초기화 종료
         */

        /**
         * 아래 변수는 채팅방에 처음 들어갈 때 들고 갈 내용임
         */
        userInfo = new HashMap<>();
        Intent getIntent = getIntent();
        String tempRoomID = String.valueOf(getIntent.getIntExtra("roomID", 0));
        userInfo.put("roomID", tempRoomID);
//        userInfo.put("chatRoomID", getIntent.getStringExtra("chatRoomID"));               // 시청자 쪽에서 쓰이는 부분
//        userInfo.put("userSessionID", getIntent.getStringExtra("userSessionID"));         // 시청자 쪽에서 쓰이는 부분
//        userInfo.put("broadCastingTime", getIntent.getStringExtra("broadCastingTime"));   // 시청자 쪽에서 쓰이는 부분
        /*
         type = publisher > 방송자
              publisher는 방 이름을 제공함

         type = subscriber > 시청자
              subscriver는 방 이름을 들고 서버에서 해당 방에 해당하는 리스트로 접근 후 같은 방의 유저에게 채팅 뿌림
        */
        userInfo.put("type", "join");

        Log.i("채팅초기확인", "chatInit > userInfo " + userInfo.get("roomID"));                //
//        Log.i("채팅초기확인", "chatInit > chatRoomID " + userInfo.get("chatRoomID"));
//        Log.i("채팅초기확인", "chatInit > userSessionID " + userInfo.get("userSessionID"));
//        Log.i("채팅초기확인", "chatInit > broadCastingTime " + userInfo.get("broadCastingTime"));

        JSONObject chatInitMessage = new JSONObject();
        try {
            chatInitMessage.put("type", userInfo.get("type"));
            chatInitMessage.put("chatRoomID", userInfo.get("chatRoomID"));
            chatInitMessage.put("userSessionID", userInfo.get("userSessionID"));
            chatInitMessage.put("broadCastingTime", userInfo.get("broadCastingTime"));
            chatInitMessage.put("roomID", userInfo.get("roomID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "chatInit > chatInitMessage > \n" + chatInitMessage);
        chatHandler(chatInitMessage);

    }       // chatInit() end

    void receive() {
        /*
            // 서버로 부터 받은 메세지 - UI Update는 showUpdate에서 진행한다
        */
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                //서버가 비정상적으로 종료했을 경우 IOException 발생
                int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                Log.d("readByteCount", readByteCount + "");
                //서버가 정상적으로 Socket의 close()를 호출했을 경우
                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환
                // Charset 바꿀때는 receive()측 뿐만 아니라 SendmsgTask()측도 같은걸로 바꿔줘야 함
                // 캐릭터 인코딩이 서로 다를경우 문자열을 해석하는 첫 바이트의 위치가 서로 다를뿐만 아니라
                // 해석하는 코드도 제각각으로 글자가 무조건 터져나옴 '궭퉫占' 이따위로 나옴
                Charset charset = Charset.forName("UTF-8");
                data = charset.decode(byteBuffer).toString();
                Log.d("receive", "msg :" + data);
                handler.post(showUpdate);
            } catch (IOException e) {
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    /**
     * @param chatInitMessage - 아래와 같은 값으로 서버에 전달 됨.
     *                        {
     *                        "type":"join",
     *                        "roomID":"0"
     *                        }
     */
    private void chatHandler(JSONObject chatInitMessage) {
        handler = new Handler();
        new Thread(() -> {
            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
                socketChannel.connect(new InetSocketAddress(HOST, PORT));
            } catch (Exception ioe) {
                ioe.printStackTrace();

            }
            checkUpdate.start();
        }).start();

        /*
            채팅서버 입장 및 입장 알림 메세지 출력
         */
        handler.postDelayed(() -> {
            try {

                /*
                    * JSON 내부 값 상세 예제
                        {
                            "type": "message",
                            "presenterSessionNumber": "2",
                            "getBroadCastStartTime": "2018-09-05T09:31:23.646Z",
                            "presenterSessionName": "Dashhh",
                            "sessionFileName": "DLETWBaW8Bg1Rmb",
                            "chatText": "채팅 메세지 입니다"
                        }
                 */

                SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messageType", "joinBroadcastChatRoom");
                jsonObject.put("userType", "presenter");
                jsonObject.put("presenterSessionId", pref.getSharedData("userId"));
                jsonObject.put("chatText", "activity_token");
                jsonObject.put("isStreamer", true);
                jsonObject.put("messageSendUserId", pref.getSharedData("userId"));

                ChatRoomSet_Presenter chatRoomSetPresenter = new ChatRoomSet_Presenter(
                        "presenter",
                        "알림",
                        "채팅방에 입장했습니다!"
                );


                chatRoomSetPresenters.add(chatRoomSetPresenter);
                chatRoomAdapterPresenter.notifyDataSetChanged();
                // 채팅 내용이 추가 될 경우 가장 아래로 스크롤 됨
                chatRoomRV.scrollToPosition(chatRoomSetPresenters.size() - 1);

                /*
                    서버 입장 위한 초기 메세지 전달
                 */
                new SendmsgTask().execute(jsonObject.toString()).get(10000, TimeUnit.MILLISECONDS);

                Log.i(TAG, "SendmsgTask().execute(jsonObject.toString()) > jsonObject > " + jsonObject.toString());

            } catch (JSONException | TimeoutException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000);

        /*
            채팅 전송 Client (Streamer) -> Server
         */
        chatRoomSendMessageSendBtn.setOnClickListener(v -> {
            try {
                final String return_msg = chatRoomSendMessageEditText.getText().toString();
                if (!TextUtils.isEmpty(return_msg)) {

                    String getChatText = chatRoomSendMessageEditText.getText().toString();

                    Log.d(TAG, "chatHandler > chatRoomSendMessageSendBtn onClick() > chatInitMessage > " + chatInitMessage + "");

                    Log.i("채팅내용확인", "SendmsgTask > run > " + getChatText);
                    SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

                    ChatRoomSet_Presenter chatRoomSetPresenter = new ChatRoomSet_Presenter(
                            "presenter",
                            pref.getSharedData("userId"),
                            chatRoomSendMessageEditText.getText().toString()
                    );

                    chatRoomSetPresenters.add(chatRoomSetPresenter);

                    chatRoomAdapterPresenter.notifyDataSetChanged();
                    chatRoomRV.scrollToPosition(chatRoomSetPresenters.size() - 1);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("messageType", "broadcastChatMessage");
                    jsonObject.put("userType", "presenter");
                    jsonObject.put("presenterSessionId", pref.getSharedData("userId"));
                    jsonObject.put("chatText", chatRoomSendMessageEditText.getText().toString());
                    jsonObject.put("isStreamer", true);
                    jsonObject.put("messageSendUserId", pref.getSharedData("userId"));

                    chatRoomSendMessageEditText.setText("");

                    Log.i("채팅값확인", "chatHandler > messageType is... " + "broadcastChatMessage");
                    Log.i("채팅값확인", "chatHandler > userType is... " + "presenter");
                    Log.i("채팅값확인", "chatHandler > presenterSessionNumber is... " + presenterSessionNumber);
                    Log.i("채팅값확인", "chatHandler > getBroadCastStartTime is... " + getBroadCastStartTime);
                    Log.i("채팅값확인", "chatHandler > presenterSessionId is... " + presenterSessionName);
                    Log.i("채팅값확인", "chatHandler > presenterVODFilename is... " + sessionFileName);
                    Log.i("채팅값확인", "chatHandler > chatText is... " + chatRoomSendMessageEditText.getText().toString());
                    Log.i("채팅값확인", "chatHandler > isStreamer is... " + true);
                    Log.i("채팅값확인", "chatHandler > messageSendUserId is... " + presenterSessionName);

                    Log.i("채팅내용확인", "SendmsgTask().execute(jsonObject.toString()) > jsonObject > " + jsonObject.toString());

                    new SendmsgTask().execute(jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
//        startCall();

    }

    protected void broadCastInit() {

        Log.d("호출확인", "broadCastInit() called");

        vGLSurfaceViewCall = findViewById(R.id.vGLSurfaceViewCall_RemoveRX);
        nPermission = new NPermission(true);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //config peer
        localProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        vGLSurfaceViewCallMirrorFlag = true;
        localProxyRenderer.setTarget(vGLSurfaceViewCall);

        initPeerConfig();

        /*
            화면 꺼지지 않게 유지함
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                Log.i("여기냐", "퍼미션확인3");
                this.isGranted = isGranted;
                if (!isGranted) {
                    nPermission.requestPermission(this, Manifest.permission.CAMERA);
                    Log.i("여기냐", "퍼미션확인 camera");
                } else {
                    Log.i("여기냐", "퍼미션확인 recordAudio");
                    nPermission.requestPermission(this, Manifest.permission.RECORD_AUDIO);
                    startCall();

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        TypefaceProvider.registerDefaultIconSets();
        Dialog dialog = new Dialog(LivePresenter.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_broadcast_presenter_exit);

        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.dimAmount = 0.5f;
        dialog.show();
        dialog.takeKeyEvents(true);

        BootstrapButton broadcastExit = dialog.findViewById(R.id.dialogExitBroadcastConfirm);
        broadcastExit.setOnClickListener(v -> {
            dialog.dismiss();
            disconnect();
            finish();
        });
    }

    VideoCapturer createVideoCapturer() {
        Log.i("카메라확인", "호출 : createVideoCapturer");

        VideoCapturer videoCapturer;
        Log.i("카메라확인", "호출 : createVideoCapturer > useCamera2() > " + useCamera2());
        if (useCamera2()) {
            if (!captureToTexture()) {
                Log.i("카메라확인", "호출 : createVideoCapturer > !captureToTexture() ? > enter");
                return null;
            }
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
            Log.i("카메라확인", "호출 : createVideoCapturer > !captureToTexture() ? > " + captureToTexture());
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            Log.i("카메라확인", "호출 : createVideoCapturer > videoCapturer > return null");
            return null;
        }

        Log.i("카메라확인", "호출 : createVideoCapturer > videoCapturer > 정상 리턴");
        return videoCapturer;
    }

    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    public VideoRenderer.Callbacks getLocalProxyRenderer() {
        return localProxyRenderer;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getDefaultConfig().isUseCamera2();
    }

    private boolean captureToTexture() {
        return getDefaultConfig().isCaptureToTexture();
    }

    public VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {    //
        Log.i("카메라확인", "createCameraCapturer > enter ");

        final String[] deviceNames = enumerator.getDeviceNames();


        for (int i = 0; i < deviceNames.length; i++) {
            Log.i("카메라확인", "createCameraCapturer > deviceNames > " + deviceNames[i]);
        }

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            Log.i("카메라확인", "enumerator.isFrontFacing(deviceName) > boolean > " + enumerator.isFrontFacing(deviceName));
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    Log.i("카메라확인", "String deviceName : deviceNames > videoCapturer != null");
                    return videoCapturer;
                }
            }
        }


        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            Log.i("카메라확인", "createCameraCapturer > String deviceName > " + deviceName);
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        Log.i("카메라확인", "String deviceName : deviceNames > videoCapturer == null ");

        return null;
    }

    public void initPeerConfig() {

        SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

        socketService = new DefaultSocketService(getApplication());
        rtcClient = new KurentoPresenterRTCClient(socketService, pref.getSharedData("userId"));
        defaultConfig = new DefaultConfig();
        peerConnectionParameters = defaultConfig.createPeerConnectionParams(StreamMode.SEND_ONLY);
        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, this);


    }

    public void disconnect() {
        if (rtcClient != null) {
            rtcClient.close(presenterSessionNumber);
            rtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            runOnUiThread(() -> audioManager.stop());   // 메인스레드가 아니면 ERROR
            audioManager = null;
        }

        if (socketService != null) {
            socketService.close();
        }

        localProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }

        finish();

    }

    /**
     * WebRTC 연결 초기화
     * - 미디어 서버와 Socket 통신으로 데이터 주고 받음
     * - onMessage() switch()를 통해 Server Response 데이터 확인 할 수 있음
     */
    public void startCall() {

        if (rtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }

        // LivePresenter.class 에서 호출함

        rtcClient.connectToRoom(STREAM_HOST, new BaseSocketCallback() {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                super.onOpen(serverHandshake);

                SignalingParameters parameters = new SignalingParameters(
                        new LinkedList<PeerConnection.IceServer>() {
                            {
                                add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
                                add(new PeerConnection.IceServer("turn:222.122.203.55", "kurento", "kurento"));
                            }

//                        }, true, null, null, null, null, null);
                        }, true, null, null, null, null, null);
                onSignalConnected(parameters);
            }

            @Override
            public void onMessage(String serverResponse_) {
                super.onMessage(serverResponse_);
                Log.i("이거 뭐냐", "서버응답" + serverResponse_);
                try {
                    gson = new Gson();
                    ServerResponse serverResponse = gson.fromJson(serverResponse_, ServerResponse.class);
                    Log.i("응답 확인", "serverResponse > " + serverResponse);

                    switch (serverResponse.getIdRes()) {
                        case PRESENTER_RESPONSE:
                            if (serverResponse.getTypeRes() == TypeResponse.REJECTED) {

                                Log.d(TAG, "onMessage() called with: serverResponse_ = [" + serverResponse.getMessage() + "]");

                            } else {

                                // 이 곳의 serverResponse_ 를 통해 방송자의 세션 아이디가 넘어옴
                                // Gson을 이용해 직렬화 된 자료에서 세션 아이디를 따로 뽑는다.

                                SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                                        serverResponse.getSdpAnswer());
                                onRemoteDescription(sdp);

                                presenterSessionNumber = serverResponse.getSessionNumber();
                                presenterSessionName = serverResponse.getSessionUserName();
                                sessionFileName = serverResponse.getSessionFileName();
                                getBroadCastStartTime = serverResponse.getBroadcastStartTime();

                                Log.i("응답 확인", "presenterSessionNumber > " + presenterSessionNumber);
                                Log.i("응답 확인", "getBroadCastStartTime > " + getBroadCastStartTime);
                                Log.i("응답 확인", "presenterSessionName > " + presenterSessionName);
                                Log.i("응답 확인", "sessionFileName > " + sessionFileName);
                            }

                            break;

                        case ICE_CANDIDATE:
                            CandidateModel candidateModel = serverResponse.getCandidate();
                            onRemoteIceCandidate(
                                    new IceCandidate(candidateModel.getSdpMid(), candidateModel.getSdpMLineIndex(),
                                            candidateModel.getSdp()));
                            break;


                        case WATCHER_COUNT:

                            /*
                                시청자 수 주기적으로 피드백 받음
                                방송자와 시청자 상호간에 주기적으로 받아야 하는 정보가 있다면 이 곳을 통해서 함께 진행
                             */
                            Log.d("WATCHER_COUNT", "onMessage() called with: serverResponse_ = [" + serverResponse.getMessage() + "]");

//                                presenterSessionNumber = serverResponse.getSessionNumber();
                            sessionWatcherCount = serverResponse.getWatcherCount();
//                                Log.i("응답 확인", "presenterSessionNumber > " + presenterSessionNumber);
                            Log.i("응답 확인", "sessionFileName > " + sessionFileName);

                            runOnUiThread(() -> broadcastWatcher.setMarkdownText("현재 시청자 : " + sessionWatcherCount));

                            break;

                        case RECEIVE_POINT:
                            runOnUiThread(() -> {
                                String presenterId = serverResponse.getPresenterId();
                                String presenterSessionNumber = serverResponse.getPresenterSessionNumber();
                                String viewerId = serverResponse.getViewerId();
                                String viewerSessionNumber = serverResponse.getViewerSessionNumber();
                                String donationPoint = serverResponse.getDonationPoint();
                                String donationMessage = serverResponse.getDonationMessage();

                                donationMusic = MediaPlayer.create(LivePresenter.this, R.raw.donation_sound2);
                                donationMusic.setLooping(false);
                                donationMusic.start();

                                broadcastDonationLottie.setVisibility(View.VISIBLE);
                                broadcastDonationTitle.setVisibility(View.VISIBLE);
                                broadcastDonationMessage.setVisibility(View.VISIBLE);

                                broadcastDonationTitle.setText(viewerId + "님께서" + donationPoint + "원을 후원 하셨습니다.");
                                broadcastDonationMessage.setText(donationMessage);
                                broadcastDonationLottie.setAnimation("smiley_stack.json");
                                broadcastDonationLottie.setScale(1.5f);
                                broadcastDonationLottie.playAnimation();
                                broadcastDonationLottie.useHardwareAcceleration();
                                broadcastDonationLottie.setOnLongClickListener(v -> {
                                    donationTTS.stop();
                                    donationTTS.shutdown();
                                    donationMusic.stop();
                                    try {
                                        donationMusic.prepare();
                                    } catch (IllegalStateException | IOException e) {
                                        e.printStackTrace();
                                    }
                                    broadcastDonationLottie.cancelAnimation();
                                    broadcastDonationLottie.setVisibility(View.GONE);
                                    broadcastDonationTitle.setVisibility(View.GONE);
                                    broadcastDonationMessage.setVisibility(View.GONE);
                                    return false;
                                });

                                donationVisibilityGone = new Handler();
                                donationVisibilityGone.postDelayed(() -> {
                                    donationMusic.stop();
                                    donationTTS.speak(donationMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                                    try {
                                        donationMusic.prepare();
                                    } catch (IllegalStateException | IOException e) {
                                        e.printStackTrace();
                                    }

                                    while (donationTTS.isSpeaking()) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (!donationTTS.isSpeaking()) {
                                            donationTTS.stop();
                                            broadcastDonationLottie.cancelAnimation();
                                            broadcastDonationLottie.setVisibility(View.GONE);
                                            broadcastDonationTitle.setVisibility(View.GONE);
                                            broadcastDonationMessage.setVisibility(View.GONE);
                                        }
                                    }
                                }, 8000);   // Donation Music 음악 길이 만큼 설정해야 함 (음악 종료 > TTS 순으로 재생)
                            }); // end runOnUiThread()
                            break;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                super.onClose(i, s, b);
                Log.i(TAG, "onClose: " + "Socket closed > LivePresenter");
                runOnUiThread(() -> disconnect());


            }

            @Override
            public void onError(Exception e) {
                super.onError(e);

                Log.d(TAG, "onError() called with: e = [" + e.getMessage() + "]");
                runOnUiThread(() -> disconnect());
            }

        });

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = RTCAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        audioManager.start((audioDevice, availableAudioDevices) ->
                Log.d(TAG, "onAudioManagerDevicesChanged: " + availableAudioDevices + ", "
                        + "selected: " + audioDevice));

    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void callConnected() {
        if (peerConnectionClient == null) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, 1000);
    }

    public void switchCamera() {
        peerConnectionClient.switchCamera();

        if (vGLSurfaceViewCallMirrorFlag) {
            vGLSurfaceViewCall.setMirror(false);
            vGLSurfaceViewCallMirrorFlag = false;
        } else {
            vGLSurfaceViewCall.setMirror(true);
            vGLSurfaceViewCallMirrorFlag = true;
        }

    }

    @Override
    public void onSignalConnected(SignalingParameters params) {
        runOnUiThread(() -> {
            Log.i("카메라확인", "호출 : onSignalConnected");
            signalingParameters = params;
            VideoCapturer videoCapturer = null;
            Log.i("카메라확인", "onSignalConnected > peerConnectionParameters.videoCallEnabled > " + peerConnectionParameters.videoCallEnabled);
            if (peerConnectionParameters.videoCallEnabled) {
                videoCapturer = createVideoCapturer();
            }

            peerConnectionClient
                    .createPeerConnection(getEglBaseContext(), getLocalProxyRenderer(),
                            new ArrayList<>(), videoCapturer,
                            signalingParameters);

            if (signalingParameters.initiator) {
//                    if (isViewAttached()) getView().logAndToast("Creating OFFER...");
                // Create offer. Offer SDP will be sent to answering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createOffer();
            } else {
                if (params.offerSdp != null) {
                    peerConnectionClient.setRemoteDescription(params.offerSdp);
//                        if (isViewAttached()) getView().logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
                if (params.iceCandidates != null) {
                    // Add remote ICE candidates from room.
                    for (IceCandidate iceCandidate : params.iceCandidates) {
                        peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            /*
                방송을 떠날 때 Netty에 접속 되어 있는 Channel을 없애는 명령어 날림
             */

            SharedPreferenceUtil pref = new SharedPreferenceUtil(this);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageType", "quit");
            jsonObject.put("userType", "presenter");
            jsonObject.put("chatText", "not");
            jsonObject.put("presenterSessionId", presenterSessionName);
            jsonObject.put("isStreamer", false);
            jsonObject.put("messageSendUserId", pref.getSharedData("userId"));
            new SendmsgTask().execute(jsonObject.toString()).get(10000, TimeUnit.MILLISECONDS);
//            disconnect();
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteDescription(SessionDescription sdp) {
        Log.i("이거 뭐냐", "온 리모트 설명");
        if (peerConnectionClient == null) {
            Log.i("이거 뭐냐", "온 리모트 설명2");
            Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
            return;
        }
        peerConnectionClient.setRemoteDescription(sdp);
        if (!signalingParameters.initiator) {
            Log.i("이거 뭐냐", "온 리모트 설명3");

            Log.d(TAG, "onRemoteDescription() called with: sdp = [" + "Creating ANSWER..." + "]");
            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createAnswer();
        }
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {
        if (peerConnectionClient == null) {
            Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
            return;
        }
        peerConnectionClient.addRemoteIceCandidate(candidate);
    }

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] candidates) {
        if (peerConnectionClient == null) {
            Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
            return;
        }
        peerConnectionClient.removeRemoteIceCandidates(candidates);
    }

    @Override
    public void onChannelClose() {
        disconnect();
    }

    @Override
    public void onChannelError(String description) {
        Log.e(TAG, "onChannelError: " + description);
    }

    @Override
    public void onLocalDescription(SessionDescription sdp) {
        if (rtcClient != null) {
            if (signalingParameters.initiator) {
                rtcClient.sendOfferSdp(sdp);
            } else {
                rtcClient.sendAnswerSdp(sdp);
            }
        }
        if (peerConnectionParameters.videoMaxBitrate > 0) {
            Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
            peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
        }
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        if (rtcClient != null) {
            rtcClient.sendLocalIceCandidate(candidate);
        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        if (rtcClient != null) {
            rtcClient.sendLocalIceCandidateRemovals(candidates);
        }
    }

    @Override
    public void onIceConnected() {
        iceConnected = true;
        callConnected();
    }

    @Override
    public void onIceDisconnected() {
        Log.i(TAG, "onIceDisconnected: " + "ICE disconnected");
        iceConnected = false;
        disconnect();
    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] reports) {
        if (iceConnected) {
            Log.e(TAG, "run: " + reports);
        }
    }

    @Override
    public void onPeerConnectionError(String description) {
        Log.e(TAG, "onPeerConnectionError: " + description);
    }

    /**
     * @param connectionResult implement - GoogleApiClient.OnConnectionFailedListener 를 통해 오버라이드 된 메서드
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed > connectionResult.getErrorMessage() is...  " + connectionResult.getErrorMessage());
    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    protected void onStop() {
        if (donationTTS != null) {  // Donation TTS가 끝나기 전에 화면이 끝나는 경우의 예외처리
            donationTTS.stop();
            donationTTS.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
        super.onStop();

    }

    @OnClick(R.id.broadcastingAudioControll)
    public void onViewClicked() {

        if (peerConnectionAudioMuteState) { // true = WebRTC Audio Data 보내지 않음
            broadcastingAudioControll.setImageResource(R.drawable.ic_speaker_on_24dp);
            peerConnectionAudioMuteState = false;
            peerConnectionClient.setAudioEnabled(true);
        } else {
            broadcastingAudioControll.setImageResource(R.drawable.ic_speaker_black_24dp);
            peerConnectionAudioMuteState = true;
            peerConnectionClient.setAudioEnabled(false);
        }


    }

    private class SendmsgTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {

                Log.i("확인", "doInBackground: string.length" + strings.length);
                Log.i("확인", "doInBackground: string" + strings.length);
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("UTF-8")); // 서버로
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}


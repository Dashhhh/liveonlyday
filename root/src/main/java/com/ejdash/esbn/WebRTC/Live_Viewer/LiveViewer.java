package com.ejdash.esbn.WebRTC.Live_Viewer;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;
import com.ejdash.esbn.WebRTC.Live_Viewer.Async.AsyncGetMyPoint;
import com.ejdash.esbn.WebRTC.Live_Viewer.Async.AsyncSendPointToStreamer;
import com.ejdash.esbn.WebRTC.Live_Viewer.ChatRoomAdapter_Viewer.ChatRoomAdapter_Viewer;
import com.ejdash.esbn.WebRTC.Live_Viewer.ChatRoomAdapter_Viewer.ChatRoomSet_Viewer;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.KurentoViewerRTCClient;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.CandidateModel;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.response.ServerResponse;
import com.ejdash.esbn.WebRTC.WebRTC_PeerManager.kurento.models.response.TypeResponse;
import com.ejdash.esbn.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
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

/**
 * Created by nhancao on 7/20/17.
 */
public class LiveViewer extends AppCompatActivity
        implements SignalingEvents, PeerConnectionClient.PeerConnectionEvents {
    private static final String TAG = "LiveViewer";

    private static final String STREAM_HOST = "wss://222.122.203.55:8443/one2many";
    private static final String HOST = "222.122.203.55"; // Netty Chatting Server IP
    private static final int PORT = 5001;   // Netty Chatting Server Port
    protected SurfaceViewRenderer vGLSurfaceViewCall;
    MediaPlayer donationMusic;  // 후원 시 재생음악
    Handler stopDonatoinMusicAndStartTTS; // 15초 후 도네이션 없애기 > postDelay 1500
    Handler handler;
    String data;
    SocketChannel socketChannel;    // Netty Server 연결 위한 Socket
    String msg;
    String presenterSessionNumber;
    String viewerSessionNumber;
    EditText dialogGiftPointAmount, dialogGiftMessage;         // 시청자가 스트리머에게 포인트 보낼 때 > 보내고자 하는 포인트 양
    TextView dialogGiftMyPoint;         // 시청자가 스트리머에게 포인트 보낼 때 > 현재 포인트
    TextView dialogGiftMyPointFuture;           // 시청자가 스트리머에게 포인트 보낼 때 > 포인트 보낸 이후 보유양
    JSONObject getMyPoint;  // 서버를 통해 시청자가 보유한 포인트를 조회하고 리턴 받은 값을 저장함
    JSONObject afterSendPoint;  // 서버를 통해 시청자가 보유한 포인트를 조회하고 리턴 받은 값을 저장함
    @BindView(R.id.broadcastDonationLottie)
    LottieAnimationView broadcastDonationLottie;
    @BindView(R.id.broadcastDonationTitle)
    TextView broadcastDonationTitle;
    @BindView(R.id.broadcastDonationMessage)
    TextView broadcastDonationMessage;
    TextToSpeech donationTTS;   // Donation String Message를 음성으로 읽어줌
    private SocketService socketService;
    private Gson gson;
    private PeerConnectionClient peerConnectionClient;
    private KurentoViewerRTCClient rtcClient;
    private PeerConnectionParameters peerConnectionParameters;
    private DefaultConfig defaultConfig;
    private RTCAudioManager audioManager;
    private SignalingParameters signalingParameters;
    private boolean iceConnected;
    private int roomID;
    private EglBase rootEglBase;
    private ProxyRenderer remoteProxyRenderer;
    private String sessionWatcherCount; // 시청자 수 받아 오는 변수
    private RecyclerView chatRoomRV;
    private ChatRoomAdapter_Viewer chatRoomAdapterViewer;
    private ArrayList<ChatRoomSet_Viewer> chatRoomSetViewers = new ArrayList<>();
    private HashMap<String, String> userInfo;
    private EditText chatRoomSendMessageEditText;
    private TextView chatRoomSendMessageSendBtn;
    private String getBroadCastStartTime;   // 방송 시작시간
    private String presenterSessionId; // 방송자의 아이디
    private String presenterVODFilename; // 방송자의 VOD 파일명
    // Android View Update 위한 UiThread
    private Runnable showUpdate = new Runnable() {

        // 서버로 부터 받은 메세지
        public void run() {
//            String receive = "Coming word : " + data;

            Log.i("채팅응답", "data > " + data);

            String responseData = data;
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseData);


            String userType = null;
            String messageSendUserId = null;
            String chatText = null;

            // 채팅내용 추가
            userType = element.getAsJsonObject().get("userType").getAsString();
            messageSendUserId = element.getAsJsonObject().get("messageSendUserId").getAsString();
            chatText = element.getAsJsonObject().get("chatText").getAsString();

            ChatRoomSet_Viewer setData = new ChatRoomSet_Viewer(
                    userType,
                    messageSendUserId,
                    chatText
                    );
            chatRoomSetViewers.add(setData);
            chatRoomAdapterViewer.notifyDataSetChanged();
            chatRoomRV.scrollToPosition(chatRoomSetViewers.size() - 1);
        }
    };
    private Thread checkUpdate = new Thread() {
        public void run() {
            try {
                String line;
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private AwesomeTextView viewerWatcher;
    private int userPoint;   // 서버에서 조회 한 시청자의 보유 포인트 (파싱 완료 된 값)
    private String afterSendPointParseRetsplaurnData;   // 서버에서 조회 한 시청자의 보유 포인트 (파싱 완료 된 값)
    private BootstrapButton dialogGiftConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webrtc_viewer);
        ButterKnife.bind(this);


        donationTTSInitialize();
        initViewing();
        initChat();


    }

    private void donationTTSInitialize() {
        donationTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                //사용할 언어를 설정
                int result = donationTTS.setLanguage(Locale.KOREA);
                //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(LiveViewer.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    donationTTS.setPitch(0.8f);        //음성 톤
                    donationTTS.setSpeechRate(0.9f);   //읽는 속도
                }
            }
        });
    }

    private void initChat() {
        chatRoomSendMessageEditText = findViewById(R.id.chatRoomSendMessageEditText_Viewer);
        chatRoomSendMessageSendBtn = findViewById(R.id.chatRoomSendMessageSendBtn_Viewer);


        chatRoomRV = findViewById(R.id.chatRoomRV_Viewer);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        chatRoomRV.setLayoutManager(lm);
        chatRoomRV.setHasFixedSize(true);
        chatRoomAdapterViewer = new ChatRoomAdapter_Viewer(chatRoomSetViewers, this);
        chatRoomRV.setAdapter(chatRoomAdapterViewer);


        /**
         * 아래 변수는 채팅방에 처음 들어갈 때 들고 갈 내용
         */
        userInfo = new HashMap<>();
        Intent getIntent = getIntent();
        String tempRoomID = String.valueOf(getIntent.getIntExtra("roomID", 0));
        userInfo.put("roomID", tempRoomID);
        userInfo.put("chatRoomID", getIntent.getStringExtra("chatRoomID"));
        userInfo.put("userSessionID", getIntent.getStringExtra("userSessionID"));
        userInfo.put("broadCastingTime", getIntent.getStringExtra("broadCastingTime"));
        // type = publisher > 방송자
        //      publisher는 방 이름을 제공함
        // type = subscriber > 시청자
        //      subscriver는 방 이름을 들고 서버에서 해당 방에 해당하는 리스트로 접근 후 같은 방의 유저에게 채팅 뿌림
//        userInfo.put("type", "publisher");
        userInfo.put("type", "join");

        Log.i("채팅초기확인", "chatInit > userInfo " + userInfo.get("roomID"));                //
        Log.i("채팅초기확인", "chatInit > chatRoomID " + userInfo.get("chatRoomID"));
        Log.i("채팅초기확인", "chatInit > userSessionID " + userInfo.get("userSessionID"));
        Log.i("채팅초기확인", "chatInit > broadCastingTime " + userInfo.get("broadCastingTime"));

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

        Log.i("채팅초기확인", "chatInit > chatInitMessage > \n" + chatInitMessage);
        chatHandler(chatInitMessage);   // chatInitMessage 변수 더 이상 필요 없음 > 정보 서버에서 받아옴, 필요한건 시청자쪽임

    }

    /*
     *  Netty Server로 부터 받은 메세지 처리함
     */
    void receive() {
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
     * @param chatInitMessage - 처음 방 입장 했을때 네티 서버에 입장을 알리는 초기 메세지
     *                        이하 출력 상세(예제)
     *                        {
     *                        "type": "join", // "join" 이후에는 "message"라는 항목으로 채팅을 전달 함
     *                        "presenterSessionNumber": "1",                          // 현재 nodejs에 접속 되어 있는 세션 번호
     *                        "getBroadCastStartTime": "2018-09-06T05:06:00.379Z",    // 방송 시작 시간, "몇분 전"과 같은 형태로 출력 할 경우를 대비해 추가함
     *                        "presenterSessionId": "Dashhh",                         // 방송을 시청하는 사람의 ID
     *                        "presenterVODFilename": "2AUVMQXmIA6SB49",              // VOD File명, VOD파일로 안내해야 하는 경우가 있을 것에 대비해 추가함
     *                        "chatText": "activity_token"                            // 임의의 초기 메세지 실제 채팅 시 채팅 메세지가 실리는 곳. 임의의 메세지를 추가함
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


        handler.postDelayed(() -> {
            try {
                SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("messageType", "joinBroadcastChatRoom");
                jsonObject.put("userType", "viewer");
//                jsonObject.put("presenterSessionNumber", presenterSessionNumber);
//                jsonObject.put("getBroadCastStartTime", getBroadCastStartTime);
                jsonObject.put("presenterSessionId", presenterSessionId);
//                jsonObject.put("presenterVODFilename", presenterVODFilename);
                jsonObject.put("chatText", "activity_token");
                jsonObject.put("isStreamer", false);
                jsonObject.put("messageSendUserId", pref.getSharedData("userId"));


                ChatRoomSet_Viewer chatRoomSetViewer = new ChatRoomSet_Viewer(
                        "viewer",
                        "알림",
                        "채팅방에 입장했습니다!"
                );

                /*
                    최초 접속 시에는 채팅서버가 살았는지 확인하기 위해 Recycler View 갱신보다
                    메세지 전송을 먼저 함
                 */
                new SendmsgTask().execute(jsonObject.toString()).get(10000, TimeUnit.MILLISECONDS);
                Log.i("채팅확인", "SendmsgTask().execute(jsonObject.toString()) > jsonObject > " + jsonObject.toString());

                chatRoomSetViewers.add(chatRoomSetViewer);
                chatRoomAdapterViewer.notifyDataSetChanged();

            } catch (JSONException e) {
                runOnUiThread(() -> Toast.makeText(this, "Chatting Server에 연결 할 수 없습니다.", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }, 1000);

        /*
            Client -> Server로 Message 전송
         */
        chatRoomSendMessageSendBtn.setOnClickListener(v -> {
            try {
//                    final String return_msg = sendMsgEditText.getText().toString();
                final String return_msg = chatRoomSendMessageEditText.getText().toString();
                if (!TextUtils.isEmpty(return_msg)) {

                    String getChatText = chatRoomSendMessageEditText.getText().toString();

                    Log.d(TAG, "chatHandler > chatRoomSendMessageSendBtn onClick() > chatInitMessage > " + chatInitMessage + "");
                    Log.i("채팅내용확인", "SendmsgTask > run > " + getChatText);
                    SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

                    ChatRoomSet_Viewer chatRoomSetViewer = new ChatRoomSet_Viewer(
                            "viewer",
                            pref.getSharedData("userId"),
                            getChatText
                    );

                    chatRoomSetViewers.add(chatRoomSetViewer);
                    chatRoomAdapterViewer.notifyDataSetChanged();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("messageType", "broadcastChatMessage");
                    jsonObject.put("userType", "viewer");
                    jsonObject.put("presenterSessionId", presenterSessionId);
                    jsonObject.put("chatText", chatRoomSendMessageEditText.getText().toString());
                    jsonObject.put("isStreamer", false);
                    jsonObject.put("messageSendUserId", pref.getSharedData("userId"));


                    Log.i("채팅값확인", "chatHandler > messageType is... " + "broadcastChatMessage");
                    Log.i("채팅값확인", "chatHandler > userType is... " + "viewer");
                    Log.i("채팅값확인", "chatHandler > presenterSessionId is... " + presenterSessionId);
                    Log.i("채팅값확인", "chatHandler > chatText is... " + chatRoomSendMessageEditText.getText().toString());
                    Log.i("채팅값확인", "chatHandler > isStreamer is... " + false);
                    Log.i("채팅값확인", "chatHandler > messageSendUserId is... " + pref.getSharedData("userId"));


                    chatRoomSendMessageEditText.setText("");

                    Log.i("채팅보내기시청자", "값 확인 > presenterSessionNumber is..  " + presenterSessionNumber);

                    Log.i("채팅보내기시청자", "SendmsgTask().execute(jsonObject.toString()) > jsonObject > " + jsonObject.toString());


                    new SendmsgTask().execute(jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void donationDialogInitialize(Dialog dialog) {
        dialogGiftMyPoint = dialog.findViewById(R.id.dialogGiftMyPoint);   // 현재 보유 포인트
        dialogGiftMyPointFuture = dialog.findViewById(R.id.dialogGiftMyPointFuture);   // 후원 이후 보유 포인트
        dialogGiftPointAmount = dialog.findViewById(R.id.dialogGiftPointAmount);    // 상수화 되어서 필드 변수로 선언
        dialogGiftMessage = dialog.findViewById(R.id.dialogGiftMessage);    // 상수화 되어서 필드 변수로 선언
        dialogGiftConfirm = dialog.findViewById(R.id.dialogGiftConfirm);
    }

    /**
     * 시청자측 방송 화면에서 "포인트 보내기" 버튼 누를경우 포인트를 보낼 수 있는 다이얼로그 호출
     */
    @OnClick(R.id.viewerSendPoint)
    public void onViewClicked() {
        Dialog dialog = new Dialog(LiveViewer.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_broadcast_viewer_donation_tostreamer);

        ButterKnife.bind(dialog);
        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.dimAmount = 0.5f;
        dialog.show();
        dialog.takeKeyEvents(true);

        donationDialogInitialize(dialog);   // 도네이션 다이얼로그 내에 들어가는 View 초기화

        getMyPoint = new JSONObject();  // 서버로 부터 받은 결과 값 저장 (원본)
        SharedPreferenceUtil pref = new SharedPreferenceUtil(this);

        /*
            서버를 통해 시청자의 포인트를 조회
         */
        try {
            getMyPoint = new AsyncGetMyPoint(pref.getSharedData("userId")).execute().get(10000, TimeUnit.MILLISECONDS);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(String.valueOf(getMyPoint));
            Log.i(TAG, "getUserInfo > parser data > element > " + element.toString());

            userPoint = element.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("point").getAsInt();

            dialogGiftMyPoint.setText(userPoint + "");
            dialogGiftMyPointFuture.setText(userPoint + "");

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "통신 상태가 양호하지 않습니다! 네트워크 연결 상태를 확인해 주세요!", Toast.LENGTH_SHORT).show();
        }

        dialogGiftPointAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("dialogGiftPointAmount", "beforeTextChanged > dialogGiftPointAmount s is ..  >" + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("dialogGiftPointAmount", "onTextChanged > dialogGiftPointAmount s is ..  >" + s.toString());

                if (s.toString().trim().length() != 0) {
                    int pointValue = Integer.parseInt(s.toString());
                    if (pointValue > userPoint) {
                        dialogGiftConfirm.setEnabled(false);
                        Toast.makeText(LiveViewer.this, "보유량보다 많은 포인트를 보낼 수 없습니다.", Toast.LENGTH_SHORT).show();

                    } else if (pointValue <= userPoint) {

                        dialogGiftMyPointFuture.setText(userPoint - pointValue + "");
                        dialogGiftConfirm.setEnabled(true);

                    }
                } else {
                    /*
                        10000원 보유 중 일때 90000원을 입력하고 텍스트를 하나씩 지워 나가면 마지막 '9'로 인해 9991원 남게 보임
                        정상적으로 출력시키기 위해 아래 내용 추가
                     */
                    dialogGiftMyPointFuture.setText(userPoint + "");
                }
            }   // end onTextChange()

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("dialogGiftPointAmount", "onTextChanged > dialogGiftPointAmount s is ..  >" + s.toString());
            }

        }); // end addTextChangeListener()

        dialogGiftConfirm.setOnClickListener(v -> {
            try {

                if (dialogGiftMessage.getText().toString().trim().length() == 0) {
                    dialogGiftMessage.setText(" ");
                }

                afterSendPoint = new AsyncSendPointToStreamer(
                        presenterSessionId,
                        pref.getSharedData("userId"),
                        dialogGiftPointAmount.getText().toString(),
                        dialogGiftMessage.getText().toString())
                        .execute()
                        .get(10000, TimeUnit.MILLISECONDS);

/*      // 리턴 데이터 중요하지 않아서 주석 > 포인트를 보내고 난 이후에 되돌아온 데이터를 이용해서 파싱 후 할 행동이 없음
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(String.valueOf(getMyPoint));
                Log.i(TAG, "getUserInfo > parser data > element > " + element.toString());
                afterSendPointParseReturnData = element.getAsJsonObject().get("result").getAsString();
*/
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            rtcClient.sendPoint(
                    presenterSessionId,
                    presenterSessionNumber,
                    pref.getSharedData("userId"),
                    viewerSessionNumber,
                    dialogGiftPointAmount.getText().toString(),
                    dialogGiftMessage.getText().toString()
            );

            Log.i("후원확인", "enterActivity > dialogGiftPointAmount.getText().toString()  " + dialogGiftPointAmount.getText().toString());
            Log.i("후원확인", "enterActivity > dialogGiftMessage.getText().toString()  " + dialogGiftMessage.getText().toString());
//            runOnUiThread(this::lottieAnimation);

            dialog.dismiss();

        });

    }   // end enterActivity()

    protected void initViewing() {

        viewerWatcher = findViewById(R.id.viewerWatcher);   // 시청자 수 표시

        vGLSurfaceViewCall = findViewById(R.id.vGLSurfaceViewCall);
        Intent intent = getIntent();
        intent.getIntExtra("roomID", -1);
        setRoomID(intent.getIntExtra("roomID", -1));
        Log.i("방확인", "broadCastInit() > intent.getIntExtra > " + intent.getIntExtra("roomID", -1));

        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();


        //config peer
        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        remoteProxyRenderer.setTarget(vGLSurfaceViewCall);

        initPeerConfig();
        startCall();

        /*
            화면 꺼지지 않게 유지함
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public void initPeerConfig() {

        socketService = new DefaultSocketService(getApplication());

        rtcClient = new KurentoViewerRTCClient(socketService, roomID);
        defaultConfig = new DefaultConfig();
        peerConnectionParameters = defaultConfig.createPeerConnectionParams(StreamMode.RECV_ONLY);
        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, this);
    }

    public void disconnect() {
        if (rtcClient != null) {
            rtcClient.close(presenterSessionNumber, viewerSessionNumber);
            rtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            runOnUiThread(() -> audioManager.stop());
            audioManager = null;
        }

        if (socketService != null) {
            socketService.close();
        }

        disconnectSurface();

    }

    /**
     * 서버에서 오는 메세지를 감지한다
     */
    public void startCall() {
        if (rtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }


        rtcClient.connectToRoom(STREAM_HOST, new BaseSocketCallback() {
            /**
             * @param serverHandshake
             *          서버와 초기 연결을 맺기 위해 핸드쉐이크 진행
             *          java_websocket을 상속 받는다
             */
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                super.onOpen(serverHandshake);
                SignalingParameters parameters = new SignalingParameters(
                        new LinkedList<PeerConnection.IceServer>() {
                            {
                                add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
                                /*
                                    Server에 있는 coturn을 통해 turn server 구축
                                 */
                                add(new PeerConnection.IceServer("turn:222.122.203.55", "kurento", "kurento"));
                            }
                        }, true, null, null, null, null, null);
//                        }, true, null, null, null, null, null);
                onSignalConnected(parameters);
            }

            /**
             * @param serverResponse_
             *          서버에서 오는 메세지를 감지함
             *          따로 커스텀하려면 ServerResponse.class 내에 직렬화 아이템 추가 후 사용
             */
            @Override
            public void onMessage(String serverResponse_) {
                super.onMessage(serverResponse_);
                try {
                    gson = new Gson();
                    ServerResponse serverResponse = gson.fromJson(serverResponse_, ServerResponse.class);
                    Log.i("이거 뭐냐", "viewer 서버응답" + serverResponse_);
                    switch (serverResponse.getIdRes()) {
                        case VIEWER_RESPONSE:
                            if (serverResponse.getTypeRes() == TypeResponse.REJECTED) {
                                Log.i(TAG, "onMessage: serverResponse > REJECTED > " + serverResponse.getMessage());

                            } else {
                                SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                                        serverResponse.getSdpAnswer());
                                onRemoteDescription(sdp);

                                viewerSessionNumber = serverResponse.getViewerId();
                                presenterSessionNumber = serverResponse.getSessionNumber();
                                getBroadCastStartTime = serverResponse.getBroadcastStartTime();
                                presenterSessionId = serverResponse.getSessionUserName();
                                presenterVODFilename = serverResponse.getSessionFileName();

                                Log.i("응답 확인", "presenterSessionNumber > " + presenterSessionNumber);
                                Log.i("응답 확인", "getBroadCastStartTime > " + getBroadCastStartTime);
                                Log.i("응답 확인", "presenterSessionId > " + presenterSessionId);
                                Log.i("응답 확인", "presenterVODFilename > " + presenterVODFilename);
                            }


                            break;
                        case ICE_CANDIDATE:
                            CandidateModel candidateModel = serverResponse.getCandidate();
                            onRemoteIceCandidate(
                                    new IceCandidate(candidateModel.getSdpMid(), candidateModel.getSdpMLineIndex(),
                                            candidateModel.getSdp()));
                            break;
                        case STOP_COMMUNICATION:
                            runOnUiThread(() -> stopCommunication());
                            break;

                        case WATCHER_COUNT:

                            Log.d("WATCHER_COUNT", "onMessage() called with: serverResponse_ = [" + serverResponse.getMessage() + "]");

                            sessionWatcherCount = serverResponse.getWatcherCount();
                            Log.i("응답 확인", "presenterVODFilename > " + presenterVODFilename);

                            runOnUiThread(() -> viewerWatcher.setMarkdownText("현재 시청자 : " + sessionWatcherCount));

                            break;

                        case RECEIVE_POINT:
//                            runOnUiThread(() -> Toast.makeText(LivePresenter.this, "받았습니다", Toast.LENGTH_SHORT).show());
                            runOnUiThread(() -> {

                                String presenterId = serverResponse.getPresenterId();
                                String presenterSessionNumber = serverResponse.getPresenterSessionNumber();
                                String viewerId = serverResponse.getViewerId();
                                String viewerSessionNumber = serverResponse.getViewerSessionNumber();
                                String donationPoint = serverResponse.getDonationPoint();
                                String donationMessage = serverResponse.getDonationMessage();

                                donationMusic = MediaPlayer.create(LiveViewer.this, R.raw.donation_sound2);
                                donationMusic.setLooping(false);
                                donationMusic.start();

                                broadcastDonationLottie.setVisibility(View.VISIBLE);
                                broadcastDonationTitle.setVisibility(View.VISIBLE);
                                broadcastDonationMessage.setVisibility(View.VISIBLE);

                                broadcastDonationTitle.setText(viewerId + "님께서" + donationPoint + "원을 후원 하셨습니다.");
                                broadcastDonationMessage.setText(donationMessage);
                                broadcastDonationLottie.setAnimation("smiley_stack.json");
                                broadcastDonationLottie.playAnimation();
                                broadcastDonationLottie.useHardwareAcceleration();
                                broadcastDonationLottie.setScale(3f);
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

                                stopDonatoinMusicAndStartTTS = new Handler();
                                stopDonatoinMusicAndStartTTS.postDelayed(() -> {
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
                                }, 8000);
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

                Log.i("rtcClientViewer", "onClose: i" + i);
                Log.i("rtcClientViewer", "onClose: s" + s);
                Log.i("rtcClientViewer", "onClose: b" + b);

            }

            @Override
            public void onError(Exception e) {
                super.onError(e);

                e.printStackTrace();
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

    private void callConnected() {
        if (peerConnectionClient == null) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, 1000);
    }


    @Override
    public void onSignalConnected(SignalingParameters params) {
        runOnUiThread(() -> {
            signalingParameters = params;
            peerConnectionClient
                    .createPeerConnection(getEglBaseContext(), null,
                            getRemoteProxyRenderer(), null,
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
    public void onRemoteDescription(SessionDescription sdp) {
        if (peerConnectionClient == null) {
            Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
            return;
        }
        peerConnectionClient.setRemoteDescription(sdp);
        if (!signalingParameters.initiator) {
            Log.i(TAG, "onRemoteDescription > Creating ANSWER...");

            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createAnswer();
        }
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(candidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(candidates);
        });
    }

    @Override
    public void onChannelClose() {

        Log.i(TAG, "onChannelClose: " + "Remote end hung up; dropping PeerConnection");
    }

    @Override
    public void onChannelError(String description) {
        Log.e(TAG, "onChannelError: " + description);
    }

    @Override
    public void onLocalDescription(SessionDescription sdp) {
        runOnUiThread(() -> {
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
        });
    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {
        runOnUiThread(() -> {
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidateRemovals(candidates);
            }
        });
    }

    @Override
    public void onIceConnected() {
        runOnUiThread(() -> {
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(() -> {
            iceConnected = false;
            disconnect();
        });
    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    protected void onStop() {
        if (donationTTS != null) {
            donationTTS.stop();
            donationTTS.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
        super.onStop();
    }

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] reports) {
        runOnUiThread(() -> {
            if (iceConnected) {
                Log.e(TAG, "run: " + reports);
            }
        });
    }

    @Override
    public void onPeerConnectionError(String description) {
        Log.e(TAG, "onPeerConnectionError: " + description);
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void disconnectSurface() {
        remoteProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }

//        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        TypefaceProvider.registerDefaultIconSets();
        Dialog dialog = new Dialog(LiveViewer.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_broadcast_viewer_exit);

        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.dimAmount = 0.5f;
        dialog.show();
        dialog.takeKeyEvents(true);

        BootstrapButton broadcastExit = dialog.findViewById(R.id.dialogExitViewerConfirm);
        broadcastExit.setOnClickListener(v -> {
            dialog.dismiss();
            disconnect();
            finish();
        });

    }

    /**
     * 방송자가 나가는 경우 서버에서 "STOP_COMMUNICATION" 메세지가 옴
     */
    public void stopCommunication() {
        disconnect();
        finish();

    }

    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    public VideoRenderer.Callbacks getRemoteProxyRenderer() {
        return remoteProxyRenderer;
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
            jsonObject.put("userType", "viewer");
            jsonObject.put("chatText", "nott");
            jsonObject.put("presenterSessionId", presenterSessionId);
            jsonObject.put("isStreamer", false);
            jsonObject.put("messageSendUserId", pref.getSharedData("userId"));

            new SendmsgTask().execute(jsonObject.toString()).get(10000, TimeUnit.MILLISECONDS);
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SendmsgTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> {
                // 메세지를 보냈으므로 EditText 비워주기


            });
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {

                Log.i("확인", "doInBackground: string.lien" + strings.length);
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

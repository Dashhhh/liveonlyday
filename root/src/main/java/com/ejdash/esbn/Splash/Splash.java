package com.ejdash.esbn.Splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.ejdash.esbn.GoogleCloud.GoogleCloudExample;
import com.ejdash.esbn.MainBottomNavigationTab.MainActivity;
import com.ejdash.esbn.R;
import com.ejdash.esbn.Splash.Async.AsyncPhoneAuthRequest;
import com.ejdash.esbn.Splash.Async.AsyncRedisActiveUpCount;
import com.ejdash.esbn.Splash.Async.AsyncSignUpComplete;
import com.ejdash.esbn.Splash.TestActivity.KakaoPayTest;
import com.ejdash.esbn.Splash.TestActivity.LeftNavigationMenu;
import com.ejdash.esbn.utils.SharedPreferenceUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;


/**
 * 앱 실행 직후 나오는 화면
 * 이 곳에서의 행동은 아래와 같다
 * - 회원가입
 * > SMS 인증 번호를 통한 인증이 가능하다
 * - 구글 로그인
 * > Firebase를 통한 구글 로그인
 * > 시연을 위해 자동 로그인이 되지 않도록 firebase 초기화 작업 전 logout처리를 해 두었다
 * - 일반 로그인
 * > 일반적인 로그인
 * > 아이디와 비밀번호를 치고 들어감
 */
public class Splash extends AppCompatActivity {

    private final static String TAG = "checkLog";
    final int RC_SIGN_IN = 1001; // 로그인 확인여부 코드
    /**
     * Background에서 Video가 계속 돌 수 있게끔 만듦
     * 확장자는 *.wmv ( *.avi 혹은 *.mov 확장자 영상은 빌드에는 문제가 없으나 재생이 되지 않음)
     */
    VideoView videoview;
    /**
     * 회원가입 및 로그인은 Dialog를 통해 한 번에 해결한다.
     * 여러 과정 거칠 필요 없이 딱 필요한 정보만!
     * 필요 정보
     * <p>
     * Sign-Up
     * - E-mail
     * - Password
     * - Password Check
     * - Age (Rough)
     * - Favorite SportsZ
     * <p>
     * Sign-In
     * - E-Mail
     * - Password
     * <p>
     * Server IP : 13.124.184.18 (AWS)
     */
    BootstrapButton login;      // 일반 로그인 버튼
    BootstrapButton signUp;     // 회원가입 버튼
    //    com.google.android.gms.common.SignInButton googleLogin; //구글 로그인 버튼 > 구글에서 제공하는 로그인버튼
    BootstrapButton googleLogin; //구글 로그인 버튼
    CountDownTimer countDownTimer;
    int startCount = 60;  // 인증 유효시간, 60초부터 0초까지 다운카운트 됨.

    /*
        회원가입 다이얼로그 내 들어가는 뷰 초기화
        TextInputLayout 하위에 포함되고 Hint는 TextInputLayout에서 .setHint를 통해 선언
     */
    EditText
            signupId,
            signupPassword,
            signupPasswordCheck,
            signupPhoneNumber,
            signupAuthNumber,
            signupEmail;


    /*
        EditText 감싸고 있는 Layout > 뷰 선택 시 Hint가 위로 올라가는 효과를 내줌
        어떤 View를 싸고 있는지는 변수명 참고 할 것
            참고 > 변수 뒤에 'Wrapper'를 제외하면 감싸고 있는 EditText View의 변수명이 됨
     */
    TextInputLayout
            signupIdWrapper,
            signupPasswordWrapper,
            signupPasswordCheckWrapper,
            signupPhoneNumberWrapper,
            signupAuthNumberWrapper,
            signupEmailWrapper;

    /*
        회원가입 다이얼로그에 올라가는 버튼
     */
    BootstrapButton
            signupRequestAuth,  // 인증번호 요청 버튼
            signupAuthSubmit,   // 인증번호 제출 버튼
            signupComplete;     // 회원가입 완료 버튼
    BootstrapButton signup_test;    // Signup Dialog View
    boolean checkCompleteAuth = false;
    Button testNavi;
    Button payTest;
    JSONObject returnSignupComplete;

    /*
        예외 처리 확인 위한 변수 > View 내의 Text 숫자를 확인함 > 필드에 선언 하지 않으면 final로 선언 되므로 이 곳에 선언
            @var checkLengthSignupId
            @var checkLengthSignupPassword
            @var checkLengthSignupPasswordCheck
            @var checkLengthSignupPhoneNumber
            @var checkLengthSignupAuthNumber

     */
    int checkLengthSignupId;
    int checkLengthSignupPassword;
    int checkLengthSignupPasswordCheck;
    int checkLengthSignupPhoneNumber;
    int checkLengthSignupAuthNumber;
    @BindView(R.id.loginLabel_top)
    AwesomeTextView loginLabelTop;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient; //API 클라이언트
    private ImageView signinImage;
    private JSONObject returnAuthNumberFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        /*
            퍼미션체크
            가독성을 위해 Ted Permission Library 사용
                > Oreo Manifest 이슈로 인해 Ted Permission용 Activity 선언함 (AndroidManifest.xml - <activity> TedPermissionActivity 확인)
         */
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(Splash.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(Splash.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                )
                .check();

        activityViewInitialize();
        callLoginDialogAndMoveMainActivity();
        callSignUpDialogAndMoveMainActivity();
        googleLoginInit();  // 구글 로그인 초기화


    }   // end onCreate()

    private void callSignUpDialogAndMoveMainActivity() {

        signUp.setOnClickListener(v -> {

            getRedisKeyData();  // Redis 내 사용자 로그인 카운트 올림

            Dialog dialog = new Dialog(Splash.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_signup);
            WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            lp.dimAmount = 0.8f;
            dialog.show();


            signupIdWrapper = dialog.findViewById(R.id.signupIdWrapper);
            signupPasswordWrapper = dialog.findViewById(R.id.signupPasswordWrapper);
            signupPasswordCheckWrapper = dialog.findViewById(R.id.signupPasswordCheckWrapper);
            signupPhoneNumberWrapper = dialog.findViewById(R.id.signupPhoneNumberWrapper);
            signupAuthNumberWrapper = dialog.findViewById(R.id.signupAuthNumberWrapper);
            signupEmailWrapper = dialog.findViewById(R.id.signupEmailWrapper);


            signupIdWrapper.setHint("아이디");
            signupPasswordWrapper.setHint("비밀번호 입력");
            signupPasswordCheckWrapper.setHint("비밀번호 확인");
            signupPhoneNumberWrapper.setHint("인증번호 받을 휴대폰 번호");
            signupAuthNumberWrapper.setHint("인증번호 확인");
            signupEmailWrapper.setHint("E-mail");


            signupEmail = dialog.findViewById(R.id.signupEmail);
            signupId = dialog.findViewById(R.id.signupId);
            signupPassword = dialog.findViewById(R.id.signupPassword);
            signupPasswordCheck = dialog.findViewById(R.id.signupPasswordCheck);
            signupAuthNumber = dialog.findViewById(R.id.signupAuthNumber);
            signupPhoneNumber = dialog.findViewById(R.id.signupPhoneNumber);
//            signupPhoneNumber.setText("010-5101-8428");       // 시연을 위해 미리 입력
//            signupId.setText("manadra");                      // 시연을 위해 미리 입력
            signupRequestAuth = dialog.findViewById(R.id.signupRequestAuth);    // 인증번호 요청 버튼
            signupAuthSubmit = dialog.findViewById(R.id.signupAuthSubmit);  // 서버로부터 받은 인증번호 제출버튼
            signupComplete = dialog.findViewById(R.id.signup_complete);     // 회원가입 완료 버튼
            signupComplete.setEnabled(false);   // 인증번호 입력 완료 확인 후 활성화시킴

            /*
                // false = 버튼 배경색 나옴 > 사용자에게 누를 수 있는 상태라고 느끼게함
                // true  = 버튼 배경색 사라짐 > 사용자에게 누를 수 없는 상태라고 느끼게함
            */
            signupRequestAuth.setShowOutline(false);

            signupAuthSubmit.setEnabled(false); // 인증번호 요청 버튼 누를 시 활성화
            signupComplete.setEnabled(false);   // 서버에서 발급받은 인증번호 올바르게 입력 후 '제출버튼' 누를 시 활성화

            /*
                // 인증번호 요청 버튼
            */
            signupRequestAuth.setOnClickListener(v13 -> {

                checkLengthSignupId = signupId.getText().toString().trim().length();
                checkLengthSignupPassword = signupPassword.getText().toString().trim().length();
                checkLengthSignupPasswordCheck = signupPasswordCheck.getText().toString().trim().length();
                checkLengthSignupPhoneNumber = signupPhoneNumber.getText().toString().trim().length();
                checkLengthSignupAuthNumber = signupAuthNumber.getText().toString().trim().length();

                if (checkLengthSignupId == 0 || checkLengthSignupPhoneNumber == 0) {
                    Toast.makeText(Splash.this, "공란없이 모두 작성해 주세요", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        // Client > Server로 인증번호 요청
                        returnAuthNumberFromServer = new AsyncPhoneAuthRequest(Splash.this, signupPhoneNumber.getText().toString()).execute().get(10000, TimeUnit.MILLISECONDS);
                        Toast.makeText(Splash.this, signupPhoneNumber.getText().toString() + "(으)로 인증 번호를 보냈습니다.", Toast.LENGTH_SHORT).show();

                        signupRequestAuth.setEnabled(false);    // 버튼 비활성화 후 인증 유효시간 표시 (CountDownTimer)
                        signupRequestAuth.setShowOutline(true);

                        signupAuthSubmit.setEnabled(true); // 인증번호 요청 버튼 누를 시 활성화
                        signupAuthSubmit.setShowOutline(false); // 배경 색 사라지고 카운트다운 시 돌기 시작 함 > 누를 수 있는 상태로 보이게 함

                        setCountDownTimer();
                        countDownTimer.start();

                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                    }
                    Log.i("SMSCheck", "AsyncResult > returnAuthNumberFromServer > ");

                }

            });


            // 인증번호 요청 완료 후 인증번호 입력 및 대조확인 > 이후 회원가입 완료 버튼 활성화

            /*
                // 인증번호 입력 후 대조 확인
            */
            signupAuthSubmit.setOnClickListener(v1 -> {

                String checkAuthNumber = signupAuthNumber.getText().toString();

                try {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(String.valueOf(returnAuthNumberFromServer));

                    String type = null;

                    // 인증번호 파싱 시작 > 키 값 "auth"
                    type = element.getAsJsonObject().get("auth").getAsString();

                    if (!checkAuthNumber.equals(type)) {     // 인증번호 틀릴경우
                        Toast.makeText(Splash.this, "인증번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                    } else {                                // 인증번호 맞을경우
                        Toast.makeText(this, "인증 되었습니다. 회원가입을 완료해 주세요.", Toast.LENGTH_SHORT).show();
                        checkCompleteAuth = true;

                        signupAuthSubmit.setShowOutline(true);  // 누를 수 없는 상태로 보이게 함
                        signupAuthSubmit.setEnabled(false); // 인증 완료가 되었으므로 버튼 비 활성화

                        signupComplete.setShowOutline(false);   // 회원가입 완료 버튼 누를 수 있는 상태로 보이게 함
                        signupComplete.setEnabled(true);


                        try {
                            countDownTimer.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                            countDownTimer = null;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            /*
                // 회원가입 완료 메인화면으로 넘어감
            */
            signupComplete.setOnClickListener(v12 -> {

                /*
                 *  회원가입 완료 후 서버에 회원정보 INSERT
                 *  ProfileURL은 기본 이미지로 넣음 (사람 실루엣)
                 */
                try {
                    String id = signupId.getText().toString();
                    String password = signupPassword.getText().toString();
                    String phoneNumber = signupPhoneNumber.getText().toString();
                    String email = signupEmail.getText().toString();
                    String profileUrl = "http://222.122.203.55/android_test/default.png";

                    returnSignupComplete = new AsyncSignUpComplete(this, id, phoneNumber, password, email, profileUrl).execute().get(10000, TimeUnit.MILLISECONDS);
                    Log.i(TAG, "signupReturn > " + returnSignupComplete);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }

                if (checkCompleteAuth) {
                    signupComplete.setEnabled(true);    // 인증번호가 맞으므로 회원가입 버튼 활성화

                    SharedPreferenceUtil pref = new SharedPreferenceUtil(Splash.this);
                    pref.setSharedData("userId", signupId.getText().toString());
                    pref.setSharedData("userEmail", signupEmail.getText().toString());
                    pref.setSharedData("userTel", signupPhoneNumber.getText().toString());
                    pref.setSharedData("userPhotoUrl", "http://222.122.203.55/android_test/default.png");

                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                }

                dialog.dismiss();
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);

            });

        });

    }

    /*
     *  로그인 과정이 다이얼로그를 통해 진행 됨
     *  그 과정에서 필요한 다이얼로그 호출 및 로그인 후 메인 액티비티로의 이동까지 함
     */
    private void callLoginDialogAndMoveMainActivity() {

        login.setOnClickListener(v -> {
            getRedisKeyData();
//                Dialog_blur dial = new Dialog_blur(Splash.this);
//                dial.callLoginDialog("test");

            Dialog dialog = new Dialog(Splash.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_signin);

            signinImage = dialog.findViewById(R.id.signinImage);
            Glide.with(Splash.this).load(R.drawable.dialog_3).thumbnail(0.5f).into(signinImage);
            WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            lp.dimAmount = 0.7f;
            dialog.show();
            dialog.takeKeyEvents(true);

            EditText loginId = dialog.findViewById(R.id.loginId);
            loginId.setText(R.string.loginid_temp);    // 예시 : teamnova00

            BootstrapButton loginButton = dialog.findViewById(R.id.login_complete);
            loginButton.setOnClickListener(v15 -> {

                SharedPreferenceUtil pref = new SharedPreferenceUtil(Splash.this);
                pref.setSharedData("userId", loginId.getText().toString());
                pref.setSharedData("userEmail", "");
                pref.setSharedData("userPhotoUrl", "http://222.122.203.55/android_test/default.png");

                dialog.dismiss();
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            });

        });

    }

    /*
     *  View 초기화 (Butter Knife 사용 전)
     */
    private void activityViewInitialize() {

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf.ttf");
        loginLabelTop.setTypeface(tf);

        testNavi = findViewById(R.id.testNavi);
        testNavi.setOnClickListener(v -> {

            Intent intent = new Intent(Splash.this, LeftNavigationMenu.class);
            startActivity(intent);

        });
        payTest = findViewById(R.id.payTest);
        payTest.setOnClickListener(v -> {

            Intent intent = new Intent(Splash.this, KakaoPayTest.class);
            startActivity(intent);

        });
        videoview = findViewById(R.id.splashVideo);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);

    }


    /*
     *  SMS 인증 과정에서 쓰이는 메서드. 제한시간 60초를 카운팅하며 시간이 지나면 다시 인증하게 유도함
     */
    public void setCountDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
//                signupRequestAuth.setText(String.valueOf("인증유효시간 : "+startCount + "초 안에 완료해주세요"));
                signupRequestAuth.setText(String.valueOf(startCount));
                startCount--;
            }

            public void onFinish() {
                startCount = 58;
                Toast.makeText(Splash.this, "유효시간이 경과되었습니다. 처음부터 다시해주세요", Toast.LENGTH_SHORT).show();

                signupRequestAuth.setEnabled(true);
                signupRequestAuth.setShowOutline(false);
                signupRequestAuth.setText("요청");

                signupComplete.setEnabled(false);
                signupComplete.setShowOutline(true);

                signupAuthSubmit.setEnabled(false);
                signupAuthSubmit.setShowOutline(true);
            }

        };
    }

    /**
     * 파이어베이스를 통한 구글 로그인 진행
     */
    private void googleLoginInit() {

        FirebaseAuth.getInstance().signOut();   // 시연을 위해 기존 로그인 이력을 삭제해서 자동 로그인을 막음
        mAuth = FirebaseAuth.getInstance(); // 인스턴스 생성


        /*
         * 구글 파이어베이스 토큰
         * ref ) https://console.firebase.google.com/?hl=ko
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("172024408770-4ca5ffv32r2ko9ku8jfaq09g4uv81ld5.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleLogin = findViewById(R.id.googleLogin);

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // 구글에 이 클라이언트로 로그인한 기록이 있다면 로그인 및 파이어베이스 인증 진행
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // 구글 로그인 성공, 파이어베이스 인증 시작
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d(TAG, "이름 =" + account.getDisplayName());
                Log.d(TAG, "이메일=" + account.getEmail());
                Log.d(TAG, "getId()=" + account.getId());
                Log.d(TAG, "getAccount()=" + account.getAccount());
                Log.d(TAG, "getPhotoUrl()=" + account.getPhotoUrl());
                Log.d(TAG, "getIdToken()=" + account.getIdToken());
                Log.d(TAG, "toJson()=" + account.toJson());

                saveUserInfo(account.getDisplayName(), account.getEmail(), account.getPhotoUrl());

                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // 구글로그인 실패 했을 경우 or 다른 onActivityResult 사용 필요 시 작성
            }
        }
    }

    /**
     * @param id       - 'yoyos' 와 같은 아이디
     * @param email    - 'devil1302@gmail.com'과 같은 메일주소
     * @param photoUrl - User Profile Thumbnail 주소
     */
    private void saveUserInfo(String id, String email, Uri photoUrl) {
        SharedPreferenceUtil pref = new SharedPreferenceUtil(this);
        pref.setSharedData("userId", id);
        pref.setSharedData("userEmail", email);
        pref.setSharedData("userPhotoUrl", String.valueOf(photoUrl));
    }

    public void onStart() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { // 만약 로그인이 되어있으면 다음 액티비티 실행
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 파이어베이스를 이용해 실제 계정으로 로그인 진행
     * 파이어베이스 데이터베이스 내 접근설정 아래와 같이 해둠
     * - 익명접근
     * - 구글계정 접근
     *
     * @param acct - 로그인한 계정에 관련 된 정보를 담고 있음
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // end onComplete()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d(TAG, "signInWithCredential:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Splash.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }   // end firebaseAuthWithGoogle()

    /**
     * Redis 내 사용자 로그인 카운트 올림
     */
    public void getRedisKeyData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new AsyncRedisActiveUpCount(this).execute().get(10000, TimeUnit.MILLISECONDS);
            Log.i("redisCheck", "getRedisKeyData > json > " + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // end getRedisKeyData()


    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test2);
//        Uri uri = Uri.parse("http://" + "222.122.203.55" + "/" + "Wildlife.wmv");
//        Uri uri = Uri.parse("http://" + "222.122.203.55" + "/" + "test3.wmv");

        videoview.setVideoURI(uri);
        videoview.setOnPreparedListener(mp -> {
            /**
             * Background 에서 돌고 있는 비디오를 무한 재생시킨다
             */
            mp.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            videoview.start();
        });
    }

}   // end Splash.Class

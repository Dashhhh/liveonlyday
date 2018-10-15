package computician.janusclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.ArrayList;
import java.util.List;

import computician.janusclient.util.SystemUiHider;

public class JanusActivity extends Activity {
    private static final boolean AUTO_HIDE = true;

    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender;
    private List<VideoRenderer.Callbacks> remoteRender;
    private VideoRenderer.Callbacks remoteRenderer1;
    private VideoRenderer.Callbacks remoteRenderer2;
    private VideoRenderer.Callbacks remoteRenderer3;
    private VideoRenderer.Callbacks remoteRenderer4;
    private VideoRenderer.Callbacks remoteRenderer5;
//    private VideoRenderer.Callbacks remoteRender;
    private EchoTest echoTest;
    private VideoRoomTest videoRoomTest;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private class MyInit implements Runnable {

        public void run() {
            init();
        }

        private void init() {
            try {

                remoteRender = new ArrayList<>();
                remoteRender.add(remoteRenderer1);
                remoteRender.add(remoteRenderer2);
                remoteRender.add(remoteRenderer3);
                remoteRender.add(remoteRenderer4);
                remoteRender.add(remoteRenderer5);

                EGLContext con = VideoRendererGui.getEGLContext();
                videoRoomTest = new VideoRoomTest(localRender, remoteRender);
                videoRoomTest.initializeMediaContext(JanusActivity.this, true, true, true, con);
                videoRoomTest.Start();

            } catch (Exception ex) {
                Log.e("computician.janusclient", ex.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_janus);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vsv = (GLSurfaceView) findViewById(R.id.glview);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new MyInit());

        remoteRenderer1 = VideoRendererGui.create(0, 20, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
        remoteRenderer2 = VideoRendererGui.create(20, 0, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
        remoteRenderer3 = VideoRendererGui.create(0, 20, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
        remoteRenderer4 = VideoRendererGui.create(20, 0, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        remoteRenderer5 = VideoRendererGui.create(0, 20, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        localRender     = VideoRendererGui.create(20, 0, 30, 30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);


    }
}

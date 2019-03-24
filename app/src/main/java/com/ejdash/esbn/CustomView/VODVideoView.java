package com.ejdash.esbn.CustomView;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import static org.webrtc.ContextUtils.getApplicationContext;


public class VODVideoView extends VideoView {

    public VODVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Display dis = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        setMeasuredDimension(dis.getWidth(), dis.getHeight());

    }

    @Override
    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
        super.setOnInfoListener(l);
        new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        // Progress Diaglog 출력
                        Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        // Progress Dialog 삭제
                        Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        };


    }
}
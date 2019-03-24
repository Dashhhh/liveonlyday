/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.Live.LIVEAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.ejdash.esbn.R;
import com.ejdash.esbn.WebRTC.LiveViewer.LiveViewer;

import java.util.ArrayList;

public class FragmentAdapter extends RecyclerView.Adapter<FragmentHolder> {

    private static final String TAG = "Main Room List";
    ArrayList<FragmentSet> data = new ArrayList<>();
    Context mContext;

    public FragmentAdapter(ArrayList<FragmentSet> data, Context mContext) {
        this.mContext = mContext;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }

    }

    @NonNull
    @Override
    public FragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypefaceProvider.registerDefaultIconSets();
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_room_list, parent, false);
        return new FragmentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FragmentHolder holder, int position) {
        final int i = position;

        FragmentSet getData = data.get(position);
        holder.roomAdapUserName.setMarkdownText("{ty_camera_outline} " + getData.parseUserName + "님의 방송국");
//        holder.roomAdapTime.setMarkdownText("{md_room} " + getData.parseTime);
        holder.roomAdapTime.setMarkdownText("{ty_times_outline} " + "지금 방송 중");
        holder.roomAdapTime.setVisibility(View.GONE);

        holder.roomAdapRoomTitle.setMarkdownText(getData.roomName);
        holder.roomAdapRoomDescription.setMarkdownText(getData.roomDesc);
        holder.roomAdapLocationName.setMarkdownText("{fa_map_signs} " + getData.roomLocationName);
        holder.roomAdapLocationAddress.setMarkdownText("{fa_map_pin} " + getData.roomLocationAddress);
        holder.roomAdapWatcher.setMarkdownText("시청자수 > " + getData.roomWatcher);

        Log.i(TAG, "holder.liveParticipants.setText(getData.participants) > " + getData.roomID);

        holder.roomAdapCard.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, LiveViewer.class);
            intent.putExtra("roomID", data.get(i).roomID);
            intent.putExtra("chatRoomID", data.get(i).parseFilename);  // 녹화 파일명으로 채팅방 이름 설정
            intent.putExtra("userSessionID", data.get(i).parseUserName);
            intent.putExtra("broadCastingTime", data.get(i).parseTime);

            Log.i("ejCheck", "Adapter Room Id : "+ data.get(i).roomID);
            Log.i("ejCheck", "Adapter Room parseFilename : "+ data.get(i).parseFilename);
            Log.i("ejCheck", "Adapter Room parseUserName : "+ data.get(i).parseUserName);
            Log.i("ejCheck", "Adapter Room parseTime : "+ data.get(i).parseTime);
            mContext.startActivity(intent);

        });
        holder.roomAdapThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(getData.liveThumbnailURL).thumbnail(0.5f).into(holder.roomAdapThumbnail);
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("방목록어댑터", "Adapter > getItemViewType()");
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        Log.d("방목록어댑터", "Adapter > getItemCount()");
        return data.size();
    }
}

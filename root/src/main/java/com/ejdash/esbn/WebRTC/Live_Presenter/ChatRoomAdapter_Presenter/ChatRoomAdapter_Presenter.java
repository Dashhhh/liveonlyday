/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.WebRTC.Live_Presenter.ChatRoomAdapter_Presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;

import java.util.ArrayList;

public class ChatRoomAdapter_Presenter extends RecyclerView.Adapter<ChatRoomHolder_Presenter> {

    private static final String TAG = "Main Room List";
    ArrayList<ChatRoomSet_Presenter> data = new ArrayList<>();
    Context mContext;

    public ChatRoomAdapter_Presenter(ArrayList<ChatRoomSet_Presenter> data, Context mContext) {
        this.mContext = mContext;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }
    }

    @NonNull
    @Override
    public ChatRoomHolder_Presenter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypefaceProvider.registerDefaultIconSets();
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_chatroom, parent, false);
        return new ChatRoomHolder_Presenter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatRoomHolder_Presenter holder, int position) {
        final int i = position;

        ChatRoomSet_Presenter getData = data.get(position);
        holder.chatRoomAdapUserName.setMarkdownText(getData.sessionUserName + " : ");
        holder.chatRoomText.setMarkdownText(getData.chatText);

        holder.chatRoomText.requestFocus();

//        Log.i(TAG, "holder.liveParticipants.setText(getData.participants) > " + getData.roomID);

    }

    @Override
    public int getItemViewType(int position) {
        Log.d("방송자채팅어댑터", "Adapter > getItemViewType()");
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        Log.d("방송자채팅어댑터", "Adapter > getItemCount()");
        return data.size();
    }
}

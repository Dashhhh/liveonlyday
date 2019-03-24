/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.WebRTC.LiveViewer.ChatRoomAdapter_Viewer;

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

public class ChatRoomAdapterViewer extends RecyclerView.Adapter<ChatRoomHolderViewer> {

    private static final String TAG = "Main Room List";
    ArrayList<ChatRoomSetViewer> data = new ArrayList<>();
    Context mContext;

    public ChatRoomAdapterViewer(ArrayList<ChatRoomSetViewer> data, Context mContext) {
        this.mContext = mContext;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }

    }

    @NonNull
    @Override
    public ChatRoomHolderViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypefaceProvider.registerDefaultIconSets();
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_chatroom, parent, false);
        return new ChatRoomHolderViewer(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatRoomHolderViewer holder, int position) {
        final int i = position;

        ChatRoomSetViewer getData = data.get(position);
        holder.chatRoomAdapUserName.setMarkdownText(getData.sessionUserName + " : ");
        holder.chatRoomText.setMarkdownText(getData.chatText);

        holder.chatRoomText.requestFocus();

//        Log.i(TAG, "holder.liveParticipants.setText(getData.participants) > " + getData.roomID);

    }

    @Override
    public int getItemViewType(int position) {
        Log.d("시청자채팅어댑터", "Adapter > getItemViewType()");
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        Log.d("시청자채팅어댑터", "Adapter > getItemCount()");
        return data.size();
    }
}

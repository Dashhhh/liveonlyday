/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.WebRTC.Live_Presenter.ChatRoomAdapter_Presenter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;

public class ChatRoomHolder_Presenter extends RecyclerView.ViewHolder {

    com.beardedhen.androidbootstrap.AwesomeTextView chatRoomAdapUserName, chatRoomText;
    LinearLayout chatRoomRoot;
    CardView chatRoomAdapCard;
    ImageView thumbnailPath;

    public ChatRoomHolder_Presenter(View itemView) {
        super(itemView);
        TypefaceProvider.registerDefaultIconSets();
        chatRoomAdapUserName = itemView.findViewById(R.id.chatRoomAdapUserName);
        chatRoomText = itemView.findViewById(R.id.chatRoomAdapText);
        chatRoomRoot = itemView.findViewById(R.id.chatRoomRoot);
        chatRoomAdapCard = itemView.findViewById(R.id.chatRoomAdapCard);
    }
}

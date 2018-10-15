/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Live.LIVEAdapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;

public class MainLiveFragmentHolder extends RecyclerView.ViewHolder {

    com.beardedhen.androidbootstrap.AwesomeTextView roomAdapUserName, roomAdapTime, roomAdapRoomTitle, roomAdapRoomDescription, roomAdapLocationName, roomAdapLocationAddress, roomAdapWatcher;
    LinearLayout roomListRoot;
    CardView roomAdapCard;
    ImageView roomAdapThumbnail;        // TODO server.js 에서 썸네일 만들고 경로 받아와서 글라이드로 뿌리기

    MainLiveFragmentHolder(View itemView) {
        super(itemView);
        TypefaceProvider.registerDefaultIconSets();
        roomAdapUserName = itemView.findViewById(R.id.roomAdapUserName);
        roomAdapTime = itemView.findViewById(R.id.roomAdapTime);
        roomListRoot = itemView.findViewById(R.id.roomListRoot);
        roomAdapCard = itemView.findViewById(R.id.roomAdapCard);
        roomAdapThumbnail = itemView.findViewById(R.id.roomAdapThumbnail);

        roomAdapRoomTitle = itemView.findViewById(R.id.roomAdapRoomTitle);
        roomAdapRoomDescription = itemView.findViewById(R.id.roomAdapRoomDescription);
        roomAdapLocationName = itemView.findViewById(R.id.roomAdapLocationName);
        roomAdapLocationAddress = itemView.findViewById(R.id.roomAdapLocationAddress);
        roomAdapWatcher = itemView.findViewById(R.id.roomAdapWatcher);

    }
}

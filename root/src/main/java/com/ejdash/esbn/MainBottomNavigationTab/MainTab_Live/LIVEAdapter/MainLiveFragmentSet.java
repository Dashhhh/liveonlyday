/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Live.LIVEAdapter;

public class
MainLiveFragmentSet {

    int roomID;
    String parseFilename;
    String parseUserName;
    String parseTime;
    String liveThumbnailURL;
    String roomName;
    String roomDesc;
    String roomLocationName;
    String roomLocationAddress;
    String roomWatcher;
    String getRoomDesc;

    public MainLiveFragmentSet(int roomID,
                               String parseFilename,
                               String parseUserName,
                               String parseTime,
                               String liveThumbnailURL,
                               String roomName,
                               String roomDesc,
                               String roomLocationName,
                               String roomLocationAddress,
                               String roomWatcher


    ) {
        this.roomID = roomID;
        this.parseFilename = parseFilename;
        this.parseUserName = parseUserName;
        this.parseTime = parseTime;
        this.liveThumbnailURL = liveThumbnailURL;
        this.roomName = roomName;
        this.roomDesc= roomDesc;
        this.roomLocationName = roomLocationName;
        this.roomLocationAddress = roomLocationAddress;
        this.roomWatcher = roomWatcher;

    }
}






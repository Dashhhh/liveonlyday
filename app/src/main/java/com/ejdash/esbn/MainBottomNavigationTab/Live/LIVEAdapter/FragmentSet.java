/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.Live.LIVEAdapter;

public class
FragmentSet {

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

    public FragmentSet(int roomID,
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






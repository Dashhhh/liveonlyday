/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.FriendsAdapter;

public class MainFriendsFragmentSet {

    String targetId;
    String sourceId;
    String targetIdThmbnail;
    int acceptState;
    int isFriend;

    public MainFriendsFragmentSet(
            String targetId,
            String sourceId,
            String targetIdThmbnail,
            int acceptState,
            int isFriend
    ) {
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.targetIdThmbnail = targetIdThmbnail;
        this.acceptState = acceptState;
        this.isFriend = isFriend;
    }
}

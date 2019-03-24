/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.Friends.FriendsAdapter;

public class FragmentFriendSet {

    String targetId;
    String sourceId;
    String targetIdThmbnail;
    int acceptState;
    int isFriend;

    public FragmentFriendSet(
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

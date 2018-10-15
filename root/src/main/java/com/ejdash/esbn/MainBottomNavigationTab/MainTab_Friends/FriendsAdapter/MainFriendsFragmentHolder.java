/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_Friends.FriendsAdapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.view.BootstrapSizeView;
import com.ejdash.esbn.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainFriendsFragmentHolder extends RecyclerView.ViewHolder {


    BootstrapButton friendsListCallListCheckBox;
    CircularImageView friendsThumbnail;
    AppCompatTextView friendsListMyFriendsId;
    LinearLayout friendsAdapRoot, friendsListRequestWaitLayout, friendsListRequestAddingLayout;
    BootstrapButton friendsListRequestAddingAccept, friendsListRequestAddingDeny;

    public MainFriendsFragmentHolder(View itemView) {
        super(itemView);
        TypefaceProvider.registerDefaultIconSets();

        friendsListCallListCheckBox = itemView.findViewById(R.id.friendsListCallListCheckBox);
        friendsThumbnail = itemView.findViewById(R.id.friendsThumbnail);
        friendsListMyFriendsId = itemView.findViewById(R.id.friendsListMyFriendsId);
        friendsAdapRoot = itemView.findViewById(R.id.friendsAdapRoot);
        friendsListRequestWaitLayout = itemView.findViewById(R.id.friendsListRequestWaitLayout);
        friendsListRequestAddingLayout = itemView.findViewById(R.id.friendsListRequestAddingLayout);
        friendsListRequestAddingAccept = itemView.findViewById(R.id.friendsListRequestAddingAccept);
        friendsListRequestAddingDeny = itemView.findViewById(R.id.friendsListRequestAddingDeny);



    }

}

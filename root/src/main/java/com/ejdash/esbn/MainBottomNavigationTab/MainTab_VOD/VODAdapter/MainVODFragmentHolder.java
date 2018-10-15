/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.MainTab_VOD.VODAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.R;

public class MainVODFragmentHolder extends RecyclerView.ViewHolder {

    com.beardedhen.androidbootstrap.AwesomeTextView VODAdapUser, VODAdapTime, VODAdapDesc;
    ImageView VODImage;
    android.support.constraint.ConstraintLayout VODAdapRoot;

    public MainVODFragmentHolder(View itemView) {
        super(itemView);
        TypefaceProvider.registerDefaultIconSets();
        VODAdapUser = itemView.findViewById(R.id.VODAdapUser);
        VODAdapTime = itemView.findViewById(R.id.VODAdapTime);
        VODAdapDesc = itemView.findViewById(R.id.VODAdapDesc);
        VODImage = itemView.findViewById(R.id.VODImage);
        VODAdapRoot = itemView.findViewById(R.id.VODAdapRoot);
    }
}

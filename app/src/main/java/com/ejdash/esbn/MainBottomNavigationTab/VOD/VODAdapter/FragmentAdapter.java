/*
 * Copyright (c) 2018
 */

package com.ejdash.esbn.MainBottomNavigationTab.VOD.VODAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.ejdash.esbn.R;
import com.ejdash.esbn.VOD.VODViewing;

import java.util.ArrayList;

public class FragmentAdapter extends RecyclerView.Adapter<FragmentHolder> {

    private static final String TAG = "Main Room List";
    ArrayList<FragmentSet> data = new ArrayList<>();
    Context mContext;

    public FragmentAdapter(ArrayList<FragmentSet> data, Context mContext) {
        this.mContext = mContext;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }

    }

    @NonNull
    @Override
    public FragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypefaceProvider.registerDefaultIconSets();
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_vod_list, parent, false);
        return new FragmentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FragmentHolder holder, int position) {
        final int i = position;

        FragmentSet getData = data.get(position);
        holder.VODAdapUser.setMarkdownText("{fa_user} " + getData.user);
        holder.VODAdapDesc.setMarkdownText("{ty_arrow_right_outline} " + getData.index);
        holder.VODAdapTime.setMarkdownText("{fa_times} " + getData.time );

        holder.VODImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(getData.thumbnail).into(holder.VODImage);

        holder.VODAdapRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VODViewing.class);
                intent.putExtra("VOD_URL", data.get(i).url);
                intent.putExtra("VOD_INDEX", data.get(i).index);
                intent.putExtra("VOD_TIME", data.get(i).time);
                intent.putExtra("VOD_USER", data.get(i).user);
                Log.i("ejCheck", "Adapter data.get(i).url : "+ data.get(i).url);
                Log.i("ejCheck", "Adapter data.get(i).index : "+ data.get(i).index);
                Log.i("ejCheck", "Adapter data.get(i).time : "+ data.get(i).time);
                Log.i("ejCheck", "Adapter data.get(i).user : "+ data.get(i).user);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        Log.d("VOD어댑터", "Adapter > getItemViewType()");
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        Log.d("VOD어댑터", "Adapter > getItemCount()");
        return data.size();
    }
}

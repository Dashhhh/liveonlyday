package com.ejdash.esbn.MainBottomNavigationTab.Friends.FriendsAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.Glide;
import com.ejdash.esbn.MainBottomNavigationTab.Friends.Async.AsycnAgreeRequestForAddingFriends;
import com.ejdash.esbn.MainBottomNavigationTab.Friends.Async.AsycnDenyAddingFriendsRequest;
import com.ejdash.esbn.R;
import com.ejdash.esbn.utils.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/*
    친구에게 영상통화를 걸기 전 이 RecyclerView Adapter에서 로드 된 내 친구를 선택하고 실제 영상통화를 건다
 */
public class FragmentFriendAdapter extends RecyclerView.Adapter<FragmentFriendHolder> {

    private static final String TAG = "MainRoomFriendsList";
    ArrayList<FragmentFriendSet> data = new ArrayList<>();
    Context mContext;

    public FragmentFriendAdapter(ArrayList<FragmentFriendSet> data, Context mContext) {
        this.mContext = mContext;

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }

    }

    @NonNull
    @Override
    public FragmentFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TypefaceProvider.registerDefaultIconSets();
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_friends_list, parent, false);
        return new FragmentFriendHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FragmentFriendHolder holder, int position) {
        final int i = position;

        FragmentFriendSet getData = data.get(position);

        SharedPreferenceUtil pref = new SharedPreferenceUtil(mContext);

        Log.i("친구어댑터", "onBindViewHolder 값 확인 - getData.isFriend          > " + getData.isFriend);
        Log.i("친구어댑터", "onBindViewHolder 값 확인 - getData.acceptState       > " + getData.acceptState);
        Log.i("친구어댑터", "onBindViewHolder 값 확인 - getData.sourceId          > " + getData.sourceId);
        Log.i("친구어댑터", "onBindViewHolder 값 확인 - getData.targetId          > " + getData.targetId);
        Log.i("친구어댑터", "onBindViewHolder 값 확인 - getData.targetIdThmbnail  > " + getData.targetIdThmbnail);
        /*
            추가되는 뷰 종류
                - 친구요청을 보내서 요청을 대기하는 뷰(테이블 컬럼 상 'sourceId' 컬럼을 로그인 된 아이디로 조회 했을 때 acceptState = 0 인 경우)
                - 이미 친구인 뷰(테이블 컬럼 상 'sourceId' 컬럼을 로그인 된 아이디로 조회 했을 때 acceptState = 1 인 경우)
                - 친구요청을 받아서 수락 혹은 거절을 대기하는 뷰 (테이블 컬럼 상 'targetId 컬럼을 로그인 된 아이디로 조회 했을 때 acceptState = 0 인 경우)

                - targetId 가 로그인 된 아이디와 같다면 친구 요청을 받은 상태이므로 수락/거절 버튼 레이아웃이 보여야 함
                - sourceId 가 로그인 된 아이디와 같다면 친구 요청을 보낸 상태이므로 수락 대기 중 텍스트 뷰 레이아웃이 보여야 함
         */

        Glide.with(mContext).load(getData.targetIdThmbnail).thumbnail(0.3f).into(holder.friendsThumbnail);
        holder.friendsListCallListCheckBox.setVisibility(View.GONE);
        holder.friendsListCallListCheckBox.setOnCheckedChangedListener((bootstrapButton, isChecked) -> {

            if (isChecked) {

                // 손으로 뷰 터치 > 체크박스 체크 된 시점

            } else {

                // 손으로 뷰 터치 > 체크박스 해제 된 시점

            }

        });

        /*
            isFriend는 DB friendList 테이블 컬럼 중 친구 수락을 눌렀는지 여부 확인하는 컬럼임
                // isFriend : 0 =  아직 친구가 아님 상대방의 수락을 기다림

         */
        if (getData.targetId.equals(pref.getSharedData("userId"))) {

            holder.friendsListRequestWaitLayout.setVisibility(View.GONE);
            holder.friendsListRequestAddingLayout.setVisibility(View.VISIBLE);

            holder.friendsListMyFriendsId.setText(getData.sourceId);

            // acceptState : 1 = 친구가 아닐 때 / 친구 수락을 아직 하지 않았을 때 > 친구 수락 할 경우 isFriend = 1로 업데이트 됨

            if (getData.acceptState == 0) {
                 /*
                     친구신청 수락
                  */
                holder.friendsListRequestAddingAccept.setOnClickListener(v -> {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject = new AsycnAgreeRequestForAddingFriends(getData.sourceId, getData.targetId).execute().get(10000, TimeUnit.MILLISECONDS);
                        holder.friendsListRequestAddingLayout.setVisibility(View.GONE);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "네트워크 환경을 확인하고 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    }
                });

                   /*
                       친구신청 거절
                    */
                holder.friendsListRequestAddingDeny.setOnClickListener(v -> {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject = new AsycnDenyAddingFriendsRequest(getData.sourceId, getData.targetId).execute().get(10000, TimeUnit.MILLISECONDS);
                        holder.friendsListRequestAddingLayout.setVisibility(View.GONE);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "네트워크 환경을 확인하고 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            holder.friendsListMyFriendsId.setText(getData.targetId);

            holder.friendsListRequestWaitLayout.setVisibility(View.GONE);
            holder.friendsListRequestAddingLayout.setVisibility(View.VISIBLE);

        }

        if(getData.isFriend == 1){
            holder.friendsListRequestWaitLayout.setVisibility(View.GONE);
            holder.friendsListRequestAddingLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Log.d("Friends어댑터", "Adapter > getItemViewType()");
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        Log.d("Friends어댑터", "Adapter > getItemCount()");
        return data.size();
    }
}

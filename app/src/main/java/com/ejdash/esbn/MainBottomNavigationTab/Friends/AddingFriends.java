package com.ejdash.esbn.MainBottomNavigationTab.Friends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ejdash.esbn.MainBottomNavigationTab.Friends.Async.AsycnAddingFriendsRequest;
import com.ejdash.esbn.R;
import com.ejdash.esbn.utils.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddingFriends extends AppCompatActivity {

    @BindView(R.id.addingFriendsId)
    AppCompatEditText addingFriendsId;
    @BindView(R.id.addingFriendsAddingRequestButton)
    BootstrapButton addingFriendsAddingRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_friends);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.addingFriendsAddingRequestButton)
    public void onViewClicked() {

        SharedPreferenceUtil pref = new SharedPreferenceUtil(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new AsycnAddingFriendsRequest(pref.getSharedData("userId"),addingFriendsId.getText().toString()).execute().get(10000, TimeUnit.MILLISECONDS);
            finish();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Toast.makeText(this, "네트워크 상태가 고르지 못합니다! 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}

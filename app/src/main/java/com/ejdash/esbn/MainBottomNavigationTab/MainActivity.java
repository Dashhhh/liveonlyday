package com.ejdash.esbn.MainBottomNavigationTab;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ejdash.esbn.MainBottomNavigationTab.Info.InfoFragment;
import com.ejdash.esbn.MainBottomNavigationTab.Live.LiveFragment;
import com.ejdash.esbn.MainBottomNavigationTab.VOD.VODFragment;
import com.ejdash.esbn.R;
import com.ejdash.esbn.WebRTC.LiveHost.LivePresenter;

/**
 * 로그인 직후 액티비티
 * Bottom Navigation View 하나로 구성 되었고, 각 네비게이션 뷰의 각 탭은 Fragment로 구성 되어있음
 * 필요 시 아래 내용 Declararion 해볼 것
 *
 * @see LiveFragment
 * @see VODFragment
 * @see InfoFragment
 * 방송 시작용 Floating Action Button은 유일하게 Bottom Navigation View 위에 올라 옴
 *  - Bottom Navigation View 와 위치가 겹쳐서 안보임 (margin 줘도 안됨)
 */
public class MainActivity extends AppCompatActivity
        implements
        InfoFragment.OnFragmentInteractionListener,
        VODFragment.OnFragmentInteractionListener,
        LiveFragment.OnFragmentInteractionListener,
//        FragmentMainFriends.OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    /*
        Bottom Navigation View 메뉴에 탭을 추가 하려는 경우 프래그먼트를 만들고 해당 프래그먼트 리스너를 implemet 해야함
            - ref ) FragmentMainFriends.OnFragmentInteractionListener
     */
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    TextView mainToolbarTitleText;
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TypefaceProvider.registerDefaultIconSets();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnav);
        init();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_live);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void init() {

        mainToolbarTitleText = findViewById(R.id.mainToolbarTitleText);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf.ttf");
        mainToolbarTitleText.setTypeface(tf);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LivePresenter.class);
                    startActivity(intent);
                }
        );
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //BottomNavigationView Switching 용 Fragment
        Fragment replaceFragment = null;

        switch (item.getItemId()) {
            case R.id.action_live:
                /*
                    Floatting Action Button : 방송시작버튼이며 메인 탭 외에서는 보이면 안됨
                 */
                fab.setVisibility(View.VISIBLE);
                replaceFragment = new LiveFragment();

                break;
//                return loadFragment(replaceFragment);
            case R.id.action_vod:
                fab.setVisibility(View.GONE);
                replaceFragment = new VODFragment();

                break;
//                return loadFragment(replaceFragment);
            case R.id.action_info:
                fab.setVisibility(View.GONE);
                replaceFragment = new InfoFragment();
                break;
//                return loadFragment(replaceFragment);
          /*
            case R.id.action_friends:
                fab.setVisibility(View.GONE);
                replaceFragment = new FragmentMainFriends();
                break;
                */
//                return loadFragment(replaceFragment);


        }
        return loadFragment(replaceFragment);

//        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("FlowCheck", "onFragmentInteraction: ");
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_framgent_container, fragment)
                .commit();
        return true;
    }


}

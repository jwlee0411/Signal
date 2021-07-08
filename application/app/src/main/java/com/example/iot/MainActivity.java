package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;
    TabLayout tab;
    PagerAdapter mAdapter;
    SharedPreferences mShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(MainActivity.this, SplashActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShared = PreferenceManager.getDefaultSharedPreferences(this);
        if (mShared.getBoolean( "checked", false)){
            Intent intent = new Intent(MainActivity.this, FiredectedService.class);
            startService(intent);
        }


        tab=findViewById(R.id.tab);
        pager=findViewById(R.id.pager);
        mAdapter = new PagerAdapter(getSupportFragmentManager());

        pager.setAdapter(mAdapter);

        tab.addTab(tab.newTab().setText("메인"));
        tab.addTab(tab.newTab().setText("세부사항"));

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}

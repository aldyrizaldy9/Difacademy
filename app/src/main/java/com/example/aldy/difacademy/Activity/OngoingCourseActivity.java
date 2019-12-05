package com.example.aldy.difacademy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.aldy.difacademy.OngoingBlendedFragment;
import com.example.aldy.difacademy.OngoingOnlineFragment;
import com.example.aldy.difacademy.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class OngoingCourseActivity extends AppCompatActivity {

    public static String userDocId = "";
    TextView tvNavbar;
    ConstraintLayout clBack, clNavbar;
    ImageView imgBack;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_course);

        initView();
        setViewPager();

        Intent intent = getIntent();
        userDocId = intent.getStringExtra("userDocId");
    }

    private void initView() {
        clNavbar = findViewById(R.id.cl_navbar);
        clNavbar.setBackgroundColor(getResources().getColor(R.color.navCream));
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("OnGoing Course");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        tabLayout = findViewById(R.id.tl_ongoing);
        viewPager = findViewById(R.id.vp_ongoing);
    }

    private void setViewPager() {
        final OngoingCourseActivity.ViewPagerAdapter viewPagerAdapter = new OngoingCourseActivity.ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new OngoingBlendedFragment(), "Blended");
        viewPagerAdapter.addFragment(new OngoingOnlineFragment(), "Online");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}

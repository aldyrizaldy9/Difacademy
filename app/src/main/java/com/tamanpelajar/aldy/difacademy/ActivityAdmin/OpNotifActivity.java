package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

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

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.Fragment.OpGraduationBlendedFragment;
import com.tamanpelajar.aldy.difacademy.Fragment.OpGraduationOnlineFragment;
import com.tamanpelajar.aldy.difacademy.Fragment.OpPaymentBlendedFragment;
import com.tamanpelajar.aldy.difacademy.Fragment.OpPaymentOnlineFragment;
import com.tamanpelajar.aldy.difacademy.R;

import java.util.ArrayList;

public class OpNotifActivity extends AppCompatActivity {

    TextView tvNavbar;
    ConstraintLayout clBack;
    ImageView imgBack;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_notif);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            initView();
            setViewPager();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String fromNotif = "" + intent.getStringExtra(CommonMethod.intentFromNotification);
        if (fromNotif.equals("ya")) {
            startActivity(new Intent(OpNotifActivity.this, OpMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        tvNavbar = findViewById(R.id.tv_navbar);
        tvNavbar.setText("Notification");
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
        tabLayout = findViewById(R.id.tl_op_notif);
        viewPager = findViewById(R.id.vp_op_notif);
    }

    private void setViewPager() {
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new OpPaymentBlendedFragment(OpNotifActivity.this), "Payment Blended");
        viewPagerAdapter.addFragment(new OpPaymentOnlineFragment(OpNotifActivity.this), "Payment Online");
        viewPagerAdapter.addFragment(new OpGraduationBlendedFragment(), "Graduation Blended");
        viewPagerAdapter.addFragment(new OpGraduationOnlineFragment(), "Graduation Online");

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

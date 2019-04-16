package com.example.parkyoungcheol.littletigersinit.Chat;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.parkyoungcheol.littletigersinit.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMainActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.fab)
    FloatingActionButton mFabtn;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    ViewPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        ButterKnife.bind(this); // 버터 나이프 사용

        mTabLayout.setupWithViewPager(mViewPager);
        setUpViewPager();

        mFabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = mPagerAdapter.getItem(mViewPager.getCurrentItem());
                if(currentFragment instanceof ListOfFriendsFragment){
                    ((ListOfFriendsFragment) currentFragment).toggleSearchBar();
                }else {
                    // 친구 탭으로 이동
                    mViewPager.setCurrentItem(2, true); // 2번이 friend fragment, 부드럽게 가라고 true
                    // 체크박스가 보일 수 있도록 처리
                    ListOfFriendsFragment listOfFriendsFragment = (ListOfFriendsFragment) mPagerAdapter.getItem(1);
                    listOfFriendsFragment.toggleSelectionMode();

                }
            }
        });
    }

    private void setUpViewPager() {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new ChatFragment(), "채팅");
        mPagerAdapter.addFragment(new ListOfFriendsFragment(), "친구");
        mViewPager.setAdapter(mPagerAdapter);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }
}

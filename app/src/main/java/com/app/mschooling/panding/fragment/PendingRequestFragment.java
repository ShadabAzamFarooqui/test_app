package com.app.mschooling.panding.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.app.mschooling.base.fragment.BaseFragment;
import com.app.mschooling.com.R;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.Subscribe;

public class PendingRequestFragment extends BaseFragment {
    LinearLayout search_btn;
    TabLayout tabLayout;
    ViewPager viewPager;
    LinearLayoutManager mLayoutManager;
    static PendingStudentsFragment pendingStudentsFragment;
    static PendingTeachersFragment pendingTeachersFragment;
    public static EditText searchEdit;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_request, container, false);
        toolbarClick(view,getContext().getResources().getString(R.string.pending));
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        search_btn = (LinearLayout) view.findViewById(R.id.search_btn);
        searchEdit = (EditText) view.findViewById(R.id.search);

        context=this;
        if (pendingStudentsFragment!=null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(pendingStudentsFragment).commit();
        }

        if (pendingTeachersFragment!=null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(pendingTeachersFragment).commit();
        }

        pendingStudentsFragment=new PendingStudentsFragment();
        pendingTeachersFragment=new PendingTeachersFragment();


        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager()));
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return pendingStudentsFragment;
                case 1:
                    return pendingTeachersFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

//        private String[] tabTitles = new String[]{ "FetchA", "Enter"};
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tabTitles[position];
//        }
    }


    @Subscribe
    public void timeout(String msg) {

    }
}


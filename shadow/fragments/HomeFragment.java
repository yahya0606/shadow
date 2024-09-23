package com.yahya.shadow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.yahya.shadow.R;
import com.yahya.shadow.VpAdapters.VpHomeAdapter;
import com.yahya.shadow.fragments.posts.AllPostsFragment;
import com.yahya.shadow.fragments.posts.FriendPostsFragment;
import com.yahya.shadow.fragments.posts.SearchPostsFragment;


public class HomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tabLayout = view.findViewById(R.id.taber);
        viewPager = view.findViewById(R.id.viewer);

        tabLayout.setupWithViewPager(viewPager);

        VpHomeAdapter vpAdapter = new VpHomeAdapter(getFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new SearchPostsFragment(),"Search Posts");
        vpAdapter.addFragment(new FriendPostsFragment(),"Friend Posts");
        vpAdapter.addFragment(new AllPostsFragment(),"All Posts");

        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.account);
        tabLayout.getTabAt(2).setIcon(R.drawable.search);

        viewPager.setAdapter(vpAdapter);
        viewPager.setCurrentItem(0);

        return view;
    }

}
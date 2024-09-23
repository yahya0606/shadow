package com.yahya.shadow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.google.android.material.tabs.TabLayout;
import com.yahya.shadow.VpAdapters.VpAdapter;
import com.yahya.shadow.fragments.UsersFragment;
import com.yahya.shadow.fragments.HomeFragment;
import com.yahya.shadow.fragments.ProfileFragment;
import com.yahya.shadow.services.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        tabLayout = findViewById(R.id.tabs);
        Bundle StalkPosition = getIntent().getExtras();
        viewPager = findViewById(R.id.views);
        tabLayout.setupWithViewPager(viewPager);

        startService(new Intent(this, NotificationService.class));

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        int current = StalkPosition.getInt("current");

        viewPager.setCurrentItem(current);

    }
    private void setupTabIcons() {
        tabLayout.getTabAt(1).setText("Shadow");
        tabLayout.setTabTextColors(Color.parseColor("#dfdfdf"), Color.parseColor("#ffffff"));
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_view_message);
        tabLayout.getTabAt(2).setCustomView(R.layout.custom_view_profile);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new UsersFragment(), "");
        adapter.addFrag(new HomeFragment(), "");
        adapter.addFrag(new ProfileFragment(), "");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
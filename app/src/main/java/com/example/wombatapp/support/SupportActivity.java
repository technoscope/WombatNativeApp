package com.example.wombatapp.support;

import android.graphics.Color;
import android.os.Bundle;

import com.example.wombatapp.R;
import com.example.wombatapp.SelectionPagerAdabter;
import com.example.wombatapp.support.fragments.PeopleFragment;
import com.example.wombatapp.support.fragments.Productsfragment;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class SupportActivity extends AppCompatActivity {
    SelectionPagerAdabter sectionsPagerAdapter;
    ViewPager viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        sectionsPagerAdapter = new SelectionPagerAdabter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new PeopleFragment(), "People");
        sectionsPagerAdapter.addFragment(new Productsfragment(), "Products");
        viewPager = findViewById(R.id.view_pager3);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs3);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
    }
}
package com.example.wombatapp.dashboard;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.example.wombatapp.FirebaseLogin;
import com.example.wombatapp.R;
import com.example.wombatapp.SelectionPagerAdabter;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.support.SupportActivity;
import com.example.wombatapp.userfragments.AllTimeFragment;
import com.example.wombatapp.userfragments.MonthFragment;
import com.example.wombatapp.userfragments.WeekFragment;
import com.example.wombatapp.userfragments.YearFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class DashboardActivity extends AppCompatActivity {

    RecyclerView recyclerV;
    ImageView imageView;
    ViewPager viewPager;
    TabLayout tabs;
    ArrayList<String> userslist;
    BottomNavigationView bottomNavigationView;
    SelectionPagerAdabter sectionsPagerAdapter;
    //GoogleFitService googleFitService;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    //private GoogleApiClient mApiClient;

    ArrayList<String> accountlist=new ArrayList<>();
    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userslist = new ArrayList<>();
     //   googleFitService=new GoogleFitService();
        imageView = findViewById(R.id.id_add_new_member);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, FirebaseLogin.class));

            }
        });
        //accountlist.add("AhmadKKhan67@gmail.com");
        //accountlist.add("Ahmad.rehman12@gmail.com");
        //Intent serviceIntent = new Intent(DashboardActivity.this,GoogleFitService.class);
        //serviceIntent.putExtra("UserID", accountlist);
       // startService(serviceIntent);
        sectionsPagerAdapter = new SelectionPagerAdabter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new WeekFragment(), "Week");
        sectionsPagerAdapter.addFragment(new MonthFragment(), "Month");
        sectionsPagerAdapter.addFragment(new YearFragment(), "Year");
        sectionsPagerAdapter.addFragment(new AllTimeFragment(), "All time");
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.black));
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
      //              mApiClient.clearDefaultAccountAndReconnect();
                    //startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
                } else if (item.getItemId() == R.id.navigation_support) {
                    startActivity(new Intent(DashboardActivity.this, SupportActivity.class));
                }
                return true;
            }
        });
        recyclerV = findViewById(R.id.recycleview);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            Cursor cursor = databaseHelper.getusers();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        userslist.add(cursor.getString(0));
                    }
                    while (cursor.moveToNext());
                }
            }
            assert cursor != null;
            cursor.close();
         cursor.close();

        } catch (Exception ignored) {
        }

        GridElementAdapter adapter = new GridElementAdapter(this, userslist);
        recyclerV.setLayoutManager(linearLayout);
        recyclerV.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 123);
            }
        }
    }

}


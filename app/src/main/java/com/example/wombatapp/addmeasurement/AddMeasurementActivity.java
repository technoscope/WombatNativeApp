package com.example.wombatapp.addmeasurement;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.example.wombatapp.R;
import com.example.wombatapp.minttihealth.health.App;
import com.example.wombatapp.minttihealth.health.BaseFragment2;
import com.example.wombatapp.minttihealth.health.HcService;
import com.example.wombatapp.minttihealth.health.adapter.CustomViewPager;
import com.example.wombatapp.minttihealth.health.adapter.FragmentsAdapter2;
import com.google.android.material.tabs.TabLayout;
import com.linktop.DeviceType;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.BluetoothState;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class AddMeasurementActivity extends AppCompatActivity implements MonitorDataTransmissionManager.OnServiceBindListener,
        TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener, ServiceConnection {

    public View viewFocus;
    public CustomViewPager viewPager;
    MeasurementHomeFragment mMeasurementHomeFragment = new MeasurementHomeFragment();
    private final SparseArray<BaseFragment2> sparseArray = new SparseArray<>();
    public HcService mHcService;
    //MeasurementHomeFragment measurementHomeFragment;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HcService.BLE_STATE) {
                final int state = (int) msg.obj;
                Log.e("Message", "receive state:" + state);
                if (state == BluetoothState.BLE_NOTIFICATION_ENABLED) {
                    mHcService.dataQuery(HcService.DATA_QUERY_SOFTWARE_VER);
                } else {
                    mMeasurementHomeFragment.onBleState(state);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // on a large screen device ...

        }
//        else if((getResources().getConfiguration().screenLayout& Configuration.SCREENLAYOUT_SIZE_MASK) ==
//                Configuration.SCREENLAYOUT_SIZE_NORMAL){
//            setContentView(R.layout.activity_add_measurement2);
//            getSupportActionBar().hide();
//
//        }getSupportActionBar().hide();
        getSupportActionBar().hide();

        //Bind service about Bluetooth connection.
        if (!App.isUseCustomBleDevService2) {
            Intent serviceIntent = new Intent(this, HcService.class);
            bindService(serviceIntent, this, BIND_AUTO_CREATE);
            Toast.makeText(this, "bind", Toast.LENGTH_SHORT).show();
        } else {
            //绑定服务，
            // 类型是 HealthMonitor（HealthMonitor健康检测仪），
            MonitorDataTransmissionManager.getInstance().bind(DeviceType.HealthMonitor, this,
                    this);
        }
    }

    @Override
    protected void onDestroy() {
        if (!App.isUseCustomBleDevService2) {
            unbindService(this);
        } else {
            //解绑服务
            MonitorDataTransmissionManager.getInstance().unBind();
        }
        App.isShowUploadButton.set(false);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        mHcService = ((HcService.LocalBinder) service).getService();
        mHcService.setHandler(mHandler);
        mHcService.initBluetooth();

        //UI
        Log.e("onSericeConnected", "Connected");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MeasurementHomeFragment fragment = new MeasurementHomeFragment();
        fragmentTransaction.add(R.id.add_measurement_frame, fragment);
        fragmentTransaction.commit();
        //  viewPager.setAdapter(adapter);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mHcService = null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void getMenusFragments() {
        mMeasurementHomeFragment = new MeasurementHomeFragment();
        sparseArray.put(0, mMeasurementHomeFragment);
        sparseArray.put(1, new BloodPressureFragment());
        sparseArray.put(2, new SPO2Fragment());
        sparseArray.put(3, new ECGFragment());
    }

    @Override
    public void onServiceBind() {
        MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.health_monitor_dev_name_prefixes);
//        MonitorDataTransmissionManager.getInstance().setStrongEcgGain(true);//設置增強心電圖增益
        //服务绑定成功后加载各个测量界面
        FragmentsAdapter2 adapter = new FragmentsAdapter2(this, getSupportFragmentManager());
        getMenusFragments();
        adapter.setFragments(sparseArray);
//        viewPager.setAdapter(adapter);
    }

    @Override
    public void onServiceUnbind() {

    }
}
package com.example.wombatapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.wombatapp.bluetooth.BluetoothSettingsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BluetoothSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        BluetoothSettingsFragment bluetoothSettingsFragment = new BluetoothSettingsFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, bluetoothSettingsFragment, "a");
        transaction.addToBackStack(null);
        transaction.commit();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BluetoothSettingActivity.this, MainActivity.class));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

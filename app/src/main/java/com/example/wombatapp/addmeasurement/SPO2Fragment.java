package com.example.wombatapp.addmeasurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wombatapp.MainActivity;
import com.example.wombatapp.R;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.databinding.FragmentSPO2Binding;
import com.example.wombatapp.minttihealth.health.MeasureFragment;
import com.example.wombatapp.minttihealth.health.MeasureFragment2;
import com.example.wombatapp.minttihealth.health.bean.SpO2;
import com.example.wombatapp.minttihealth.health.wave.PPGDrawWave;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnSpO2ResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.OxTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SPO2Fragment extends MeasureFragment2 implements OnSpO2ResultListener {

    private SpO2 model;
    private OxTask mOxTask;
    private PPGDrawWave oxWave;
    public SPO2Fragment() {
        // Required empty public constructor
    }

    @Override
    public int getTitle() {
        return 0;
    }

    @Override
    public boolean startMeasure() {
        if (mOxTask != null) {
            mOxTask.start();
        } else {
            MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.SPO2);
        }
        return true;
    }
    @NonNull
    @Override
    protected ViewDataBinding onCreateBindingView(LayoutInflater inflater,
                                                  @Nullable ViewGroup container,
                                                  @Nullable Bundle savedInstanceState) {
        FragmentSPO2Binding binding = setBindingContentView(inflater, R.layout.fragment_s_p_o2, container);
        binding.setContent(this);
      //  binding.setShowUpload(App.isShowUploadButton);
        //oxWave = new PPGDrawWave();
    //    binding.ppgWave.setDrawWave(oxWave);
        this.btnMeasure = binding.btnMeasure;
        model = new SpO2();
        binding.setModel(model);
        return binding;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHcService != null) {
            mOxTask = mHcService.getBleDevManager().getOxTask();
            mOxTask.setOnSpO2ResultListener(this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnSpO2ResultListener(this);
        }
    }


    @Override
    public void stopMeasure() {
        if (mOxTask != null) {
            mOxTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }

    }

    @Override
    public void clickUploadData(View v) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void reset() {

    }

    @Override
    public void onSpO2Result(int bloodOxygen, int heartrate) {
        model.setValue(bloodOxygen);
        model.setHr(heartrate);

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        String remarks = "Great";
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        boolean result = databaseHelper.addMeasurementHeart(MainActivity.USER_NAME,Integer.toString(heartrate),Integer.toString(bloodOxygen),remarks,currentDateAndTime);
        if (!result)
            Toast.makeText(getContext(),"Error in submitting data to database", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSpO2Wave(int i) {

    }

    @Override
    public void onSpO2End() {
        model.setTs(System.currentTimeMillis() / 1000L);
        resetState();
    }

    @Override
    public void onFingerDetection(int state) {
        if (state == FINGER_NO_TOUCH) {
            stopMeasure();
            model.setValue(0);
            model.setHr(0);
            toast("No finger was detected on the SpO₂ sensor.");
        }

    }
}
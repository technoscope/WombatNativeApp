package com.example.wombatapp.addmeasurement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wombatapp.R;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.databinding.FragmentBloodPressureBinding;
import com.example.wombatapp.minttihealth.health.MeasureFragment2;
import com.example.wombatapp.minttihealth.health.bean.Bp;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnBpResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.BpTask;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.example.wombatapp.MainActivity.USER_NAME;

public class BloodPressureFragment extends MeasureFragment2 implements OnBpResultListener {

    private Bp model;
    private BpTask mBpTask;

    public BloodPressureFragment() {
        // Required empty public constructor
    }
    @Override
    public int getTitle() {
        return 0;
    }

    @Override
    public boolean startMeasure() {
        if (mBpTask != null) {
            if (mHcService.getBleDevManager().getBatteryTask().getPower() < 20) {
                toast("设备电量过低，请充电\nLow power.Please charge.");
                return false;
            }
            mBpTask.start();
        } else {
            if (MonitorDataTransmissionManager.getInstance().getBatteryValue() < 20) {
                toast("设备电量过低，请充电\nLow power.Please charge.");
                return false;
            }
            MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.BP);
        }
        return true;
    }

    @Override
    public void stopMeasure() {
        if (mBpTask != null) {
            mBpTask.stop();
        } else {
            MonitorDataTransmissionManager.getInstance().stopMeasure();
        }
    }

    @Override
    protected ViewDataBinding onCreateBindingView(LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBloodPressureBinding binding = setBindingContentView(inflater, R.layout.fragment_blood_pressure, container);
         binding.setContent(this);
        //  binding.setShowUpload(App.isShowUploadButton);
        this.btnMeasure = binding.btnMeasure;
        model = new Bp();
        binding.setModel(model);
        return binding;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mHcService != null) {
            mBpTask = mHcService.getBleDevManager().getBpTask();
            mBpTask.setOnBpResultListener(this);
        } else {
            MonitorDataTransmissionManager.getInstance().setOnBpResultListener(this);
        }
    }

    //    @Override
//    public int getTitle() {
//        return R.string.blood_pressure;
//    }
    @Override
    public void clickUploadData(View v) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void onBpResult(int systolicPressure, int diastolicPressure, int heartRate) {
        model.setTs(System.currentTimeMillis() / 1000L);
        model.setSbp(systolicPressure);
        model.setDbp(diastolicPressure);
        model.setHr(heartRate);
        resetState();

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        String remarks = "Great";
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.addMeasurementBloodPressure(USER_NAME,String.valueOf(systolicPressure),String.valueOf(diastolicPressure),String.valueOf(heartRate),remarks,currentDateAndTime);
    }

    @Override
    public void onBpResultError() {
        resetState();

    }

    @Override
    public void onLeakError(int errorType) {
        resetState();
        Observable.just(errorType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(error -> {
                    int textId = 0;
                    switch (error) {
                        case 0:
                            textId = R.string.leak_and_check;
                            break;
                        case 1:
                            textId = R.string.measurement_void;
                            break;
                        default:
                            break;
                    }
                    if (textId != 0)
                        Toast.makeText(getContext(), getString(textId), Toast.LENGTH_SHORT).show();
                });
    }

    }

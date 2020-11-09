package com.example.wombatapp.userfragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wombatapp.R;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.databinding.FragmentTodayBinding;
import com.example.wombatapp.minttihealth.health.MeasureFragment;
import com.example.wombatapp.minttihealth.health.bean.SpO2;
import com.example.wombatapp.minttihealth.health.wave.PPGDrawWave;
import com.example.wombatapp.model.StepModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.infs.OnSpO2ResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.OxTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import lib.linktop.obj.DataFile;
import lib.linktop.sev.HmLoadDataTool;

import static android.content.Context.MODE_PRIVATE;

public class TodayFragment extends MeasureFragment implements OnSpO2ResultListener, MonitorDataTransmissionManager.OnServiceBindListener {

    TextView fatview, musclesview, weightview,steptext,tvUpdateTimeWeightData, tvRemarksWeight;
    ScaleModel modell = new ScaleModel();
    StepModel stepModel=new StepModel();
    Datamodel datamodel = new Datamodel();
    private SpO2 model;
    private OxTask mOxTask;
    private PPGDrawWave oxWave;
    DatabaseReference mReference;
    DatabaseHelper databaseHelper;
    String username;

    public TodayFragment() {
    }

    public static TodayFragment getInstance() {
        return new TodayFragment();
    }

    SharedPreferences sh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getTitle() {
        return 0;
    }


    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sh = getActivity().getSharedPreferences("measurement",
                MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(getContext());
        fatview = view.findViewById(R.id.id_fat);
        steptext=view.findViewById(R.id.id_steps);
        TextView textView = view.findViewById(R.id.id_time);
        textView.setText(getTodayDate());
        musclesview = view.findViewById(R.id.id_musclesmass);
        weightview = view.findViewById(R.id.id_weight);
        tvUpdateTimeWeightData = view.findViewById(R.id.weight_update_time);
        tvRemarksWeight = view.findViewById(R.id.tv_remarks_weight);

        Bundle bundle = this.getArguments();
        username = bundle.getString("username").trim();
        mReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        try {
            Cursor cursor = databaseHelper.getWeightData(username);
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    tvUpdateTimeWeightData.setText(cursor.getString(5));
                    tvRemarksWeight.setText(cursor.getString(4));
                    fatview.setText(cursor.getString(3));
                    musclesview.setText(cursor.getString(2));
                    weightview.setText(cursor.getString(1));
                }
            } else {
                fatview.setText("0");
                musclesview.setText("0");
                weightview.setText("0");
            }
        } catch (NullPointerException e) {
            fatview.setText("0");
            musclesview.setText("0");
            weightview.setText("0");
        }
        modell = ViewModelProviders.of((FragmentActivity) getContext()).get(ScaleModel.class);
        try {
            modell.getName().observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                    try {
                        Cursor cursor = databaseHelper.getWeightData(s);
                        if (cursor != null) {
                            if (cursor.moveToLast()) {
                                tvUpdateTimeWeightData.setText(cursor.getString(5));
                                tvRemarksWeight.setText(cursor.getString(4));
                                fatview.setText(cursor.getString(3));
                                musclesview.setText(cursor.getString(2));
                                weightview.setText(cursor.getString(1));
                            }
                        } else {
                            fatview.setText("0");
                            musclesview.setText("0");
                            weightview.setText("0");
                        }
                    } catch (NullPointerException e) {
                        fatview.setText("0");
                        musclesview.setText("0");
                        weightview.setText("0");
                    }
                }
            });
            stepModel= ViewModelProviders.of(this).get(StepModel.class);
            stepModel.getStep().observe((LifecycleOwner) getContext(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    steptext.setText(s);
                }
            });
            datamodel = ViewModelProviders.of((FragmentActivity) getContext()).get(Datamodel.class);
            datamodel.getFat().observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    fatview.setText(s);
                }
            });
            datamodel.getMuscle().observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {

                    musclesview.setText(s);
                    // scaleDataModel.setMuscle(s);
                }
            });
            datamodel.getWeight().observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    weightview.setText(s);
                    //scaleDataModel.setWeight(s);
                }
            });
        } catch (Exception e) {
        }
    }

    public String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
        Date date = new Date();
        return dateFormat.format(date);
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
        if (model == null || model.isEmptyData()) {
            toast("不能上传空数据");
            return;
        }
        HmLoadDataTool.getInstance().uploadData(DataFile.DATA_SPO2, model);
    }

    @Override
    public void onSpO2Result(int bloodOxygen, int heartrate) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        String remarks = "Great";
        boolean result = databaseHelper.addMeasurementHeart(username,Integer.toString(heartrate),Integer.toString(bloodOxygen),remarks,currentDateAndTime);  // here we are getting new data of heart
        if (!result)
            Toast.makeText(getContext(),"Error in submitting data to database", Toast.LENGTH_LONG).show();
        model.setValue(bloodOxygen);
        model.setHr(heartrate);
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

    @NonNull
    @Override
    protected ViewDataBinding onCreateBindingView(LayoutInflater inflater,
                                                  @Nullable ViewGroup container,
                                                  @Nullable Bundle savedInstanceState) {
        FragmentTodayBinding binding = setBindingContentView(inflater, R.layout.fragment_today, container);
        binding.setContent(TodayFragment.this);
        oxWave = new PPGDrawWave();
        this.btnMeasure = binding.btnMeasuree;
        model = new SpO2();
        binding.setModel(model);
        return binding;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mHcService != null) {
            mOxTask = mHcService.getBleDevManager().getOxTask();
            mOxTask.setOnSpO2ResultListener(this);
        } else {
            try {
                MonitorDataTransmissionManager.getInstance().setOnSpO2ResultListener(this);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onServiceBind() {
        MonitorDataTransmissionManager.getInstance().setScanDevNamePrefixWhiteList(R.array.health_monitor_dev_name_prefixes);
    }

    @Override
    public void onServiceUnbind() {

    }
}

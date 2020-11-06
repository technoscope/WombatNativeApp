package com.example.wombatapp.addmeasurement;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wombatapp.R;
import com.example.wombatapp.databinding.FragmentECGBinding;
import com.example.wombatapp.minttihealth.health.AlertDialogBuilder;
import com.example.wombatapp.minttihealth.health.App;
import com.example.wombatapp.minttihealth.health.MeasureFragment2;
import com.example.wombatapp.minttihealth.health.WaveSurfaceView;
import com.example.wombatapp.minttihealth.health.bean.ECG;
import com.example.wombatapp.minttihealth.health.bean.UserProfile;
import com.example.wombatapp.minttihealth.health.wave.ECGDrawWave;
import com.linktop.MonitorDataTransmissionManager;
import com.linktop.constant.IUserProfile;
import com.linktop.infs.OnEcgResultListener;
import com.linktop.whealthService.MeasureType;
import com.linktop.whealthService.task.EcgTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import lib.linktop.obj.DataFile;
import lib.linktop.sev.HmLoadDataTool;

public class ECGFragment extends MeasureFragment2 implements OnEcgResultListener {
    private ECG model;
    private final ObservableField<String> pagerSpeedStr = new ObservableField<>();
    private final ObservableField<String> gainStr = new ObservableField<>();
    private final StringBuilder ecgWaveBuilder = new StringBuilder();
    private EcgTask mEcgTask;
    private WaveSurfaceView waveView;
    private ECGDrawWave ecgDrawWave;
    public ECGFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getTitle() {
        return 0;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean startMeasure() {
        if (mEcgTask != null) {
            if (mEcgTask.isModuleExist()) {
                mEcgTask.initEcgTg();
                mEcgTask.start();
                return true;
            } else {
                toast("This Device's ECG module is not exist.");
                return false;
            }
        } else {
            if (MonitorDataTransmissionManager.getInstance().isEcgModuleExist()) {
                MonitorDataTransmissionManager.getInstance().startMeasure(MeasureType.ECG);
                return true;
            } else {
                toast("This Device's ECG module is not exist.");
                return false;
            }
        }
    }

    @Override
    public void stopMeasure() {
        if (mEcgTask != null) {
            mEcgTask.stop();
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
        HmLoadDataTool.getInstance().uploadData(DataFile.DATA_ECG, model);
    }

    @Override
    protected ViewDataBinding onCreateBindingView(@Nullable LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentECGBinding binding = setBindingContentView(inflater, R.layout.fragment_e_c_g, container);
        binding.setContent(this);
        binding.setShowUpload(App.isShowUploadButton);
        this.btnMeasure = binding.btnMeasure;
        model = new ECG();
        binding.setModel(model);
        ecgDrawWave = new ECGDrawWave();
        ecgDrawWave.setPagerSpeed(ECGDrawWave.PagerSpeed.VAL_25MM_PER_SEC);
        ecgDrawWave.setGain(ECGDrawWave.Gain.VAL_10MM_PER_MV);
        waveView = binding.waveView;
        waveView.setDrawWave(ecgDrawWave);
        binding.setPagerSpeedStr(pagerSpeedStr);
        binding.setGainStr(gainStr);
        pagerSpeedStr.set(ecgDrawWave.getPagerSpeed().getDesc());
        gainStr.set(ecgDrawWave.getGain().getDesc());
        /**
         * Whether the ECG automatically ends the measurement.
         * @return true. It means ECG will keep measuring until all results has value.
         * @return false. It means ECG will keep measuring until you call stop api.
         */
        if (mEcgTask != null) {
            binding.sw.setChecked(mEcgTask.isAutoEnd());
        } else {
            try {
                binding.sw.setChecked(MonitorDataTransmissionManager.getInstance().isEcgAutoEnd());
            } catch (Exception e) {

            }
        }
        binding.sw.setOnCheckedChangeListener((compoundButton, autoEnd) -> {
            /**
             * Set ECG to automatically end the measurement.
             * Default is “true”. If you do not want to automatically end the measurement,
             * set it “false”.
             */
            if (mEcgTask != null) {
                mEcgTask.setAutoEnd(autoEnd);
            } else {
                MonitorDataTransmissionManager.getInstance().setEcgAutoEnd(autoEnd);
            }
        });
        return binding;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IUserProfile userProfile = new UserProfile("ccl", 1, 27, 170, 60);
        if (mHcService != null) {
            mEcgTask = mHcService.getBleDevManager().getEcgTask();
            mEcgTask.setOnEcgResultListener(this);
            //Import user profile makes the result more accurate.
            mEcgTask.setUserProfile(userProfile);
        } else {
            MonitorDataTransmissionManager.getInstance().setUserProfile(userProfile);
            MonitorDataTransmissionManager.getInstance().setOnEcgResultListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        waveView.reply();
    }

    @Override
    public void onPause() {
        super.onPause();
        waveView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void reset() {
        model.reset();
        ecgWaveBuilder.setLength(0);
        ecgDrawWave.clear();
    }

    long startTs = 0L;
    int i = 0;

    /*
     * 心电图数据点
     * */
    @Override
    public synchronized void onDrawWave(int wave) {
//        i++;
//        if (startTs == 0L) startTs = System.currentTimeMillis();
        //将数据点在心电图控件里描绘出来
        ecgDrawWave.addData((float) wave);
        //将数据点存入容器，查看大图使用
        ecgWaveBuilder.append(wave).append(",");
    }

    @Override
    public void onSignalQuality(int quality) {
        Log.e("ECG", "onSignalQuality:" + quality);
    }

    @Override
    public void onECGValues(int key, int value) {
        switch (key) {
            case RRI_MAX:
                model.setRRMax(value);
                break;
            case RRI_MIN:
                model.setRRMin(value);
                break;
            case HR:
                model.setHr(value);
                break;
            case HRV:
                model.setHrv(value);
                break;
            case MOOD:
                model.setMood(value);
                break;
            case RR://Respiratory rate.
                model.setRr(value);
                break;
        }
    }

    /*
     * 心电图测量持续时间,该回调一旦触发说明一次心电图测量结束
     * */
    @Override
    public void onEcgDuration(long duration) {
        final long l = (System.currentTimeMillis() - startTs) / 1000L;
        startTs = 0L;
        i = 0;
        model.setDuration(duration);
        model.setTs(System.currentTimeMillis() / 1000L);
        String ecgWave = ecgWaveBuilder.toString();
        ecgWave = ecgWave.substring(0, ecgWave.length() - 1);
        model.setWave(ecgWave);
        resetState();
    }
/*
    public void openECGLarge(View v) {
        Intent intent = new Intent(mActivity, ECGLargeActivity.class);
        intent.putExtra("pagerSpeed", ecgDrawWave.getPagerSpeed());
        intent.putExtra("gain", ecgDrawWave.getGain());
        intent.putExtra("model", model);
        startActivity(intent);
    }
*/
    /*
     * 点击设置时间基准(走纸速度)
     * 该值反应心电图x轴的幅度，设置的值这里没做保存，请自行保存，以便下次启动该页面时自动设置已保存的值
     * */
    public void clickSetPagerSpeed(View v) {
        int checkedItem = 0;
        final ECGDrawWave.PagerSpeed[] pagerSpeeds = ECGDrawWave.PagerSpeed.values();
        for (int i = 0; i < pagerSpeeds.length; i++) {
            if (pagerSpeeds[i].equals(ecgDrawWave.getPagerSpeed())) {
                checkedItem = i;
                break;
            }
        }
        onShowSingleChoiceDialog(R.string.pager_speed, pagerSpeeds, checkedItem
                , (dialog, which) -> {
                    ECGDrawWave.PagerSpeed pagerSpeed = pagerSpeeds[which];
                    ecgDrawWave.setPagerSpeed(pagerSpeed);
                    pagerSpeedStr.set(pagerSpeed.getDesc());
                    dialog.dismiss();
                });
    }

    /*
     * 点击设置增益
     * 该值反应心电图y轴的幅度，设置的值这里没做保存，请自行保存，以便下次启动该页面时自动设置已保存的值
     * */
    public void clickSetGain(View v) {
        int checkedItem = 0;
        final ECGDrawWave.Gain[] gains = ECGDrawWave.Gain.values();
        for (int i = 0; i < gains.length; i++) {
            if (gains[i].equals(ecgDrawWave.getGain())) {
                checkedItem = i;
                break;
            }
        }
        onShowSingleChoiceDialog(R.string.gain, gains, checkedItem
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ECGDrawWave.Gain gain = gains[which];
                        ecgDrawWave.setGain(gain);
                        gainStr.set(gain.getDesc());
                        dialog.dismiss();
                    }
                });
    }

    private void onShowSingleChoiceDialog(@StringRes int titleResId, ECGDrawWave.ECGValImp[] arrays
            , int checkedItem, DialogInterface.OnClickListener listener) {
        int length = arrays.length;
        CharSequence[] items = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            items[i] = arrays[i].getDesc();
        }
        new AlertDialogBuilder(mActivity2)
                .setTitle(titleResId)
                .setSingleChoiceItems(items, checkedItem, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
}

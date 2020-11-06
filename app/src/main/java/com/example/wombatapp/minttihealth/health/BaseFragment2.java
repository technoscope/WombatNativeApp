package com.example.wombatapp.minttihealth.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wombatapp.addmeasurement.AddMeasurementActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * Created by ccl on 2017/2/7.
 */
public abstract class BaseFragment2 extends Fragment {
    //it should be add
    protected AddMeasurementActivity mActivity2;
    protected HcService mHcService;

    public BaseFragment2() {
    }

    @StringRes
    public abstract int getTitle();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateBindingView(inflater, container, savedInstanceState).getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity2= (AddMeasurementActivity) getActivity();
        if (!App.isUseCustomBleDevService)
            mHcService=mActivity2.mHcService;
    }


    protected ViewDataBinding onCreateBindingView(@Nullable LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    protected <T extends ViewDataBinding> T setBindingContentView(LayoutInflater inflater
            , int layoutId, @Nullable ViewGroup parent) {
        return DataBindingUtil.inflate(inflater, layoutId, parent, false);
    }

    public abstract void reset();


    protected void toast(@NonNull String text) {
        Toast.makeText(mActivity2, text, Toast.LENGTH_SHORT).show();
    }

    protected void toast(@StringRes int text) {
        Toast.makeText(mActivity2, text, Toast.LENGTH_SHORT).show();
    }

}

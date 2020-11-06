package com.example.wombatapp.minttihealth.health.bean;

import com.example.wombatapp.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import lib.linktop.obj.LoadBgBean;

/**
 * Created by ccl on 2018/1/25.
 */

public class Bg extends BaseObservable implements LoadBgBean {

    private long ts = 0;
    private double value = 0.0d;

    public Bg() {
    }

    @Override
    public long getTs() {
        return ts;
    }

    @Override
    public void setTs(long ts) {
        this.ts = ts;
    }

    @Bindable
    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        if(this.value!=value) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
    }

    public void reset() {
        value = 0.0d;
        ts = 0L;
        notifyChange();
    }

    public boolean isEmptyData() {
        return value == 0.0d || ts == 0L;
    }

    @Override
    public String toString() {
        return "Bt{" +
                "ts=" + ts +
                ", value=" + value +
                '}';
    }
}

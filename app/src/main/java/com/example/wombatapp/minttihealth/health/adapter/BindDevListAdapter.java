package com.example.wombatapp.minttihealth.health.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.example.wombatapp.BR;
import com.example.wombatapp.R;
import com.example.wombatapp.minttihealth.health.adapter.vh.BindingViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import lib.linktop.common.CssSubscriber;
import lib.linktop.obj.Device;
import lib.linktop.sev.CssServerApi;

/**
 * Created by ccl on 2017/3/9.
 */

public class BindDevListAdapter extends DataBindingAdapter<Device> {

    private final ObservableField<String> id;

    public BindDevListAdapter(Context context, ObservableField<String> id) {
        super(context, R.layout.item_bind_dev);
        this.id = id;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        super.onBindViewHolder(holder, position);
        holder.binding.setVariable(BR.id, id);
        holder.binding.setVariable(BR.clickGetDevActiveInfo, (View.OnClickListener) v -> {
            final String devId = getItem(position).getDevId();

            CssServerApi.getDevActiveInfo(devId)
                    .subscribe(new CssSubscriber<String[]>() {
                        @Override
                        public void onNextRequestSuccess(String[] strings) {
                            final String s = "Device key:" + strings[0] + ",\nQR code:" + strings[1];
                            //Toast.makeText().show(v.getContext(), s);
                        }

                        @Override
                        public void onNextRequestFailed(int status) {

                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        });
    }
}

package com.example.wombatapp.minttihealth.health.binding;

import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ccl on 2017/2/8.
 * DataBinding 的BindingAdapter类
 */

public class DataBindingHelper {

    @BindingAdapter("recyclerAdapter")
    public static void setRecyclerAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        if (adapter != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
    }


    @BindingAdapter("addTextChangeListener")
    public static void addTextChangeListener(TextView view, TextWatcher watcher) {
        view.addTextChangedListener(watcher);
    }

    @BindingAdapter("onViewClick")
    public static void setOnViewClick(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }
}

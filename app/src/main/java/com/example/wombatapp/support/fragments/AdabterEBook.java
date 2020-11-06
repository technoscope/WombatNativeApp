package com.example.wombatapp.support.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wombatapp.R;

import java.util.ArrayList;

public class AdabterEBook extends BaseAdapter {
    ArrayList<String> booknames;
    Context context;
    LayoutInflater inflator;

    public AdabterEBook(Context context, ArrayList<String> booknames) {
        this.context = context;
        this.booknames = booknames;
    }

    @Override
    public int getCount() {
        return booknames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView bookname;

        if (inflator == null) {
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null) {
            view = inflator.inflate(R.layout.ebookview, null);
        }
        bookname = view.findViewById(R.id.id_bookname);
        bookname.setText(booknames.get(i));
        return view;
    }
}

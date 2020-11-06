package com.example.wombatapp.support.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wombatapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class eBOOKFragment extends Fragment {
    GridView gridView;
    AdabterEBook adabterEBook;
    ArrayList<String> booknames;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_b_o_o_k, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view.findViewById(R.id.id_gridview);
        booknames=new ArrayList<>();
        booknames.add("The 28 Day Diet and Excercise Plan");
        booknames.add("The 28 Day Diet and Excercise Plan 2");
        booknames.add("The 28 Day Diet and Excercise Plan 3");
        booknames.add("Diet and Excercise");
        booknames.add("Excercise Plan");
        booknames.add("Breathing and Guided Meditation");
        booknames.add("The 28 Day Diet");
        booknames.add("The 28 Day Diet 2");
        booknames.add("Excercise Plan 2");
        booknames.add("Plan 2");
        booknames.add("Plan 9");
        booknames.add("Excercise Plan 2");
        booknames.add("Diet and Excercise Plan");
        booknames.add("The 28 Day");
        booknames.add("The 28 Day 2");
        adabterEBook=new AdabterEBook(getContext(),booknames);
        gridView.setAdapter(adabterEBook);
    }
}
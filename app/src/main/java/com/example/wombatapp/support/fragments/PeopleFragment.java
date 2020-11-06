package com.example.wombatapp.support.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wombatapp.R;
import com.example.wombatapp.support.SupportAdabter;
import com.example.wombatapp.support.peopemodel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PeopleFragment extends Fragment {
    RecyclerView recyclerView;
    SupportAdabter adabter;
    peopemodel model;
    ArrayList<peopemodel> peoplelist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleview_people);
        peoplelist=new ArrayList<>();
        model=new peopemodel();
        model.setName("Dr. Diana Leibnitz");
        model.setChargespersession("€ 80 per session");
        model.setDescription("As a trained counsellor with over 7 years’ experience, I support clients in overcoming challenges and building resilience in a safe, professional setting. I also offer sessions online.");
        model.setType("Family Physician");
        peoplelist.add(model);
        model=new peopemodel();
        model.setName("Dr. Ellie Goldsmith");
        model.setChargespersession("€ 70 per session");
        model.setDescription("As a trained counsellor with over 7 years’ experience, I support clients in overcoming challenges and building resilience in a safe, professional setting. I also offer sessions online.");
        model.setType("Physician");
        peoplelist.add(model);
        adabter = new SupportAdabter(getContext(), peoplelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adabter);
    }
}
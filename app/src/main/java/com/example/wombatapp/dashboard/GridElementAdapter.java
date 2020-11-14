package com.example.wombatapp.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wombatapp.MainActivity;
import com.example.wombatapp.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridElementAdapter extends RecyclerView.Adapter<GridElementAdapter.SimpleViewHolder> {


    private Context context;
    private ArrayList<String> elements;

    public GridElementAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.elements = arrayList;
    }

    @Override
    public void onBindViewHolder(@NonNull final GridElementAdapter.SimpleViewHolder holder, final int position) {
        holder.username.setText(elements.get(position));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("username", elements.get(position));
                context.startActivity(intent);
            }
        });

//        holder.circularProgressBar1.setProgressWithAnimation(30f, (long) 6000); // =6s
//        holder.circularProgressBar2.setProgressWithAnimation(50f, (long) 6000); // =6s
//        holder.circularProgressBar3.setProgressWithAnimation(84f, (long) 6000); // =6s


        holder.circularProgressBar1.setProgress(30f);
        holder.circularProgressBar2.setProgress(50f);
        holder.circularProgressBar3.setProgress(84f);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.size();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.grid_element, parent, false);
        return new SimpleViewHolder(view);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView username;
        public final LinearLayout linearLayout;
        CircularProgressBar circularProgressBar1, circularProgressBar2, circularProgressBar3;

        public SimpleViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            linearLayout = itemView.findViewById(R.id.user_container);
            circularProgressBar1 = view.findViewById(R.id.circularProgressBar1);
            circularProgressBar2 = view.findViewById(R.id.circularProgressBar2);
            circularProgressBar3 = view.findViewById(R.id.circularProgressBar3);

        }
    }


}

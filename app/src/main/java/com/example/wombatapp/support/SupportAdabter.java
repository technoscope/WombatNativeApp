package com.example.wombatapp.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wombatapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SupportAdabter extends RecyclerView.Adapter<SupportAdabter.ViewHolder> {
    Context context;
    ArrayList<peopemodel> peoplelist;

    public SupportAdabter(Context context, ArrayList<peopemodel> peoplelist) {
        this.context = context;
        this.peoplelist = peoplelist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.supportview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fees.setText(String.valueOf(peoplelist.get(position).getChargespersession()));
        holder.description.setText(String.valueOf(peoplelist.get(position).getDescription()));
        holder.type.setText(String.valueOf(peoplelist.get(position).getType()));
        holder.name.setText(String.valueOf(peoplelist.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return peoplelist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,type,description,fees;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.id_name);
            type=itemView.findViewById(R.id.id_type);
            description=itemView.findViewById(R.id.id_description);
            fees=itemView.findViewById(R.id.id_fees);
            imageView=itemView.findViewById(R.id.id_img);

        }
    }
}

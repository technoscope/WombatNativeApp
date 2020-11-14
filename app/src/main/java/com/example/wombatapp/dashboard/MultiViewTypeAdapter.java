package com.example.wombatapp.dashboard;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wombatapp.R;
import com.example.wombatapp.model.UserModalClass;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class MultiViewTypeAdapter extends RecyclerView.Adapter {

    private ArrayList<UserModalClass> dataSet;
    Context mContext;
    int total_types;

    public static class FamilyTypeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lyFamily;

        public FamilyTypeViewHolder(View itemView) {
            super(itemView);

            this.lyFamily = itemView.findViewById(R.id.ly_family);

        }
    }

    public static class UserTypeViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlUser;
        ImageView userIcon;
        TextView userName;
        CircularProgressBar circularProgressBar1;
        CircularProgressBar circularProgressBar2;
        CircularProgressBar circularProgressBar3;

        public UserTypeViewHolder(View itemView) {
            super(itemView);

            this.rlUser = itemView.findViewById(R.id.rl_user);
            this.userIcon = itemView.findViewById(R.id.gridimg);
            this.userName = itemView.findViewById(R.id.username);
            this.circularProgressBar1 = itemView.findViewById(R.id.circularProgressBar1);
            this.circularProgressBar2 = itemView.findViewById(R.id.circularProgressBar2);
            this.circularProgressBar3 = itemView.findViewById(R.id.circularProgressBar3);

        }
    }

    public static class AddNewTypeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lyAddNew;

        public AddNewTypeViewHolder(View itemView) {
            super(itemView);

            this.lyAddNew = itemView.findViewById(R.id.ly_add_new);
        }
    }

    public MultiViewTypeAdapter(Context context, ArrayList<UserModalClass>data) {
        this.dataSet = data;
        this.mContext = context;
        total_types = 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blueprint_family, parent, false);
                return new FamilyTypeViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_element, parent, false);
                return new UserTypeViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blueprint_add, parent, false);
                return new AddNewTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        UserModalClass object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.getViewType()) {
                case 0:
                    ((FamilyTypeViewHolder) holder).lyFamily.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // todo click listener for family
                        }
                    });

                    break;
                case 1:
                    ((UserTypeViewHolder) holder).userName.setText(object.getUserName());
                    ((UserTypeViewHolder) holder).rlUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // todo click listener for user
//                            Intent intent = new Intent(mContext, MainActivity.class);
//                            intent.putExtra("username", elements.get(position));
//                            context.startActivity(intent);

                            int dummyProgress1 = (int) (Math.random() * (100 - 5 + 1) + 5+0f);
                            ((UserTypeViewHolder) holder).circularProgressBar1.setProgressWithAnimation(dummyProgress1, Long.valueOf(2000));

                            int dummyProgress2 = (int) (Math.random() * (100 - 5 + 1) + 5+0f);
                            ((UserTypeViewHolder) holder).circularProgressBar2.setProgressWithAnimation(dummyProgress2, Long.valueOf(2000));

                            int dummyProgress3 = (int) (Math.random() * (100 - 5 + 1) + 5+0f);
                            ((UserTypeViewHolder) holder).circularProgressBar3.setProgressWithAnimation(dummyProgress3, Long.valueOf(2000));
                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Glide.with(mContext).load(mContext.getDrawable(object.getIconId())).into(((UserTypeViewHolder) holder).userIcon);
                    }

                    ((UserTypeViewHolder) holder).circularProgressBar1.setProgress(30f);
                    ((UserTypeViewHolder) holder).circularProgressBar2.setProgress(50f);
                    ((UserTypeViewHolder) holder).circularProgressBar3.setProgress(84f);

                    break;
                case 2:
                    ((AddNewTypeViewHolder) holder).lyAddNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // todo click listener for user
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

package com.example.wombatapp.AddNewUser;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wombatapp.R;
import com.example.wombatapp.dashboard.DashboardActivity;
import com.example.wombatapp.dashboard.MultiViewTypeAdapter;
import com.example.wombatapp.database.DatabaseHelper;
import com.example.wombatapp.minttihealth.health.adapter.CustomRecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class AddNewUser extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String userIconId="" ;
    String genderValue="";
    String myAge="";
    TextView age, height, name;
    TextView male, female, unspecified;
    RelativeLayout lyMale, lyFemale, lyUnspecified;
    TextView adduserbtn;
    //  DatabaseReference reference;
    UserDetailModel usermodel;
    CheckBox cbFitandactive, cbMorerelaxation, cbFatloss, cbBettersleep, cbStrength, cbFocusproductivity;
    ImageView ivCalendar;
    DatePickerDialog datePickerDialog;
    ArrayList<Integer> iconsList = new ArrayList<>();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        male = findViewById(R.id.male_gender);
        female = findViewById(R.id.female_gender);
        unspecified = findViewById(R.id.unspecified_gender);

        lyMale = findViewById(R.id.ly_male);
        lyFemale = findViewById(R.id.ly_female);
        lyUnspecified = findViewById(R.id.ly_unspecified);

        ivCalendar = findViewById(R.id.iv_calendar);

        name = findViewById(R.id.id_namee);
        age = findViewById(R.id.id_age);
        height = findViewById(R.id.id_input_height);
        adduserbtn = findViewById(R.id.id_btn_add);
        cbFitandactive = findViewById(R.id.id_fit_and_active);
        cbMorerelaxation = findViewById(R.id.id_more_relaxation);
        cbFatloss = findViewById(R.id.id_fat_loss);
        cbBettersleep = findViewById(R.id.id_better_sleep);
        cbStrength = findViewById(R.id.id_strength);
        cbFocusproductivity = findViewById(R.id.id_focus_productivity);

        setRecyclerView();

        final DatabaseHelper dbh = new DatabaseHelper(AddNewUser.this);
        //  try {
        //   String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // reference = FirebaseDatabase.getInstance().getReference(userid);
        //}catch (Exception e){}

        Date date = new Date();
        datePickerDialog = new DatePickerDialog(
                AddNewUser.this, AddNewUser.this, date.getYear(), date.getMonth(), date.getDay());

        adduserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName =name.getText().toString();
                String myHeight = height.getText().toString();

                int random_int = (int)(Math.random() * (9 - 0 + 1) + 0);
                userIconId = iconsList.get(random_int) + ""; // todo use proper logic
                String personalGoalsList = "";
                int goalsCount = 0;

                if(genderValue.equals("")){
                    Toast.makeText(AddNewUser.this,"Please select gender",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myName.trim().equals("")){
                    Toast.makeText(AddNewUser.this,"Please enter your name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myAge.trim().equals("")){
                    Toast.makeText(AddNewUser.this,"Please enter your date of birth",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myHeight.trim().equals("")){
                    Toast.makeText(AddNewUser.this,"Please enter your height",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cbFitandactive.isChecked()){
                    personalGoalsList = cbFitandactive.getText().toString() + ",";
                    goalsCount++;
                }

                if(cbMorerelaxation.isChecked()){
                    personalGoalsList = cbMorerelaxation.getText().toString() + ",";
                    goalsCount++;
                }

                if(cbFatloss.isChecked()){
                    personalGoalsList = cbFatloss.getText().toString() + ",";
                    goalsCount++;
                }

                if(cbBettersleep.isChecked()){
                    personalGoalsList = cbBettersleep.getText().toString() + ",";
                    goalsCount++;
                }

                if(cbStrength.isChecked()){
                    personalGoalsList = cbStrength.getText().toString() + ",";
                    goalsCount++;
                }

                if(cbFocusproductivity.isChecked()){
                    personalGoalsList = cbFocusproductivity.getText().toString() + ",";
                    goalsCount++;
                }

                if(goalsCount>3){
                    Toast.makeText(AddNewUser.this,"Sorry you can only select 3 goals",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(personalGoalsList.equals("")){
                    Toast.makeText(AddNewUser.this,"Please select personal goals",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbh.addUser(myName, genderValue, myAge, myHeight, personalGoalsList, userIconId)) {
                    usermodel = new UserDetailModel();
                    usermodel.setUsername(name.getText().toString());
                    usermodel.setDateofbirth(age.getText().toString());
                    usermodel.setHeight(height.getText().toString());
                    usermodel.setGender(genderValue);
                    usermodel.setGoal1(personalGoalsList);
                    usermodel.setUserIconId(userIconId);
                    // reference.child("UserDetail").setValue(usermodel);
                    startActivity(new Intent(AddNewUser.this, DashboardActivity.class));
                }
            }
        });

        ivCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        setGenderListener();
    }

    private void setGenderListener() {
        lyMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedGender(male.getId());
            }
        });

        lyFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedGender(female.getId());
            }
        });

        lyUnspecified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedGender(unspecified.getId());
            }
        });
    }

    private void changeSelectedGender(int id) {

        switch (id) {
            case R.id.male_gender:
                male.setVisibility(View.VISIBLE);
                female.setVisibility(View.GONE);
                unspecified.setVisibility(View.GONE);
                genderValue = "male";
                break;
            case R.id.female_gender:
                female.setVisibility(View.VISIBLE);
                male.setVisibility(View.GONE);
                unspecified.setVisibility(View.GONE);
                genderValue = "female";
                break;
            case R.id.unspecified_gender:
                unspecified.setVisibility(View.VISIBLE);
                male.setVisibility(View.GONE);
                female.setVisibility(View.GONE);
                genderValue = "unspecified";
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddNewUser.this, DashboardActivity.class));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myAge = dayOfMonth + "/" + month + "/" + year;
        age.setText(myAge);
    }

    private void setRecyclerView() {
        iconsList.add(R.drawable.icon_bee);
        iconsList.add(R.drawable.icon_badger);
        iconsList.add(R.drawable.icon_bear);
        iconsList.add(R.drawable.icon_fennec);
        iconsList.add(R.drawable.icon_frog);
        iconsList.add(R.drawable.icon_hyena);
        iconsList.add(R.drawable.icon_panda);
        iconsList.add(R.drawable.icon_penguin);
        iconsList.add(R.drawable.icon_tarsier);
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.addItemDecoration(new MemberItemDecoration());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<AddNewUser.ViewHolderRt> adapter = new RecyclerView.Adapter<AddNewUser.ViewHolderRt>() {
            @NonNull
            @Override
            public AddNewUser.ViewHolderRt onCreateViewHolder(@NonNull ViewGroup viewGroup, int ViewType) {
                return new AddNewUser.ViewHolderRt(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blueprint_add_user_icons, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull AddNewUser.ViewHolderRt viewHolderRt, final int i) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Glide.with(AddNewUser.this).load(getDrawable(iconsList.get(i))).into(viewHolderRt.imageViewIcon);
                }

            }

            @Override
            public int getItemCount() {
                return iconsList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private class ViewHolderRt extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;

        public ViewHolderRt(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.iv_icon);

        }
    }

    public class MemberItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // only for the last one
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 280/* set your margin here */;
            } else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount()-1) {
                outRect.right = 280/* set your margin here */;
            }
        }
    }
}
package com.example.wombatapp.AddNewUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.wombatapp.R;
import com.example.wombatapp.dashboard.DashboardActivity;
import com.example.wombatapp.database.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;

import info.hoang8f.android.segmented.SegmentedGroup;

public class AddNewUser extends AppCompatActivity {
    String userIconId = "1";
    RadioButton male, female, rbUnspecified;
    String genderValue;
    TextView age, height, name;
    Button adduserbtn;
    //  DatabaseReference reference;
    UserDetailModel usermodel;
    CheckBox cbFitandactive, cbMorerelaxation, cbFatloss, cbBettersleep, cbStrength, cbFocusproductivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        final SegmentedGroup segmented2 = findViewById(R.id.segmented2);
        name = findViewById(R.id.id_namee);
        male = findViewById(R.id.button21);
        female = findViewById(R.id.button22);
        rbUnspecified = findViewById(R.id.button23);
        age = findViewById(R.id.id_age);
        height = findViewById(R.id.id_input_height);
        adduserbtn = findViewById(R.id.id_btn_add);
        cbFitandactive = findViewById(R.id.id_fit_and_active);
        cbMorerelaxation = findViewById(R.id.id_more_relaxation);
        cbFatloss = findViewById(R.id.id_fat_loss);
        cbBettersleep = findViewById(R.id.id_better_sleep);
        cbStrength = findViewById(R.id.id_strength);
        cbFocusproductivity = findViewById(R.id.id_focus_productivity);
        age.getText().toString();
        height.getText().toString();
        final DatabaseHelper dbh = new DatabaseHelper(AddNewUser.this);
        //  try {
        //   String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // reference = FirebaseDatabase.getInstance().getReference(userid);
        //}catch (Exception e){}
        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.button21:
                        genderValue = "male";
                        break;
                    case R.id.button22:
                        genderValue = "female";
                        break;
                    case R.id.button23:
                        genderValue = "unspecified";
                        break;
                }
            }
        });

        adduserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personalGoalsList = null;
                if (cbFatloss.isChecked() || cbBettersleep.isChecked() || cbStrength.isChecked() ||
                        cbFocusproductivity.isChecked() || cbFitandactive.isChecked() || cbMorerelaxation.isChecked()) {
                    if (cbFatloss.isChecked()) {
                        personalGoalsList = cbFatloss.getText().toString() + ", ";
                    } else if (cbBettersleep.isChecked() && usermodel.getGoal1().isEmpty()) {
                        personalGoalsList = cbBettersleep.getText().toString() + ", ";
                    } else if (cbStrength.isChecked()) {
                        personalGoalsList = cbStrength.getText().toString() + ", ";
                    } else if (cbFocusproductivity.isChecked()) {
                        personalGoalsList = cbFocusproductivity.getText().toString() + ", ";
                    } else if (cbFitandactive.isChecked()) {
                        personalGoalsList = cbFitandactive.getText().toString() + ", ";
                    }


                }
                if (dbh.addUser(name.getText().toString(), genderValue, age.getText().toString(), height.getText().toString(), personalGoalsList, userIconId)) {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddNewUser.this, DashboardActivity.class));
    }
}

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
    RadioButton male, female;
    String m, f;
    TextView age, height, name;
    Button adduserbtn;
  //  DatabaseReference reference;
    UserDetailModel usermodel;
    CheckBox fitandactive,morerelaxation,fatloss,bettersleep,strength,focusproductivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        final SegmentedGroup segmented2 = findViewById(R.id.segmented2);
        name = findViewById(R.id.id_namee);
        male = findViewById(R.id.button21);
        female = findViewById(R.id.button22);
        age = findViewById(R.id.id_age);
        height = findViewById(R.id.id_input_height);
        adduserbtn = findViewById(R.id.id_btn_add);
        fitandactive=findViewById(R.id.id_fit_and_active);
        morerelaxation=findViewById(R.id.id_more_relaxation);
        fatloss=findViewById(R.id.id_fat_loss);
        bettersleep=findViewById(R.id.id_better_sleep);
        strength=findViewById(R.id.id_strength);
        focusproductivity=findViewById(R.id.id_focus_productivity);
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
                   if (group.getCheckedRadioButtonId() == R.id.button21) {
                       m = male.getText().toString();
                       f = null;
                   }
                   if (group.getCheckedRadioButtonId() == R.id.button22) {
                       f = female.getText().toString();
                       m = null;
                   }
               }
           });

        adduserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f != null) {
                    if (dbh.adduser(name.getText().toString(), f, age.getText().toString(), height.getText().toString())) {
                        usermodel=new UserDetailModel();
                        usermodel.setUsername(name.getText().toString());
                        usermodel.setDateofbirth(age.getText().toString());
                        usermodel.setHeight(height.getText().toString());
                        usermodel.setGender(f);
                        if(fatloss.isChecked() || bettersleep.isChecked() || strength.isChecked() ||
                           focusproductivity.isChecked() || fitandactive.isChecked() ||  morerelaxation.isChecked()){
                            if(fatloss.isChecked()){
                                usermodel.setGoal1(fatloss.getText().toString());
                            }else if(bettersleep.isChecked() && usermodel.getGoal1().isEmpty()){
                                usermodel.setGoal1(bettersleep.getText().toString());
                            }else if(strength.isChecked()){
                                usermodel.setGoal2(strength.getText().toString());
                            }else if(focusproductivity.isChecked()){
                                usermodel.setGoal3(focusproductivity.getText().toString());
                            }else if(fitandactive.isChecked()){
                                usermodel.setGoal2(fitandactive.getText().toString());
                            }

                        }
    //                    reference.child("UserDetail").setValue(usermodel);
                        startActivity(new Intent(AddNewUser.this, DashboardActivity.class));
                    }
                } else if (m != null) {
                    if (dbh.adduser(name.getText().toString(), m, age.getText().toString(), height.getText().toString())) {
                        usermodel=new UserDetailModel();
                        usermodel.setUsername(name.getText().toString());
                        usermodel.setDateofbirth(age.getText().toString());
                        usermodel.setHeight(height.getText().toString());
                        usermodel.setGender(m);
                        if(fatloss.isChecked() || bettersleep.isChecked() || strength.isChecked() ||
                                focusproductivity.isChecked() || fitandactive.isChecked() ||  morerelaxation.isChecked()){
                            if(fatloss.isChecked()){
                                usermodel.setGoal1(fatloss.getText().toString());
                            }else if(bettersleep.isChecked() && usermodel.getGoal1().isEmpty()){
                                usermodel.setGoal1(bettersleep.getText().toString());
                            }else if(strength.isChecked()){
                                usermodel.setGoal2(strength.getText().toString());
                            }else if(focusproductivity.isChecked()){
                                usermodel.setGoal3(focusproductivity.getText().toString());
                            }else if(fitandactive.isChecked()){
                                usermodel.setGoal2(fitandactive.getText().toString());
                            }
                        }
            //           reference.child("UserDetail").setValue(usermodel);
                        startActivity(new Intent(AddNewUser.this, DashboardActivity.class));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddNewUser.this,DashboardActivity.class));
    }
}

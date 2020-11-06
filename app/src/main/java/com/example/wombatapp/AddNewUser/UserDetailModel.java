package com.example.wombatapp.AddNewUser;

public class UserDetailModel {
    public String username;
    public String dateofbirth;
    public String height;
    public String gender;
    public String goal1;
    public String goal2;
    public String goal3;
    public UserDetailModel(){}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGoal1() {
        return goal1;
    }

    public void setGoal1(String goal1) {
        this.goal1 = goal1;
    }

    public String getGoal2() {
        return goal2;
    }

    public void setGoal2(String goal2) {
        this.goal2 = goal2;
    }

    public String getGoal3() {
        return goal3;
    }

    public void setGoal3(String goal3) {
        this.goal3 = goal3;
    }
}

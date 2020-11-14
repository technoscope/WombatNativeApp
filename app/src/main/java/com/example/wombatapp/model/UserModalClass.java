package com.example.wombatapp.model;

public class UserModalClass {
    private int iconId;
    private String userName;
    private String gender;
    private String height;
    private String birthDate;
    private String PersonalGoals;
    private int viewType;


    public UserModalClass(int iconId, String userName, int viewType) {
        this.iconId = iconId;
        this.userName = userName;
        this.viewType = viewType;
    }

    public UserModalClass(int iconId, String userName) {
        this.iconId = iconId;
        this.userName = userName;
    }

    public int getViewType() {
        return viewType;
    }

    public int getIconId() {
        return iconId;
    }

    public String getUserName() {
        return userName;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPersonalGoals() {
        return PersonalGoals;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setPersonalGoals(String personalGoals) {
        PersonalGoals = personalGoals;
    }
}

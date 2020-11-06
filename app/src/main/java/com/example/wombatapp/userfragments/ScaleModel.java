package com.example.wombatapp.userfragments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScaleModel extends ViewModel {
    MutableLiveData<String> name;
    MutableLiveData<String> weight;
    MutableLiveData<String> muscle;
    MutableLiveData<String> fat;
    public ScaleModel() {
        name = new MutableLiveData<>();
        weight = new MutableLiveData<>();
        muscle = new MutableLiveData<>();
        fat = new MutableLiveData<>();
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public MutableLiveData<String> getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight.setValue(weight);
    }

    public MutableLiveData<String> getMuscle() {
        return muscle;
    }

    public void setMuscle(String muscle) {
        this.muscle.setValue(muscle);
    }

    public MutableLiveData<String> getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat.setValue(fat);
    }


}

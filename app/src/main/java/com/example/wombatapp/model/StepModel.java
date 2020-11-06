package com.example.wombatapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StepModel extends ViewModel {
    public StepModel() {
    }

    MutableLiveData<String> step;

    public MutableLiveData<String> getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step.setValue(step);
    }
}

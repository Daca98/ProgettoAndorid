package com.example.vcv.ui.hourMonth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HourMonthViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HourMonthViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is month fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

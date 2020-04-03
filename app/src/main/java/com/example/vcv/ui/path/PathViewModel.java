package com.example.vcv.ui.path;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PathViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PathViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is path fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
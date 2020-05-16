package com.example.vcv.ui.myProfile;

import android.content.Context;

import com.example.vcv.activity.MainActivity;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyProfileViewModel extends ViewModel {

    private MutableLiveData<User> mUser;
    public static Context context;

    public MyProfileViewModel() {
        mUser = new MutableLiveData<>();
        mUser.setValue(getUserFromLocalDB());
    }

    public LiveData<User> getUser() {
        return mUser;
    }

    private User getUserFromLocalDB() {
        return new QueryDB(context).readUser();
    }
}
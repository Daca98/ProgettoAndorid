package com.example.vcv.ui.myProfile;

import android.content.Context;

import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyProfileViewModel extends ViewModel {

    private MutableLiveData<User> mUser;
    public static Context context;
    private static QueryDB db;

    public MyProfileViewModel() {
        mUser = new MutableLiveData<>();
        mUser.setValue(getUserFromLocalDB());
    }

    public LiveData<User> getUser() {
        return mUser;
    }

    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    public void writeNewDataInDB(User user) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            firebaseDatabase.setValue(user);

            db.updateUser(user);
        }
    }
}
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

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class MyProfileViewModel extends ViewModel {

    private MutableLiveData<User> mUser;
    public static Context context;
    private static QueryDB db;

    /**
     * Constructor
     */
    public MyProfileViewModel() {
        mUser = new MutableLiveData<>();
        mUser.setValue(getUserFromLocalDB());
    }

    /**
     * Method to get user data from fragment
     *
     * @return User
     */
    public LiveData<User> getUser() {
        return mUser;
    }

    /**
     * Method to set user data in fragment
     *
     * @return User
     */
    public void setUser(User user) {
        mUser.postValue(user);
    }

    /**
     * Method use to get logged user from local DB
     *
     * @return logged user
     */
    private User getUserFromLocalDB() {
        db = new QueryDB(context);
        return db.readUser();
    }

    /**
     * Method to update the user data in local DB
     *
     * @param user
     */
    public void writeNewDataInDB(User user) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            firebaseDatabase.setValue(user);

            db.updateUser(user);
        }
    }
}
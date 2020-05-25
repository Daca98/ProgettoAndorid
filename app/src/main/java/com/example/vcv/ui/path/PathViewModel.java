package com.example.vcv.ui.path;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vcv.activity.LoginActivity;
import com.example.vcv.utility.PersonalMap;
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PathViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public static PathFragment pathFragment;

    public PathViewModel() {}

    public void downLoadMap(){
        final ArrayList<PersonalMap> maps = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("maps");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PersonalMap map = postSnapshot.getValue(PersonalMap.class);
                    map.path = postSnapshot.getKey();
                    maps.add(map);
                }
                pathFragment.setMenu(maps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("info:", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }
}
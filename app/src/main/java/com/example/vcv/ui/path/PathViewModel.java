package com.example.vcv.ui.path;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.vcv.utility.PersonalMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class PathViewModel extends ViewModel {

    public static PathFragment pathFragment;

    /**
     * Constructor
     */
    public PathViewModel() {}

    /**
     * Method used to download the paths from firebase
     */
    public void downLoadMap(){
        final ArrayList<PersonalMap> maps = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("maps");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             * Handle the snapshot of referenced data. This method is triggered only once
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    PersonalMap map = postSnapshot.getValue(PersonalMap.class);
                    map.path = postSnapshot.getKey();
                    maps.add(map);
                }
                Log.i("MAP_DOWNLOAD", "Retrived paths from firebase successfully");
                pathFragment.setMenu(maps);
            }

            /**
             * Handle the error occured while retriving data. This method is triggered only once
             *
             * @param databaseError
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MAP_DOWNLOAD", "Error to retrive paths from firebase " + databaseError.getMessage());
            }
        });

    }
}
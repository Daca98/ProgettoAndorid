package com.example.vcv.ui.myProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.vcv.ForgotPasswordActivity;
import com.example.vcv.R;

public class MyProfileFragment extends Fragment {

    private MyProfileViewModel myProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myProfileViewModel =
                ViewModelProviders.of(this).get(MyProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_myprofile, container, false);
//        final TextView textView = root.findViewById(R.id.text_myprofile);
//        myProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        Button b_forgetPassword = root.findViewById(R.id.b_change_password);
        b_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(root.getContext(), ForgotPasswordActivity.class);
                startActivity(myIntent);
            }
        });

        return root;
    }
}

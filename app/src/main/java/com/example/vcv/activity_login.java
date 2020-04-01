package com.example.vcv;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.vcv.formlogin.Login;
import com.example.vcv.formlogin.Signin;

import androidx.appcompat.app.AppCompatActivity;

public class activity_login extends AppCompatActivity {

    private android.app.FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        fragmentManager = getFragmentManager();

        Fragment fragment = new Login();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.body_login_signin_fragment, fragment);
        fragmentTransaction.commit();
    }

    // Handler
    public void clickLogin(View view) {
        Button BLogin = findViewById(R.id.b_login);
        Button BSignin = findViewById(R.id.b_signin);

        BLogin.setBackgroundResource(R.drawable.tab_left_border_selected);
        BSignin.setBackgroundResource(R.drawable.tab_right_border);
        insertFragmentLogin();
    }

    public void clickSignin(View view) {
        Button BLogin = findViewById(R.id.b_login);
        Button BSignin = findViewById(R.id.b_signin);

        BLogin.setBackgroundResource(R.drawable.tab_left_border);
        BSignin.setBackgroundResource(R.drawable.tab_right_border_selected);
        insertFragmentSignin();
    }

    // Utility
    private void insertFragmentLogin() {
        LinearLayout containerFragment = findViewById(R.id.container_fragment);
        containerFragment.setPadding(15, 10, 15, 70);

        fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.body_login_signin_fragment);

        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }

        Fragment newFragment = new Login();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.body_login_signin_fragment, newFragment);
        fragmentTransaction.commit();
    }

    private void insertFragmentSignin() {
        LinearLayout containerFragment = findViewById(R.id.container_fragment);
        containerFragment.setPadding(15, 10, 15, 35);

        fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.body_login_signin_fragment);

        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }

        Fragment newFragment = new Signin();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.body_login_signin_fragment, newFragment);
        fragmentTransaction.commit();
    }
}

package com.example.vcv;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vcv.formlogin.Login;
import com.example.vcv.formlogin.Signin;

import androidx.appcompat.app.AppCompatActivity;

public class activity_login extends AppCompatActivity {
    private android.app.FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean isLogginin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        insertFragmentLogin();
    }

    // Handler
    public void clickLoginTab(View view) {
        Button TLogin = findViewById(R.id.b_login);
        Button TSignin = findViewById(R.id.b_signin);
        Button BAccess = findViewById(R.id.b_access);
        TLogin.setBackgroundResource(R.drawable.tab_left_border_selected);
        TSignin.setBackgroundResource(R.drawable.tab_right_border);
        insertFragmentLogin();
        BAccess.setText(R.string.login);
        isLogginin = true;
    }

    public void clickSigninTab(View view) {
        Button TLogin = findViewById(R.id.b_login);
        Button TSignin = findViewById(R.id.b_signin);
        Button BAccess = findViewById(R.id.b_access);
        TLogin.setBackgroundResource(R.drawable.tab_left_border);
        TSignin.setBackgroundResource(R.drawable.tab_right_border_selected);
        insertFragmentSignin();
        BAccess.setText(R.string.signin);
        isLogginin = false;
    }

    public void access(View view) {
        if (isLogginin) {
            login();
        } else {
            signin();
        }
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

    private void login() {
        EditText ETEmail = findViewById(R.id.et_email);
        EditText ETPassword = findViewById(R.id.et_password);
        String email = "admin";
        String password = "admin";
        if (ETEmail.getText().toString().equals(email) && ETPassword.getText().toString().equals(password)) {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        }
    }

    private void signin() {
        EditText ETName = findViewById(R.id.et_name);
        EditText ETSurname = findViewById(R.id.et_surname);
        EditText ETTelephone = findViewById(R.id.et_telephone);
        EditText ETEmail = findViewById(R.id.et_email_signin);
        // TODO: Registrazione
    }
}
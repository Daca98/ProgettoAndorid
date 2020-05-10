package com.example.vcv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vcv.ui.login.LoginFragment;
import com.example.vcv.ui.signin.SigninFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean isLogginin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        insertFragmentLogin();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        // System.out.println("currentUser " + currentUser);
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

    public void forgotPassword(View view) {
        Intent myIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(myIntent);
    }

    // Utility
    private void insertFragmentLogin() {
        LinearLayout containerFragment = findViewById(R.id.container_fragment);
        containerFragment.setPadding(15, 10, 15, 70);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.body_login_signin_fragment);
        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        Fragment newFragment = new LoginFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.body_login_signin_fragment, newFragment);
        fragmentTransaction.commit();
    }

    private void insertFragmentSignin() {
        LinearLayout containerFragment = findViewById(R.id.container_fragment);
        containerFragment.setPadding(15, 10, 15, 35);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.body_login_signin_fragment);
        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        Fragment newFragment = new SigninFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.body_login_signin_fragment, newFragment);
        fragmentTransaction.commit();
    }

    private void login() {
        // String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        // String password = ((EditText) findViewById(R.id.et_password)).getText().toString();

        String email = "mat.daca@gmail.com";
        String password = "prova!";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (task.isSuccessful() && user.isEmailVerified()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "signInWithEmail:success");
                                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(myIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void signin() {
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String surname = ((EditText) findViewById(R.id.et_surname)).getText().toString();
        final String telephone = ((EditText) findViewById(R.id.et_telephone)).getText().toString();
        final String email = ((EditText) findViewById(R.id.et_email_signin)).getText().toString();
        String pssw = ((EditText) findViewById(R.id.et_password_signin)).getText().toString();
        String psswConfirm = ((EditText) findViewById(R.id.et_confirm_password_signin)).getText().toString();

        if (pssw.equals(psswConfirm)) {
            mAuth.createUserWithEmailAndPassword(email, pssw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(LoginActivity.this,
                                                                "Verification email sent to " + email,
                                                                Toast.LENGTH_SHORT).show();

                                                        // TODO: Registrazione utente
                                                    } else {
                                                        Log.e("", "sendEmailVerification", task.getException());
                                                        Toast.makeText(LoginActivity.this,
                                                                "Failed to send verification email.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
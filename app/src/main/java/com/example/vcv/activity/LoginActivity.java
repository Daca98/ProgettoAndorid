package com.example.vcv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vcv.R;
import com.example.vcv.ui.login.LoginFragment;
import com.example.vcv.ui.signin.SigninFragment;
import com.example.vcv.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginActivity extends AppCompatActivity {
    private int REQUEST_CODE_FORGOT_PASSWORD = 0;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean isLogginin = true;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        insertFragmentLogin();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DB
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if currentUser is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // TODO: check with finger print or face recognition
            login(currentUser);
        }
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
            login(null);
        } else {
            signin();
        }
    }

    public void forgotPassword(View view) {
        Intent myIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivityForResult(myIntent, REQUEST_CODE_FORGOT_PASSWORD);
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

    private void login(FirebaseUser user) {
        String email = "";
        String password = "";

        if (user != null) {
            try {
                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithEmail:success");
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
        } else {
            email = ((EditText) findViewById(R.id.et_email)).getText().toString();
            password = ((EditText) findViewById(R.id.et_password)).getText().toString();

            if (!email.equals("") && !password.equals("")) {
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
                                        // If sign goes wrong, display a message to the user
                                        Log.w("", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign goes wrong, display a message to the user
                                    Log.w("", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signin() {
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String surname = ((EditText) findViewById(R.id.et_surname)).getText().toString();
        final String telephone = ((EditText) findViewById(R.id.et_telephone)).getText().toString();
        final String email = ((EditText) findViewById(R.id.et_email_signin)).getText().toString();
        final String badgeNumber = ((EditText) findViewById(R.id.et_badge_number_signin)).getText().toString();
        String pssw = ((EditText) findViewById(R.id.et_password_signin)).getText().toString();
        String psswConfirm = ((EditText) findViewById(R.id.et_confirm_password_signin)).getText().toString();
        final User user = new User(name, surname, telephone, badgeNumber, email);

        if (!name.equals("") && !surname.equals("") && !telephone.equals("") && !email.equals("") && !badgeNumber.equals("") && !pssw.equals("") && pssw.equals(psswConfirm)) {
            mAuth.createUserWithEmailAndPassword(email, pssw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "createUserWithEmail:success");
                                final FirebaseUser userFirebase = mAuth.getCurrentUser();
                                if (userFirebase != null) {
                                    userFirebase.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(LoginActivity.this,
                                                                getString(R.string.send_email_check) + " " + email,
                                                                Toast.LENGTH_SHORT).show();

                                                        // Initialize Firebase DB
                                                        mDatabase = FirebaseDatabase.getInstance().getReference();
                                                        mDatabase.child("users").child(userFirebase.getUid()).setValue(user)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.i("", "User added with success to user's collection");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e("", e.getMessage());
                                                                    }
                                                                });
                                                    } else {
                                                        Log.e("", "sendEmailVerification", task.getException());
                                                        Toast.makeText(LoginActivity.this,
                                                                getString(R.string.send_email_error),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                    mAuth.signOut();
                                                    insertFragmentLogin();
                                                }
                                            });
                                }
                            } else {
                                // If sign in goes wrong, display a message to the userf
                                Log.w("", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
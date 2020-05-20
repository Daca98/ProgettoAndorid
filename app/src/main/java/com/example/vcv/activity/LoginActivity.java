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
import com.example.vcv.utility.QueryDB;
import com.example.vcv.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    private int REQUEST_CODE_FORGOT_PASSWORD = 0;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean isLogginin = true;
    private DatabaseReference mDatabase;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        insertFragmentLogin();

        //initialize for biometric authentication
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.authentication_failed), Toast.LENGTH_SHORT)
                        .show();
                logout();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.authentication_succes), Toast.LENGTH_SHORT).show();
                login(currentUser);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.biometric_negative))
                .setConfirmationRequired(false)
                .build();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase DB
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if currentUser is signed in (non-null)
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //check with finger print or face recognition
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo);
            }
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
                            goToMainActivity();
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
                                        // Download firebase data and insert them in sqlite
                                        DatabaseReference dbFirebase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                        dbFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User completeUser = dataSnapshot.getValue(User.class);

                                                if (completeUser != null) {
                                                    QueryDB db = new QueryDB(LoginActivity.this);
                                                    db.insertUserData(completeUser);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("", databaseError.getMessage());
                                            }
                                        });

                                        goToMainActivity();
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

    private void logout()
    {
        mAuth.signOut();
        QueryDB db = new QueryDB(LoginActivity.this);
        db.cleanLogout();
    }

    private void goToMainActivity() {
        Log.d("", "signInWithEmail:success");
        ((EditText) findViewById(R.id.et_email)).setText("");
        ((EditText) findViewById(R.id.et_password)).setText("");
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
    }
}
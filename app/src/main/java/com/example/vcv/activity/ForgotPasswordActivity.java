package com.example.vcv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vcv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private boolean isChangingPassword = false;

    /**
     * Create View for reset password
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Bundle b = getIntent().getExtras();
        // b != null -> user is changing password (from profile)
        // b == null -> user is resetting password
        if (b != null) {
            isChangingPassword = b.getBoolean("isChangingPassword", false);
        }
    }

    /**
     * Method use for resetting password
     *
     * @param view
     */
    public void resetPassword(View view) {
        final String email = ((EditText) findViewById(R.id.et_email_rec_pssw)).getText().toString();

        if (!email.equals("")) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.send_reset_pssw), Toast.LENGTH_SHORT).show();
                                Log.i("RESET_PASSWORD", "Email sent with success to " + email);

                                if (isChangingPassword) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent myIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(myIntent);
                                } else {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.send_reset_pssw_error), Toast.LENGTH_SHORT).show();
                                Log.e("RESET_PASSWORD", "Can not send email reset password to " + email);
                            }
                        }
                    });
        }
    }
}

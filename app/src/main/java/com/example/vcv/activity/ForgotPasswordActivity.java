package com.example.vcv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.vcv.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private boolean isChangingPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isChangingPassword = b.getBoolean("isChangingPassword", false);
        }
    }

    public void resetPassword(View view) {
        String email = ((EditText) findViewById(R.id.et_email_rec_pssw)).getText().toString();

        if (!email.equals("")) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.send_reset_pssw), Toast.LENGTH_SHORT).show();
                                Log.d("", "Email sent");

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
                                Log.e("", "Can not send email reset password");
                            }
                        }
                    });
        }
    }
}

package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static maes.tech.intentanim.CustomIntent.customType;

public class VerifyPhoneNo extends AppCompatActivity {

    EditText numberOTP;
    TextView phone_verify;
    String verificationCodeBySystem;
    Button BtnVerify, BtnSend;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    ImageButton btnBackVerify;
    private static final String TAG = "VerifyPhoneNo";
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);

        BtnVerify = findViewById(R.id.buttonVerify);
        BtnSend = findViewById(R.id.sendOTP);
        phone_verify = findViewById(R.id.phoneNo);
        numberOTP = findViewById(R.id.textOTP);
        progressBar = findViewById(R.id.progress_verify);
        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);
        BtnSend.setVisibility(View.INVISIBLE);
        btnBackVerify = findViewById(R.id.back_verify);

        String phoneNo = getIntent().getStringExtra("phone");
        sendVerificationCodeToUser(phoneNo);
        phone_verify.setText(phoneNo);

        BtnSend.postDelayed(new Runnable() {
            @Override
            public void run() {
                BtnSend.setVisibility(View.VISIBLE);
            }
        }, 1000 * 40);

        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(phoneNo, mResendToken);
            }

            private void resendVerificationCode(String phoneNo,
                                                PhoneAuthProvider.ForceResendingToken token) {
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNo)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(VerifyPhoneNo.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .setForceResendingToken(token)     // ForceResendingToken from callbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        btnBackVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAcc();
            }
        });

        BtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = numberOTP.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    numberOTP.setError("Wrong OTP");
                    numberOTP.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationCodeBySystem = s;
            mResendToken = token;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            sendToMain();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(VerifyPhoneNo.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            deleteAcc();
                        }
                    }
                });

    }

    private void deleteAcc() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User deleted!");
                    }
                });
        String uid = user.getUid().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("Name", "Deleted User");
        updates.put("email", FieldValue.delete());
        updates.put("phone", FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Details successfully deleted!");
            }
        });

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(VerifyPhoneNo.this, TutorialFirst.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            startActivity(new Intent(VerifyPhoneNo.this, SignUp.class));
            customType(VerifyPhoneNo.this, "right-to-left");
            deleteAcc();
            finish();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to go back.", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}
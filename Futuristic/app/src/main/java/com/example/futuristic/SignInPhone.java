package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignInPhone extends AppCompatActivity {

    TextInputEditText logNumberOTP;
    EditText logPhoneNumber, logCountryCode;
    String verificationCodeBySystem;
    Button logBtnVerify, logBtnSend, logBtnResend, buttonSignUp, buttonLogin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    private static final String TAG = "SignInPhone";
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_login_phone);

        logBtnVerify = findViewById(R.id.buttonVerify_log);
        logBtnResend = findViewById(R.id.resendOTP_log);
        logBtnSend = findViewById(R.id.buttonSend_log);

        logCountryCode = findViewById(R.id.cc_log);
        logPhoneNumber = findViewById(R.id.phoneNo_log);

        logNumberOTP = findViewById(R.id.textOTP_log);
        progressBar = (ProgressBar) findViewById(R.id.progress_verify_log);
        mAuth = FirebaseAuth.getInstance();
        logBtnResend.setVisibility(View.INVISIBLE);

        buttonSignUp = findViewById(R.id.buttonsignup_phone);
        buttonLogin = findViewById(R.id.buttonlog_phone);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInPhone.this, SignUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                customType(SignInPhone.this, "left-to-right");
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInPhone.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                customType(SignInPhone.this, "left-to-right");
            }
        });


        logBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logNumberOTP.requestFocus();

                String country_code = logCountryCode.getText().toString().trim();
                String phone_number = logPhoneNumber.getText().toString().trim().replaceAll("^0+(?=.)", "");
                String phoneNo = "+" + country_code + "" + phone_number;

                if (TextUtils.isEmpty(country_code)) {
                    logCountryCode.setError("Enter Country Code");
                    logCountryCode.requestFocus();
                    return;
                } else {
                    logCountryCode.clearFocus();
                    logCountryCode.setError(null);
                }

                if (TextUtils.isEmpty(phone_number)) {
                    logPhoneNumber.setError("Enter Phone Number");
                    logPhoneNumber.requestFocus();
                    return;
                } else {
                    logPhoneNumber.clearFocus();
                    logPhoneNumber.setError(null);
                }

                if (!TextUtils.isEmpty(country_code) && !TextUtils.isEmpty(phone_number)) {
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference yourCollRef = rootRef.collection("users");
                    Query query = yourCollRef.whereEqualTo("phone", phoneNo);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                    sendVerificationCodeToUser(phoneNo);
                                    Toast.makeText(SignInPhone.this, "Code has been sent to" + phoneNo, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignInPhone.this, "Please Create Account First", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        logBtnResend.postDelayed(new Runnable() {
            @Override
            public void run() {
                logBtnSend.setVisibility(View.VISIBLE);
            }
        }, 1000 * 40);

        logBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country_code = logCountryCode.getText().toString().trim();
                String phone_number = logPhoneNumber.getText().toString().trim().replaceAll("^0+(?=.)", "");
                String phoneNo = "+" + country_code + "" + phone_number;

                resendVerificationCode(phoneNo, mResendToken);
            }

            private void resendVerificationCode(String phoneNo,
                                                PhoneAuthProvider.ForceResendingToken token) {

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNo)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(SignInPhone.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .setForceResendingToken(token)     // ForceResendingToken from callbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        logBtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = logNumberOTP.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    logNumberOTP.setError("Wrong OTP");
                    logNumberOTP.requestFocus();
                    return;
                } else {
                    logNumberOTP.clearFocus();
                    logNumberOTP.setError(null);
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
                        .setActivity(SignInPhone.this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
            Toast.makeText(SignInPhone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            sendToMain();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            task.getException();// The verification code entered was invalid
                        }
                    }
                });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(SignInPhone.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInPhone.this, FirstPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SignInPhone.this, "right-to-left");
    }

}

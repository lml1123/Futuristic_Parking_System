package com.example.futuristic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignUp extends AppCompatActivity {

    TextInputLayout regPhoneNumber, regPassword, regEmail, regCC, repassword;
    TextInputLayout regFullname;
    Button buttonSignUp, buttonLogin;
    ProgressBar progressBar;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    private static final String TAG = "SignUp";
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_register);

        regCC = findViewById(R.id.CCode);
        regPhoneNumber = findViewById(R.id.phoneNumber_reg);
        regPassword = findViewById(R.id.password_reg);
        repassword = findViewById(R.id.repassword_reg);
        regFullname = findViewById(R.id.fullname_reg);
        regEmail = findViewById(R.id.email_reg);
        buttonSignUp = findViewById(R.id.buttonsignup);
        buttonLogin = findViewById(R.id.buttonlogin);
        progressBar = (ProgressBar) findViewById(R.id.progress_reg);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            customType(SignUp.this, "right-to-left");
            finish();
        });


        buttonSignUp.setOnClickListener(v -> {
            String email = regEmail.getEditText().getText().toString().trim();
            String password = regPassword.getEditText().getText().toString().trim();
            String reenterpass = repassword.getEditText().getText().toString().trim();
            String fullname = regFullname.getEditText().getText().toString();
            String country_code = regCC.getEditText().getText().toString().trim();
            String phone_number = regPhoneNumber.getEditText().getText().toString().trim().replaceAll("^0+(?=.)", "");
            String phoneNo = "+" + country_code + "" + phone_number;

            if (TextUtils.isEmpty(fullname)) {
                regFullname.setError("Please Enter Your Name");
                return;
            } else {
                regFullname.setError(null);
            }

            if (TextUtils.isEmpty(country_code)) {
                regCC.setError("Enter Country Code");
                regCC.requestFocus();
                return;
            } else {
                regCC.setError(null);
            }

            if (TextUtils.isEmpty(phone_number)) {
                regPhoneNumber.setError("Phone Number is Required.");
                return;
            } else {
                regPhoneNumber.setError(null);
            }

            if (TextUtils.isEmpty(email)) {
                regEmail.setError("Email is Required.");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                regEmail.setError("Please enter a valid email address");
                return;
            } else {
                regEmail.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                regPassword.setError("Password is Required.");
                return;
            } else if (password.length() < 6) {
                regPassword.setError("Password must be >= 6 Characters.");
                return;
            } else {
                regPassword.setError(null);
            }

            if (TextUtils.isEmpty(reenterpass)) {
                repassword.setError("Please Enter Password Again");
                return;
            } else {
                repassword.setError(null);
            }

            if (!reenterpass.equals(password)) {
                repassword.setError("Password Not Same");
                regPassword.setError("Password Not Same");
                return;
            } else {
                repassword.setError(null);
                regPassword.setError(null);
            }

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference yourCollRef = rootRef.collection("users");
            Query query = yourCollRef.whereEqualTo("phone", phoneNo);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Be Patient and wait for the Verification Code.", Toast.LENGTH_LONG).show();

                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fstore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();

                                        user.put("Name", fullname);
                                        user.put("email", email);
                                        user.put("phone", phoneNo);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "onSuccess: user profile is created for: " + userID);

                                                DocumentReference doc1 = fstore.collection("users").document(userID)
                                                        .collection("E-Wallet").document("Cash");
                                                Map<String, Integer> money = new HashMap<>();
                                                money.put("Money", 0);
                                                doc1.set(money).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "E-Wallet Created For " + userID);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure" + e.toString());
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure" + e.toString());
                                            }
                                        });



                                        Intent verify = new Intent(SignUp.this, VerifyPhoneNo.class);
                                        verify.putExtra("phone", phoneNo);
                                        startActivity(verify);
                                    } else {
                                        Toast.makeText(SignUp.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUp.this, "Phone Number Ady Exists in Other Account.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                }
            });

            progressBar.setVisibility(View.VISIBLE);

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this, FirstPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SignUp.this, "right-to-left");
    }
}
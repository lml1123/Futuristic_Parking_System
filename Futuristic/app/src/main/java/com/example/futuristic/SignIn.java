package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignIn extends AppCompatActivity {

    TextInputLayout logEmail, logPassword;
    Button buttonSignUp, buttonLogin, buttonForget, buttonPhone;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_login);

        logEmail = findViewById(R.id.email_log);
        logPassword = findViewById(R.id.password_log);
        buttonSignUp = findViewById(R.id.buttonsignup);
        buttonLogin = findViewById(R.id.buttonlogin);
        buttonForget = findViewById(R.id.buttonforget);
        buttonPhone = findViewById(R.id.buttonphonelog);
        progressBar = (ProgressBar) findViewById(R.id.progress_log);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                customType(SignIn.this, "left-to-right");
            }
        });

        buttonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInPhone.class);
                startActivity(intent);
                finish();
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = logEmail.getEditText().getText().toString().trim();
                String password = logPassword.getEditText().getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    logEmail.setError("Email is Required.");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    logEmail.setError("Please enter a valid email address");
                    return;
                } else {
                    logEmail.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    logPassword.setError("Password is Required.");
                    return;
                } else if (password.length() < 6) {
                    logPassword.setError("Password must be >= 6 Characters.");
                    return;
                } else {
                    logPassword.setError(null);
                }


                progressBar.setVisibility(View.VISIBLE);

                fAuth = FirebaseAuth.getInstance();
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(SignIn.this, "Wrong Email or Password", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

        buttonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View popupView = LayoutInflater.from(SignIn.this).inflate(R.layout.pop_forget_password, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                TextInputLayout EmailReset = popupView.findViewById(R.id.email_reset_password);
                Button BtnClose = popupView.findViewById(R.id.closePopUpForget);
                Button BtnConfirm = popupView.findViewById(R.id.confirmReset);

                BtnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                fAuth = FirebaseAuth.getInstance();

                BtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String mail = EmailReset.getEditText().getText().toString();

                        if (TextUtils.isEmpty(mail)) {
                            EmailReset.setError("Email is Required.");
                            return;
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            EmailReset.setError("Please enter a valid email address");
                            return;
                        } else {
                            EmailReset.setError(null);
                        }

                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignIn.this, "Reset Link Has Been Sent to Your Email.", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignIn.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(popupView, 0, 0);

                dimBehind(popupWindow);
            }

            public void dimBehind(PopupWindow popupWindow) {
                View container = popupWindow.getContentView().getRootView();
                Context context = popupWindow.getContentView().getContext();
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.4f;
                wm.updateViewLayout(container, p);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignIn.this, FirstPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SignIn.this, "right-to-left");
    }

}
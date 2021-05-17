package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class ProfileDetails extends ProfilePage {

    TextInputLayout EditFullName, EditPhone, EditEmail, EditPassword, EditRePassword;
    Button EditSave, ResetPassword;
    ImageButton BtnBack;
    ExpandableRelativeLayout LinearPassword;
    private static final String TAG = "ProfileDetails";
    String enteredPassword;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_profile);

        EditFullName = findViewById(R.id.edit_FullName);
        EditPhone = findViewById(R.id.edit_PhoneNumber);
        EditEmail = findViewById(R.id.edit_Email);
        EditPassword = findViewById(R.id.edit_Password);
        EditRePassword = findViewById(R.id.edit_RePassword);
        EditSave = findViewById(R.id.edit_SaveBtn);
        ResetPassword = findViewById(R.id.edit_btnPassword);
        LinearPassword = findViewById(R.id.linear_resetPassword);
        BtnBack = findViewById(R.id.backProfileP);

        userID = fAuth.getCurrentUser().getUid();

        enteredPassword = getIntent().getStringExtra("enteredPass");

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapshot != null;
                EditFullName.getEditText().setText(documentSnapshot.getString("Name"));
                EditPhone.getEditText().setText(documentSnapshot.getString("phone"));
                EditEmail.getEditText().setText(documentSnapshot.getString("email"));
            }
        });

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileDetails.this, ProfilePage.class));
                customType(ProfileDetails.this, "right-to-left");
                finish();
            }
        });

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearPassword.toggle();
            }
        });


        EditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newFullName = EditFullName.getEditText().getText().toString();
                String newEmail = EditEmail.getEditText().getText().toString();

                String newPassword = EditPassword.getEditText().getText().toString();
                String newRePassword = EditRePassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(newFullName)) {
                    EditFullName.setError("Please Enter Your Name");
                    return;
                } else {
                    EditFullName.setError(null);
                }

                if (TextUtils.isEmpty(newEmail)) {
                    EditEmail.setError("Email is Required.");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    EditEmail.setError("Please enter a valid email address");
                    return;
                } else {
                    EditEmail.setError(null);
                }

                if (newPassword.length() >= 1 && newPassword.length() < 6) {
                    EditPassword.setError("Password must be >= 6 Characters.");
                    return;
                } else {
                    EditPassword.setError(null);
                }

                if (newRePassword.length() >= 1 && newRePassword.length() < 6) {
                    EditRePassword.setError("Password must be >= 6 Characters.");
                    return;
                } else {
                    EditRePassword.setError(null);
                }

                if (!newRePassword.equals(newPassword)) {
                    EditPassword.setError("Password Not Same");
                    EditRePassword.setError("Password Not Same");
                    return;
                } else {
                    EditPassword.setError(null);
                    EditRePassword.setError(null);
                }

                DocumentReference documentReference = fStore.collection("users").document(userID);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        assert documentSnapshot != null;
                        String name = documentSnapshot.getString("Name");
                        String email = documentSnapshot.getString("email");

                        if (newFullName.equals(name) && newEmail.equals(email) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(newRePassword)) {
                            View v = getCurrentFocus();
                            v.clearFocus();
                        } else if (!newFullName.equals(name) && newEmail.equals(email) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(newRePassword)) {
                            updateName();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!newEmail.equals(email) && newFullName.equals(name) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(newRePassword)) {
                            updateEmail();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newRePassword) && newFullName.equals(name) && newEmail.equals(email)) {
                            updatePassword();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!newFullName.equals(name) && !newEmail.equals(email) && TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(newRePassword)) {
                            updateName();
                            updateEmail();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!newFullName.equals(name) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newRePassword) && newEmail.equals(email)) {
                            updateName();
                            updatePassword();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!newEmail.equals(email) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newRePassword) && newFullName.equals(name)) {
                            updateEmail();
                            updatePassword();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else if (!newFullName.equals(name) && !newEmail.equals(email) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newRePassword)) {
                            updateName();
                            updateEmail();
                            updatePassword();
                            View v = getCurrentFocus();
                            v.clearFocus();
                            Toast.makeText(ProfileDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            View v = getCurrentFocus();
                            v.clearFocus();
                        }

                        startActivity(new Intent(ProfileDetails.this, ProfilePage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        customType(ProfileDetails.this, "right-to-left");

                    }
                });
            }
        });
    }

    public void updateName() {
        String newFullName = EditFullName.getEditText().getText().toString();

        fStore.collection("users").document(userID).update("Name", newFullName);
    }

    public void updateEmail() {
        String newEmail = EditEmail.getEditText().getText().toString();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapshot != null;

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                assert user != null;
                AuthCredential credential = EmailAuthProvider
                        .getCredential(Objects.requireNonNull(user.getEmail()), enteredPassword);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    assert user != null;
                                    user.updateEmail(newEmail)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        fStore.collection("users").document(userID).update("email", newEmail);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    public void updatePassword() {
        String newPassword = EditPassword.getEditText().getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                task.isSuccessful();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            startActivity(new Intent(ProfileDetails.this, ProfilePage.class));
            customType(ProfileDetails.this, "right-to-left");
            finish();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}


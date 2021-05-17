package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static maes.tech.intentanim.CustomIntent.customType;

public class ProfilePage extends AppCompatActivity {

    Button btnout, btnNews, btnSupport, btnAbout, btnTerms, btnVehicle;
    RelativeLayout EditProfile;
    TextView proFullname, proEmail, proPhoneNo;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditProfile = findViewById(R.id.editProfile);
        btnout = findViewById(R.id.lgoutbtn);
        proFullname = findViewById(R.id.fullname_profile);
        proEmail = findViewById(R.id.email_profile);
        proPhoneNo = findViewById(R.id.phoneNumber_profile);
        btnNews = findViewById(R.id.newsBtn);
        btnSupport = findViewById(R.id.supportBtn);
        btnAbout = findViewById(R.id.aboutBtn);
        btnTerms = findViewById(R.id.termsBtn);
        btnVehicle = findViewById(R.id.vehicleBtn);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setSelectedItemId(R.id.profile);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.record:
                        startActivity(new Intent(getApplicationContext(), RecordPage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.wallet:
                        startActivity(new Intent(getApplicationContext(), WalletPage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;

            }
        });

        btnVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, VehiclePage.class));
                customType(ProfilePage.this, "left-to-right");
            }
        });

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, NewsPage.class));
                customType(ProfilePage.this, "left-to-right");
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, SupportPage.class));
                customType(ProfilePage.this, "left-to-right");
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, AboutPage.class));
                customType(ProfilePage.this, "left-to-right");
            }
        });

        btnTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, TermsConditions.class));
                customType(ProfilePage.this, "left-to-right");
            }
        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapshot != null;
                proFullname.setText(documentSnapshot.getString("Name"));
                proPhoneNo.setText(documentSnapshot.getString("phone"));
                proEmail.setText(documentSnapshot.getString("email"));
            }
        });

        btnout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfilePage.this, SignIn.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(ProfilePage.this).inflate(R.layout.pop_password, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                TextInputLayout Re_Password = popupView.findViewById(R.id.re_password);
                Button btnClose = popupView.findViewById(R.id.closePopUpEdit);
                Button btnConfirm = popupView.findViewById(R.id.confirmPassword);
                ConstraintLayout DismissPOP = popupView.findViewById(R.id.outsidePOPPassword);

                DismissPOP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(Re_Password.getEditText().getText())) {
                            Re_Password.setError("Please Enter Password");
                            Re_Password.requestFocus();
                            return;
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userEmail = user.getEmail();
                        String userPassword = Re_Password.getEditText().getText().toString();

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userEmail, userPassword);

                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(ProfilePage.this, ProfileDetails.class);
                                                intent.putExtra("enteredPass", userPassword);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfilePage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(popupView, 0, 0);

                dimBehind(popupWindow);
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

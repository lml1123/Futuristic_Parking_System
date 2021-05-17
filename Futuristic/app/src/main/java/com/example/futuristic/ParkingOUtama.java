package com.example.futuristic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ParkingOUtama extends AppCompatActivity {

    Button cancel, CheckPrice;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView totalSlot, currentSlot;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private static final String TAG = "ParkingOUtama";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_outama);

        cancel = findViewById(R.id.cReservation);
        CheckPrice = findViewById(R.id.checkPrice);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);

        totalSlot = findViewById(R.id.tSlot);
        currentSlot = findViewById(R.id.cSlot);

        fStore.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("place", "One Utama")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int size = task.getResult().size();

                            fStore.collection("parking").document("oneUtama").collection("ou_b2")
                                    .whereEqualTo("place", "One Utama")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int totalSize = size + task.getResult().size();
                                                String TotalSize = String.valueOf(totalSize);
                                                totalSlot.setText(TotalSize);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        fStore.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("Availability", "Available")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int NotSize = task.getResult().size();

                            fStore.collection("parking").document("oneUtama").collection("ou_b2")
                                    .whereEqualTo("Availability", "Available")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int totalNotSize = NotSize + task.getResult().size();

                                                if (totalNotSize <= 15) {
                                                    currentSlot.setTextColor(getResources().getColor(R.color.colorRed));
                                                } else if (totalNotSize <= 25) {
                                                    currentSlot.setTextColor(getResources().getColor(R.color.colorOrange));
                                                } else {
                                                    currentSlot.setTextColor(getResources().getColor(R.color.colorLightGreen));
                                                }

                                                String TotalNotSize = String.valueOf(totalNotSize);
                                                currentSlot.setText(TotalNotSize);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        getTabs();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParkingOUtama.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CheckPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupViewPrice = LayoutInflater.from(ParkingOUtama.this).inflate(R.layout.pop_price, null);
                final PopupWindow popupWindowPrice = new PopupWindow(popupViewPrice, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                TextView Price1 = popupViewPrice.findViewById(R.id.price1);
                TextView Price2 = popupViewPrice.findViewById(R.id.price2);
                Button btnClose = popupViewPrice.findViewById(R.id.closePopUpPrice);
                ConstraintLayout DismissPOP = popupViewPrice.findViewById(R.id.outsidePOPPrice);

                DismissPOP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowPrice.dismiss();
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowPrice.dismiss();
                    }
                });

                DocumentReference documentReference = fStore.collection("parking").document("oneUtama");
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        assert documentSnapshot != null;
                        Double price1 = documentSnapshot.getDouble("price");
                        Double price2 = documentSnapshot.getDouble("price2");

                        String firstPrice = String.format(Locale.getDefault(), "%.2f", price1);
                        String secondPrice = String.format(Locale.getDefault(), "%.2f", price2);

                        Price1.setText("RM " + firstPrice + " for first four hours.");
                        Price2.setText("RM " + secondPrice + " for subsequent hours.");
                    }
                });


                popupWindowPrice.setFocusable(true);
                popupWindowPrice.showAsDropDown(popupViewPrice, 0, 0);

                dimBehind(popupWindowPrice);
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


    public void getTabs() {

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.addFragment(fragment_ou_b1.getInstance(), "Base 1");
                viewPagerAdapter.addFragment(fragment_ou_b2.getInstance(), "Base 2");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });


    }
}
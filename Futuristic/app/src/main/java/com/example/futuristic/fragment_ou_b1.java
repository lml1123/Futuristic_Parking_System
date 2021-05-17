package com.example.futuristic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class fragment_ou_b1 extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    RecyclerView B1ARecyclerView, B1BRecyclerView, B1CRecyclerView, B1DRecyclerView;
    private ParkingAdapter adapter1, adapter2, adapter3, adapter4;


    public static fragment_ou_b1 getInstance() {
        fragment_ou_b1 fragment_OU_B1 = new fragment_ou_b1();
        return fragment_OU_B1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_ou_b1, container, false);

        init(itemView);
        return itemView;
    }

    private void init(View itemView) {
        B1ARecyclerView = itemView.findViewById(R.id.b1aRecyclerView);
        B1BRecyclerView = itemView.findViewById(R.id.b1bRecyclerView);
        B1CRecyclerView = itemView.findViewById(R.id.b1cRecyclerView);
        B1DRecyclerView = itemView.findViewById(R.id.b1dRecyclerView);
        setUpRecyclerView1();
        setUpRecyclerView2();
        setUpRecyclerView3();
        setUpRecyclerView4();
    }

    private void setUpRecyclerView1() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("area", "A");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter1 = new ParkingAdapter(options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B1ARecyclerView.setLayoutManager(gridLayoutManager);
        B1ARecyclerView.setAdapter(adapter1);
        adapter1.setOnItemClickListener(this::onItemClick);
    }

    private void setUpRecyclerView2() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("area", "B");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter2 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B1BRecyclerView.setLayoutManager(gridLayoutManager);
        B1BRecyclerView.setAdapter(adapter2);

        adapter2.setOnItemClickListener(this::onItemClick);
    }

    private void setUpRecyclerView3() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("area", "C");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter3 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B1CRecyclerView.setLayoutManager(gridLayoutManager);
        B1CRecyclerView.setAdapter(adapter3);

        adapter3.setOnItemClickListener(this::onItemClick);
    }

    private void setUpRecyclerView4() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b1")
                .whereEqualTo("area", "D");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter4 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B1DRecyclerView.setLayoutManager(gridLayoutManager);
        B1DRecyclerView.setAdapter(adapter4);

        adapter4.setOnItemClickListener(this::onItemClick);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter1.startListening();
        adapter2.startListening();
        adapter3.startListening();
        adapter4.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter1.stopListening();
        adapter2.stopListening();
        adapter3.stopListening();
        adapter4.stopListening();
    }

    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_booking, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Button btnClose = (Button) popupView.findViewById(R.id.closePopUp);
        Button btnConfirm = (Button) popupView.findViewById(R.id.confirmReservation);

        String uid = fAuth.getCurrentUser().getUid();

        TextView place, parkingNumber, parkingFloor, parkid;
        place = popupView.findViewById(R.id.placeName);
        parkingFloor = popupView.findViewById(R.id.pfloor);
        parkingNumber = popupView.findViewById(R.id.pNo);
        parkid = popupView.findViewById(R.id.parkID);

        place.setText(documentSnapshot.getString("place"));
        parkingFloor.setText(documentSnapshot.getString("floorlvl"));
        parkingNumber.setText(documentSnapshot.getString("parkingNo"));
        parkid.setText(documentSnapshot.getString("parkingID"));

        TextView TextSpinner = popupView.findViewById(R.id.textSpinner);
        final Spinner spinner = popupView.findViewById(R.id.vehicleSpinner);
        final List<String> subjects = new ArrayList<>();
        subjects.add("None");
        subjects.add("+ Add Vehicle");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("Vehicle").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("plateNumber");
                        subjects.add(subject);
                    }
                }

            }
        });



        Query q1 = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("Record").whereEqualTo("payment", "UNPAID");
        q1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            popupWindow.dismiss();
                            btnConfirm.setClickable(false);
                            Toast.makeText(getActivity(), "You Have An Incomplete Reservation. Check Record Page.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        TextSpinner.setVisibility(View.VISIBLE);

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Please Select Your Vehicle!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 1:
                        popupWindow.dismiss();
                        startActivity(new Intent(getActivity(), VehiclePage.class));
                        customType(getActivity(), "right-to-left");
                        break;
                    default:
                        TextSpinner.setVisibility(View.GONE);
                        String textSelected = spinner.getSelectedItem().toString();

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String pid = documentSnapshot.getId();
                                String placeName = documentSnapshot.getString("place");
                                String pfloor = documentSnapshot.getString("floorlvl");
                                String pNumber = documentSnapshot.getString("parkingNo");

                                Map<String, Object> booked = new HashMap<>();
                                booked.put("User ID", uid);
                                booked.put("carPlate", textSelected);

                                DocumentReference doc = db.collection("parking").document("oneUtama").collection("ou_b1").document(pid);
                                doc.update("Availability", "Not Available").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(getActivity(), "Reservation placed successfully! " + pid, Toast.LENGTH_LONG).show();

                                        doc.update(booked);
                                        popupWindow.dismiss();

                                        Map<String, Object> reservation = new HashMap<>();
                                        reservation.put("place", placeName);
                                        reservation.put("parkingID", pid);
                                        reservation.put("floorlvl", pfloor);
                                        reservation.put("parkingNo", pNumber);
                                        reservation.put("bookingTime", FieldValue.serverTimestamp());
                                        reservation.put("payment", "UNPAID");
                                        reservation.put("carPlate", textSelected);

                                        String rid = db.collection("users").document(uid).collection("Record").document().getId();

                                        db.collection("users").document(uid).collection("Record").document(rid).set(reservation, SetOptions.merge());

                                        Intent intent = new Intent(getActivity(), RecordPage.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "There is an Error to make your reservation", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(popupView, 0, 0);
        dimBehind(popupWindow);
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.4f;
        wm.updateViewLayout(container, p);
    }
}
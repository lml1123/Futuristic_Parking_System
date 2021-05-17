package com.example.futuristic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class fragment_ou_b2 extends Fragment implements ParkingAdapter.OnItemClickListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    RecyclerView B2ARecyclerView, B2BRecyclerView, B2CRecyclerView, B2DRecyclerView;
    private ParkingAdapter adapter1, adapter2, adapter3, adapter4;
    private static final String TAG = "fragment_ou_b2";

    public static fragment_ou_b2 getInstance() {
        fragment_ou_b2 fragment_OU_B2 = new fragment_ou_b2();
        return fragment_OU_B2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_ou_b2, container, false);

        init(itemView);

        return itemView;
    }

    private void init(View itemView) {
        B2ARecyclerView = itemView.findViewById(R.id.b2aRecyclerView);
        B2BRecyclerView = itemView.findViewById(R.id.b2bRecyclerView);
        B2CRecyclerView = itemView.findViewById(R.id.b2cRecyclerView);
        B2DRecyclerView = itemView.findViewById(R.id.b2dRecyclerView);
        setUpRecyclerView1();
        setUpRecyclerView2();
        setUpRecyclerView3();
        setUpRecyclerView4();
    }

    private void setUpRecyclerView1() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b2")
                .whereEqualTo("area", "A");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter1 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B2ARecyclerView.setLayoutManager(gridLayoutManager);
        B2ARecyclerView.setAdapter(adapter1);

        adapter1.setOnItemClickListener(this);

    }

    private void setUpRecyclerView2() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b2")
                .whereEqualTo("area", "B");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter2 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B2BRecyclerView.setLayoutManager(gridLayoutManager);
        B2BRecyclerView.setAdapter(adapter2);

        adapter2.setOnItemClickListener(this);
    }

    private void setUpRecyclerView3() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b2")
                .whereEqualTo("area", "C");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();

        adapter3 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B2CRecyclerView.setLayoutManager(gridLayoutManager);
        B2CRecyclerView.setAdapter(adapter3);

        adapter3.setOnItemClickListener(this);
    }

    private void setUpRecyclerView4() {
        Query query = db.collection("parking").document("oneUtama").collection("ou_b2")
                .whereEqualTo("area", "D");
        FirestoreRecyclerOptions<TimeSlot> options = new FirestoreRecyclerOptions.Builder<TimeSlot>()
                .setQuery(query, TimeSlot.class)
                .build();


        adapter4 = new ParkingAdapter(options);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        B2DRecyclerView.setLayoutManager(gridLayoutManager);
        B2DRecyclerView.setAdapter(adapter4);

        adapter4.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_booking, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Button btnClose = popupView.findViewById(R.id.closePopUp);
        Button btnConfirm = popupView.findViewById(R.id.confirmReservation);

        String uid = fAuth.getCurrentUser().getUid();

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
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String subject = document.getString("plateNumber");
                        subjects.add(subject);
                    }
                }

            }
        });
        adapter.notifyDataSetChanged();

        TextView place, parkingNumber, parkingFloor, parkid;
        place = popupView.findViewById(R.id.placeName);
        parkingFloor = popupView.findViewById(R.id.pfloor);
        parkingNumber = popupView.findViewById(R.id.pNo);
        parkid = popupView.findViewById(R.id.parkID);

        place.setText(documentSnapshot.getString("place"));
        parkingFloor.setText(documentSnapshot.getString("floorlvl"));
        parkingNumber.setText(documentSnapshot.getString("parkingNo"));
        parkid.setText(documentSnapshot.getString("parkingID"));


        Query q1 = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("Record").whereEqualTo("payment", "UNPAID");
        q1.get().

                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                DocumentReference doc = db.collection("parking").document("oneUtama").collection("ou_b2").document(pid);
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

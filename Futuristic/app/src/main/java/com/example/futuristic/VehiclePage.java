package com.example.futuristic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class VehiclePage extends ProfilePage {

    ImageButton btnBack;
    RecyclerView recyclerVehicle;
    Button BtnAdd;
    private VehicleAdapter vAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        vAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_vehicle);

        btnBack = findViewById(R.id.backVehicle);
        recyclerVehicle = findViewById(R.id.vehicleRecycler);
        BtnAdd = findViewById(R.id.btnAdd);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customType(VehiclePage.this, "right-to-left");
                finish();
            }
        });

        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(VehiclePage.this).inflate(R.layout.pop_vehicle, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                TextInputLayout PlateNumber = popupView.findViewById(R.id.plateNumber);
                TextInputLayout ModelCar = popupView.findViewById(R.id.modelCar);
                Button btnClose = popupView.findViewById(R.id.closePopUpVehicle);
                Button btnConfirm = popupView.findViewById(R.id.confirmVehicle);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String carPlateNumber = PlateNumber.getEditText().getText().toString().toUpperCase().trim();
                        String carPlate = carPlateNumber.replace(" ", "");
                        String carModel = ModelCar.getEditText().getText().toString();

                        if (TextUtils.isEmpty(carPlateNumber)) {
                            PlateNumber.setError("Car Plate Number Required!");
                            return;
                        } else {
                            PlateNumber.setError(null);
                        }

                        if (TextUtils.isEmpty(carModel)) {
                            ModelCar.setError("Enter a car model!");
                            return;
                        } else {
                            ModelCar.setError(null);
                        }

                        Map<String, Object> newVehicle = new HashMap<>();
                        newVehicle.put("plateNumber", carPlate);
                        newVehicle.put("carModel", carModel);

                        fStore.collection("users").document(userID).collection("Vehicle")
                                .whereEqualTo("plateNumber", carPlate).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().isEmpty()) {
                                                fStore.collection("users").document(userID).collection("Vehicle")
                                                        .document().set(newVehicle);
                                                Toast.makeText(VehiclePage.this, "Add Vehicle Successful.", Toast.LENGTH_SHORT).show();
                                                popupWindow.dismiss();
                                            } else {
                                                Toast.makeText(VehiclePage.this, "Car Plate already existed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });


                    }
                });

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(popupView, 0, 0);

                dimBehind(popupWindow);
            }


        });

        vehicleAdapter();
        vAdapter.notifyDataSetChanged();
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

    private void vehicleAdapter() {
        String userID = fAuth.getCurrentUser().getUid();

        Query queryT = fStore.collection("users").document(userID)
                .collection("Vehicle")
                .orderBy("plateNumber", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Vehicle> options = new FirestoreRecyclerOptions.Builder<Vehicle>()
                .setQuery(queryT, Vehicle.class)
                .build();

        vAdapter = new VehicleAdapter(options);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerVehicle.setLayoutManager(linearLayoutManager);

        recyclerVehicle.setAdapter(vAdapter);
        vAdapter.notifyDataSetChanged();
        vAdapter.setOnItemClickListener(this::onItemClick4);
    }

    public void onItemClick4(DocumentSnapshot documentSnapshot, int position) {

        String vehicleRef = documentSnapshot.getId();

        View popupView2 = LayoutInflater.from(VehiclePage.this).inflate(R.layout.pop_vehicle_edit, null);
        final PopupWindow popupWindow2 = new PopupWindow(popupView2, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextInputLayout EditPlateNumber = popupView2.findViewById(R.id.editPlateNumber);
        TextInputLayout EditCarModel = popupView2.findViewById(R.id.editModelCar);
        Button BtnRemove = popupView2.findViewById(R.id.removeVehicle);
        Button BtnUpdate = popupView2.findViewById(R.id.updateVehicle);
        ConstraintLayout DismissPop = popupView2.findViewById(R.id.outsidePOPEdit);

        fStore.collection("users").document(userID).collection("Vehicle").document(vehicleRef)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                EditPlateNumber.getEditText().setText(documentSnapshot.getString("plateNumber"));
                EditCarModel.getEditText().setText(documentSnapshot.getString("carModel"));
            }
        });

        DismissPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.dismiss();
            }
        });

        BtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedCarPlate = EditPlateNumber.getEditText().getText().toString().toUpperCase().trim();
                String updatedCarNumber = updatedCarPlate.replace(" ", "");
                String updatedCarModel = EditCarModel.getEditText().getText().toString();

                if (TextUtils.isEmpty(updatedCarPlate)) {
                    EditPlateNumber.setError("Car Plate Number Required!");
                    return;
                } else {
                    EditPlateNumber.setError(null);
                }

                if (TextUtils.isEmpty(updatedCarModel)) {
                    EditCarModel.setError("Enter a car model!");
                    return;
                } else {
                    EditCarModel.setError(null);
                }

                Map<String, Object> updateVehicle = new HashMap<>();
                updateVehicle.put("plateNumber", updatedCarNumber);
                updateVehicle.put("carModel", updatedCarModel);

                fStore.collection("users").document(userID).collection("Vehicle")
                        .whereEqualTo("plateNumber", updatedCarNumber).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    
                                    fStore.collection("users").document(userID).collection("Vehicle").document(vehicleRef)
                                            .update(updateVehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(VehiclePage.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                            popupWindow2.dismiss();
                                        }
                                    });

                                }
                            }
                        });
            }
        });

        BtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fStore.collection("users").document(userID).collection("Vehicle").document(vehicleRef)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String currentCarPlate = documentSnapshot.getString("plateNumber");
                        AlertDialog.Builder builder = new AlertDialog.Builder(VehiclePage.this);
                        builder.setMessage("Are you sure to remove " + currentCarPlate + " ?");
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fStore.collection("users").document(userID).collection("Vehicle")
                                        .document(vehicleRef).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(VehiclePage.this, "Vehicle Removed", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        popupWindow2.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });

        popupWindow2.setFocusable(true);

        popupWindow2.showAsDropDown(popupView2, 0, 0);
        dimBehind(popupWindow2);
    }

}

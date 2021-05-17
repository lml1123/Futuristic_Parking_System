package com.example.futuristic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class VehicleAdapter extends FirestoreRecyclerAdapter<Vehicle, VehicleAdapter.VehicleHolder> {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private OnItemClickListener listener4;

    public VehicleAdapter(@NonNull FirestoreRecyclerOptions<Vehicle> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VehicleAdapter.VehicleHolder vehicleHolder, int i, @NonNull Vehicle vehicle) {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        String userID = fAuth.getCurrentUser().getUid();

        String vehicleRef = getSnapshots().getSnapshot(vehicleHolder.getAdapterPosition()).getId();

        DocumentReference docTrans = fStore.collection("users").document(userID)
                .collection("Vehicle").document(vehicleRef);

        docTrans.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                vehicleHolder.CarPlate.setText(vehicle.getPlateNumber());
                vehicleHolder.CarModel.setText(vehicle.getCarModel());

            }
        });
    }

    @NonNull
    @Override
    public VehicleAdapter.VehicleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vehicle, parent, false);
        return new VehicleHolder(v);
    }

    public class VehicleHolder extends RecyclerView.ViewHolder {

        TextView CarPlate, CarModel;
        ImageButton BtnEdit;

        public VehicleHolder(@NonNull View itemView) {
            super(itemView);

            CarPlate = itemView.findViewById(R.id.carPlate);
            CarModel = itemView.findViewById(R.id.carModel);
            BtnEdit = itemView.findViewById(R.id.editVehicle);

            BtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener4 != null) {
                        listener4.onItemClick3(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick3(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(VehicleAdapter.OnItemClickListener listener4) {
        this.listener4 = listener4;
    }
}

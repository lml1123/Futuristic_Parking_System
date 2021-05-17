package com.example.futuristic;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ParkingAdapter extends FirestoreRecyclerAdapter<TimeSlot, ParkingAdapter.ParkingHolder> {

    private OnItemClickListener listener;

    public ParkingAdapter(@NonNull FirestoreRecyclerOptions<TimeSlot> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ParkingAdapter.ParkingHolder parkingHolder, int i, @NonNull TimeSlot timeSlot) {
        parkingHolder.pID.setText(timeSlot.getParkingID());
        parkingHolder.pAva.setText(timeSlot.getAvailability());

        if ("Available".equals(timeSlot.getAvailability())) {
            parkingHolder.imageView.setVisibility(View.INVISIBLE);
            parkingHolder.cardView.setClickable(true);
        } else {
            parkingHolder.imageView.setVisibility(View.VISIBLE);
            parkingHolder.cardView.setClickable(false);
        }

    }

    @NonNull
    @Override
    public ParkingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_parking, parent, false);
        return new ParkingHolder(v);
    }

    public class ParkingHolder extends RecyclerView.ViewHolder {

        TextView pID, pAva;
        CardView cardView;
        ImageView imageView;

        public ParkingHolder(@NonNull View itemView) {
            super(itemView);
            this.pID = itemView.findViewById(R.id.txt_time_slot);
            this.pAva = itemView.findViewById(R.id.txt_time_slot_description);
            this.cardView = itemView.findViewById(R.id.card_time_slot);
            this.imageView = itemView.findViewById(R.id.imageCar);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

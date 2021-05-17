package com.example.futuristic;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.protobuf.StringValue;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

public class RecordAdapter extends FirestoreRecyclerAdapter<Record, RecordAdapter.RecordHolder> {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private OnItemClickListener listener2;

    public RecordAdapter(@NonNull FirestoreRecyclerOptions<Record> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecordAdapter.RecordHolder recordHolder, int i, @NonNull Record record) {

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        String userID = fAuth.getCurrentUser().getUid();

        String bookingRef = getSnapshots().getSnapshot(recordHolder.getAdapterPosition()).getId();

        recordHolder.bookID.setText(bookingRef);

        DocumentReference docRecord = fStore.collection("users").document(userID).collection("Record").document(bookingRef);
        docRecord.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                recordHolder.parkID.setText(value.getString("parkingID"));

                Date date = record.getBookingTime();
                if (date != null) {
                    DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                    String creationDate = dateFormat.format(date);
                    recordHolder.recordDate.setText(creationDate);
                }

                Drawable imageHolder = recordHolder.imageRecord.getContext().getResources().getDrawable(R.drawable.paid);
                Drawable imageHolder2 = recordHolder.imageRecord.getContext().getResources().getDrawable(R.drawable.unpaid);
                Drawable imageHolder3 = recordHolder.imageRecord.getContext().getResources().getDrawable(R.drawable.cancelled);

                if ("PAID".equals(record.getPayment())) {
                    recordHolder.imageRecord.setImageDrawable(imageHolder);
                } else if ("UNPAID".equals(record.getPayment())){
                    recordHolder.imageRecord.setImageDrawable(imageHolder2);
                }else if ("Cancelled".equals(record.getPayment())){
                    recordHolder.imageRecord.setImageDrawable(imageHolder3);
                }
            }
        });


    }

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_record, parent, false);
        return new RecordHolder(v);
    }

    public class RecordHolder extends RecyclerView.ViewHolder{

        TextView bookID, parkID, recordDate;
        RelativeLayout cardRecord;
        ImageView imageRecord;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            this.bookID = itemView.findViewById(R.id.recordBID);
            this.parkID = itemView.findViewById(R.id.recordPID);
            this.recordDate = itemView.findViewById(R.id.recordBDate);
            this.cardRecord = itemView.findViewById(R.id.recordCard);
            this.imageRecord = itemView.findViewById(R.id.statusRecord);

            cardRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener2 != null) {
                        listener2.onItemClick2(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick2(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnRecordClickListener(RecordAdapter.OnItemClickListener listener2) {
        this.listener2 = listener2;
    }
}



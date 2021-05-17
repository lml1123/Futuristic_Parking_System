package com.example.futuristic;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TransactionAdapter extends FirestoreRecyclerAdapter<Transaction, TransactionAdapter.TransactionHolder> {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private OnItemClickListener listener3;

    public TransactionAdapter(@NonNull FirestoreRecyclerOptions<Transaction> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionAdapter.TransactionHolder transactionHolder, int i, @NonNull Transaction transaction) {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        String userID = fAuth.getCurrentUser().getUid();

        String tranRef = getSnapshots().getSnapshot(transactionHolder.getAdapterPosition()).getId();

        transactionHolder.TranID.setText(tranRef);

        DocumentReference docTrans = fStore.collection("users").document(userID)
                .collection("E-Wallet").document("Transaction")
                .collection("TransactionRecord").document(tranRef);

        docTrans.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                transactionHolder.TranStatus.setText(transaction.getTransactionMode());
                transactionHolder.TranID.setText(tranRef);

                if ("TopUp".equals(transaction.getTransactionMode())) {
                    assert value != null;
                    Double topUpAmount = value.getDouble("topUpAmount");
                    String TopUpAmount = String.format(Locale.getDefault(), "%.2f", topUpAmount);
                    transactionHolder.TranAmount.setText("MYR " + TopUpAmount);

                    transactionHolder.TranAmount.setTextColor(Color.parseColor("#69D86E"));

                } else if ("Payment".equals(transaction.getTransactionMode())) {
                    assert value != null;
                    Double paymentAmount = value.getDouble("paymentAmount");
                    String PaymentAmount = String.format(Locale.getDefault(), "%.2f", paymentAmount);
                    transactionHolder.TranAmount.setText("- MYR " + PaymentAmount);

                    transactionHolder.TranAmount.setTextColor(Color.parseColor("#000000"));
                }

                Date date = transaction.getTransactionTime();
                if (date != null) {
                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
                    String creationDate = dateFormat.format(date);
                    transactionHolder.TranDate.setText(creationDate);
                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transaction, parent, false);
        return new TransactionHolder(v);
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {

        TextView TranStatus, TranID, TranAmount, TranDate;
        CardView TranCard;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            TranStatus = itemView.findViewById(R.id.transStatus);
            TranID = itemView.findViewById(R.id.tranID);
            TranAmount = itemView.findViewById(R.id.amount);
            TranCard = itemView.findViewById(R.id.transactionCard);
            TranDate = itemView.findViewById(R.id.tranDate);

            TranCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener3 != null) {
                        listener3.onItemClick3(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick3(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnRecordClickListener(TransactionAdapter.OnItemClickListener listener3) {
        this.listener3 = listener3;
    }
}


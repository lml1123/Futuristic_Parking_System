package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static maes.tech.intentanim.CustomIntent.customType;

public class TransactionAll extends WalletPage {

    RecyclerView TransRecycler;
    private TransactionAdapter TAdapter;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ImageButton BtnBack;

    @Override
    protected void onStart() {
        super.onStart();
        TAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_transaction_all);

        TransRecycler = findViewById(R.id.transRecycler);
        BtnBack = findViewById(R.id.backBtn);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransactionAll.this, WalletPage.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(TransactionAll.this,"right-to-left");
            }
        });

        transactionAdapter();
    }

    private void transactionAdapter() {

        String userID = fAuth.getCurrentUser().getUid();

        Query queryT = fStore.collection("users").document(userID)
                .collection("E-Wallet").document("Transaction")
                .collection("TransactionRecord")
                .orderBy("transactionTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Transaction> options = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(queryT, Transaction.class)
                .build();

        TAdapter = new TransactionAdapter(options);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TransRecycler.setLayoutManager(linearLayoutManager);
        TransRecycler.setAdapter(TAdapter);
        TAdapter.notifyDataSetChanged();
        TAdapter.setOnRecordClickListener(this::onItemClick3);
    }

    public void onItemClick3(DocumentSnapshot documentSnapshot, int position) {

        String tranRef = documentSnapshot.getId();
        Intent intent = new Intent(TransactionAll.this, TransactionDetails.class);
        customType(TransactionAll.this,"left-to-right");
        intent.putExtra("transactionID", tranRef);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(TransactionAll.this,"right-to-left");
    }
}

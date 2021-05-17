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

public class RecordPastPage extends RecordPage{

    RecyclerView PastAllRecycler;
    private RecordAdapter pAdapter;
    ImageButton BtnBack;

    @Override
    protected void onStart() {
        super.onStart();
        pAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_record_past);
        PastAllRecycler = findViewById(R.id.pastAllRecycler);
        BtnBack = findViewById(R.id.backBtnPast);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordPastPage.this, RecordPage.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(RecordPastPage.this,"right-to-left");
            }
        });

        transactionAdapter();
    }

    private void transactionAdapter() {

        Query queryA = dbase.collection("users").document(UID).collection("Record")
                .whereEqualTo("payment", "PAID")
                .orderBy("bookingTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                .setQuery(queryA, Record.class)
                .build();

        pAdapter = new RecordAdapter(options);

        pAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = pAdapter.getItemCount();

                if(totalNumberOfItems >=1) {
                    imagePast.setVisibility(View.GONE);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        PastAllRecycler.setLayoutManager(linearLayoutManager);
        PastAllRecycler.setAdapter(pAdapter);
        PastAllRecycler.setNestedScrollingEnabled(false);
        pAdapter.notifyDataSetChanged();
        pAdapter.setOnRecordClickListener(this::onItemClick2);
    }

    public void onItemClick2(DocumentSnapshot documentSnapshot, int position) {

        String bookIDRef = documentSnapshot.getId();
        Intent intent = new Intent(RecordPastPage.this, RecordDetails.class);
        intent.putExtra("bookingID", bookIDRef);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(RecordPastPage.this,"right-to-left");
    }
}

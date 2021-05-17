package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import static maes.tech.intentanim.CustomIntent.customType;

public class RecordCancelPage extends RecordPage{

    RecyclerView CancelRecycler;
    private RecordAdapter canAdapter;
    ImageButton BtnBack;

    @Override
    protected void onStart() {
        super.onStart();
        canAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        canAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_record_past);
        CancelRecycler = findViewById(R.id.pastAllRecycler);
        BtnBack = findViewById(R.id.backBtnPast);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordCancelPage.this, RecordPage.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(RecordCancelPage.this,"right-to-left");
            }
        });

        transactionAdapter();
    }

    private void transactionAdapter() {

        Query queryA = dbase.collection("users").document(UID).collection("Record")
                .whereEqualTo("payment", "Cancelled")
                .orderBy("bookingTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                .setQuery(queryA, Record.class)
                .build();

        canAdapter = new RecordAdapter(options);

        canAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = canAdapter.getItemCount();

                if(totalNumberOfItems >=1) {
                    imagePast.setVisibility(View.GONE);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        CancelRecycler.setLayoutManager(linearLayoutManager);
        CancelRecycler.setAdapter(canAdapter);
        CancelRecycler.setNestedScrollingEnabled(false);
        canAdapter.notifyDataSetChanged();
        canAdapter.setOnRecordClickListener(this::onItemClick2);
    }

    public void onItemClick2(DocumentSnapshot documentSnapshot, int position) {

        String bookIDRef = documentSnapshot.getId();
        Intent intent = new Intent(RecordCancelPage.this, RecordDetails.class);
        intent.putExtra("bookingID", bookIDRef);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(RecordCancelPage.this,"right-to-left");
    }
}

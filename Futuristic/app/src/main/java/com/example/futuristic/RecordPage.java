package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class RecordPage extends AppCompatActivity {

    TextView PastShowAll, CancelShowAll;
    RecyclerView currentRecyclerView, pastRecyclerView, cancelRecyclerView;
    FirebaseFirestore dbase = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private RecordAdapter cAdapter, pAdapter, canAdapter;
    SwipeRefreshLayout RefreshLayout;
    ImageView imagePast, imageCurrent, imageCancel;
    String UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setSelectedItemId(R.id.record);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.record:
                        return true;
                    case R.id.wallet:
                        startActivity(new Intent(getApplicationContext(), WalletPage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;

            }
        });

        currentRecyclerView = findViewById(R.id.currentRecycler);
        pastRecyclerView = findViewById(R.id.pastRecycler);
        cancelRecyclerView = findViewById(R.id.cancelRecycler);

        imageCurrent = findViewById(R.id.emptyCurrent);
        imagePast = findViewById(R.id.emptyPast);
        imageCancel = findViewById(R.id.emptyCancel);

        PastShowAll = findViewById(R.id.pastShowAll);
        CancelShowAll = findViewById(R.id.cancelShowAll);

        currentAdapter();
        pastAdapter();
        cancelAdapter();

        RefreshLayout = findViewById(R.id.refreshLayout);
        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onStop();
                onStart();
                RefreshLayout.setRefreshing(false);
            }
        });

        PastShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordPage.this, RecordPastPage.class);
                customType(RecordPage.this,"left-to-right");
                startActivity(intent);
            }
        });

        CancelShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordPage.this, RecordCancelPage.class);
                customType(RecordPage.this,"left-to-right");
                startActivity(intent);
            }
        });
    }

    void currentAdapter() {

        Query queryR = dbase.collection("users").document(UID).collection("Record")
                .whereEqualTo("payment", "UNPAID")
                .orderBy("bookingTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                .setQuery(queryR, Record.class)
                .build();

        cAdapter = new RecordAdapter(options);

        cAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = cAdapter.getItemCount();

                if(totalNumberOfItems >= 1) {
                    imageCurrent.setVisibility(View.GONE);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        currentRecyclerView.setLayoutManager(linearLayoutManager);
        currentRecyclerView.setAdapter(cAdapter);
        currentRecyclerView.setNestedScrollingEnabled(false);
        cAdapter.notifyDataSetChanged();
        cAdapter.setOnRecordClickListener(this::onItemClick2);
    }

    void pastAdapter() {

        Query queryA = dbase.collection("users").document(UID).collection("Record")
                .whereEqualTo("payment", "PAID")
                .orderBy("bookingTime", Query.Direction.DESCENDING)
                .limit(3);
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
        pastRecyclerView.setLayoutManager(linearLayoutManager);
        pastRecyclerView.setAdapter(pAdapter);
        pastRecyclerView.setNestedScrollingEnabled(false);
        pAdapter.notifyDataSetChanged();
        pAdapter.setOnRecordClickListener(this::onItemClick2);
    }

    void cancelAdapter() {

        Query queryA = dbase.collection("users").document(UID).collection("Record")
                .whereEqualTo("payment", "Cancelled")
                .orderBy("bookingTime", Query.Direction.DESCENDING)
                .limit(3);
        FirestoreRecyclerOptions<Record> options = new FirestoreRecyclerOptions.Builder<Record>()
                .setQuery(queryA, Record.class)
                .build();

        canAdapter = new RecordAdapter(options);

        canAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = canAdapter.getItemCount();

                if(totalNumberOfItems >=1) {
                    imageCancel.setVisibility(View.GONE);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cancelRecyclerView.setLayoutManager(linearLayoutManager);
        cancelRecyclerView.setAdapter(canAdapter);
        cancelRecyclerView.setNestedScrollingEnabled(false);
        canAdapter.notifyDataSetChanged();
        canAdapter.setOnRecordClickListener(this::onItemClick2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cAdapter.startListening();
        pAdapter.startListening();
        canAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cAdapter.stopListening();
        pAdapter.stopListening();
        canAdapter.stopListening();
    }

    public void onItemClick2(DocumentSnapshot documentSnapshot, int position) {

        String bookIDRef = documentSnapshot.getId();
        Intent intent = new Intent(RecordPage.this, RecordDetails.class);
        intent.putExtra("bookingID", bookIDRef);
        customType(RecordPage.this,"left-to-right");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

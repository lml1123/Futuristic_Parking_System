package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class TransactionDetails extends WalletPage {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextView TransStatus, TransTitle, TransAmount, TransBy, TransTime, TransID, TransBook, TransBookTitle;
    ImageButton BtnBack;
    LinearLayout TranBookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_transaction);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        TransStatus = findViewById(R.id.detailsTransStatus);
        TransTitle = findViewById(R.id.detailsTitleTrans);
        TransAmount = findViewById(R.id.amountTransaction);
        TransBy = findViewById(R.id.detailsTransBy);
        TransTime = findViewById(R.id.detailsTransTime);
        TransID = findViewById(R.id.detailsTransID);

        TransBook = findViewById(R.id.detailsTransBookID);
        TransBookTitle = findViewById(R.id.detailsTransBookTitle);
        TranBookID = findViewById(R.id.tranBookID);

        TranBookID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bookIDRef = TransBook.getText().toString();
                Intent intent = new Intent(TransactionDetails.this, RecordDetails.class);
                intent.putExtra("bookingID", bookIDRef);
                startActivity(intent);
            }
        });

        BtnBack = findViewById(R.id.backBtnDT);

        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                customType(TransactionDetails.this, "right-to-left");
            }
        });

        String transID = getIntent().getStringExtra("transactionID");

        TransID.setText(transID);

        String userID = fAuth.getCurrentUser().getUid();

        assert transID != null;
        DocumentReference transDoc = fStore.collection("users").document(userID).collection("E-Wallet")
                .document("Transaction").collection("TransactionRecord").document(transID);
        transDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                TransStatus.setText(value.getString("transactionMode"));

                Date transDate = value.getDate("transactionTime");
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                assert transDate != null;
                String transactionDate = dateFormat.format(transDate);
                TransTime.setText(transactionDate);

                if ("Payment".equals(value.getString("transactionMode"))) {

                    Double amount = value.getDouble("paymentAmount");
                    String Amount = String.format(Locale.getDefault(), "%.2f", amount);
                    TransAmount.setText(Amount);

                    TransBook.setText(value.getString("recordRef"));
                    TransTitle.setText("Parking Fee");
                    TransBy.setText("Paid By");

                } else if ("TopUp".equals(value.getString("transactionMode"))) {
                    TransBook.setVisibility(View.GONE);
                    TransBookTitle.setVisibility(View.GONE);
                    TranBookID.setVisibility(View.GONE);

                    Double amount = value.getDouble("topUpAmount");
                    String Amount = String.format(Locale.getDefault(), "%.2f", amount);
                    TransAmount.setText(Amount);
                    TransTitle.setText("You've topped up");
                    TransBy.setText("Total");
                }
            }
        });
    }
}
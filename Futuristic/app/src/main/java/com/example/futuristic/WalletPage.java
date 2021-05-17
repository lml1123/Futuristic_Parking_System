package com.example.futuristic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static maes.tech.intentanim.CustomIntent.customType;

public class WalletPage extends AppCompatActivity {

    TextView UserCash, TranShowAll;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    RecyclerView WalletRecycler;
    private TransactionAdapter tAdapter;
    RelativeLayout TopUp;
    private static final String TAG = "WalletPage";

    @Override
    protected void onStart() {
        super.onStart();
        tAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        UserCash = findViewById(R.id.userCash);
        TopUp = findViewById(R.id.topUp);
        WalletRecycler = findViewById(R.id.walletRecycler);
        TranShowAll = findViewById(R.id.tranShowAll);

        String userID = fAuth.getCurrentUser().getUid();

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setSelectedItemId(R.id.wallet);
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
                        startActivity(new Intent(getApplicationContext(), RecordPage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.wallet:
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

        DocumentReference documentReference = fStore.collection("users").document(userID).collection("E-Wallet").document("Cash");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapshot != null;

                Double dbCash = documentSnapshot.getDouble("Money");
                String RealAmount = String.format(Locale.getDefault(), "%.2f", dbCash);

                UserCash.setText(RealAmount);

            }
        });

        TopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(WalletPage.this).inflate(R.layout.pop_top_up, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                EditText inputTopUp = popupView.findViewById(R.id.amountTopUp);
                Button btnClose = popupView.findViewById(R.id.closePopUp2);
                Button btnConfirm = popupView.findViewById(R.id.confirmTopUp);
                ConstraintLayout DismissPop = popupView.findViewById(R.id.outsidePOPTopUp);

                inputTopUp.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});

                DismissPop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });


                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputAmount = inputTopUp.getText().toString().trim();
                        if (TextUtils.isEmpty(inputAmount)) {
                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(WalletPage.this, "Enter Amount!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Double amountTopUp = Double.parseDouble(inputAmount);
                            DocumentReference documentReference = fStore.collection("users").document(userID)
                                    .collection("E-Wallet").document("Cash");
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        assert document != null;
                                        if (document.exists()) {
                                            Double cashDB = document.getDouble("Money");
                                            Double newAmount = amountTopUp + cashDB;

                                            documentReference.update("Money", newAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    String newAmount2 = String.format(Locale.getDefault(), "%.2f", newAmount);
                                                    Toast.makeText(WalletPage.this, "Top Up Success. New Balance: RM" + newAmount2, Toast.LENGTH_SHORT).show();

                                                    Map<String, Object> transaction = new HashMap<>();
                                                    transaction.put("oldBalance", cashDB);
                                                    transaction.put("topUpAmount", amountTopUp);
                                                    transaction.put("newBalance", newAmount);
                                                    transaction.put("transactionMode", "TopUp");
                                                    transaction.put("transactionTime", FieldValue.serverTimestamp());

                                                    String topUpID = fStore.collection("users").document(userID).collection("E-Wallet")
                                                            .document("Transaction").collection("TransactionRecord").document().getId();

                                                    fStore.collection("users").document(userID).collection("E-Wallet")
                                                            .document("Transaction").collection("TransactionRecord")
                                                            .document(topUpID).set(transaction, SetOptions.merge());

                                                    popupWindow.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(WalletPage.this, "Top Up Failed " + e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            });

                        }
                    }
                });


                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(popupView, 0, 0);

                dimBehind(popupWindow);
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
        });

        TranShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletPage.this, TransactionAll.class);
                customType(WalletPage.this,"left-to-right");
                startActivity(intent);
            }
        });

        transactionAdapter();
        tAdapter.notifyDataSetChanged();
    }

    private void transactionAdapter() {

        String userID = fAuth.getCurrentUser().getUid();

        Query queryT = fStore.collection("users").document(userID)
                .collection("E-Wallet").document("Transaction")
                .collection("TransactionRecord")
                .orderBy("transactionTime", Query.Direction.DESCENDING).limit(3);
        FirestoreRecyclerOptions<Transaction> options = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(queryT, Transaction.class)
                .build();

        tAdapter = new TransactionAdapter(options);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        WalletRecycler.setLayoutManager(linearLayoutManager);

        WalletRecycler.setAdapter(tAdapter);
        tAdapter.notifyDataSetChanged();
        tAdapter.setOnRecordClickListener(this::onItemClick3);
    }

    public void onItemClick3(DocumentSnapshot documentSnapshot, int position) {

        String tranRef = documentSnapshot.getId();
        Intent intent = new Intent(WalletPage.this, TransactionDetails.class);
        intent.putExtra("transactionID", tranRef);
        customType(WalletPage.this,"right-to-left");
        startActivity(intent);

    }

    public static class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

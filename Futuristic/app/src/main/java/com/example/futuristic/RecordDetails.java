package com.example.futuristic;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static maes.tech.intentanim.CustomIntent.customType;

public class RecordDetails extends RecordPage {

    TextView DetailsBookID, DetailsParkID, DetailsPlace, DetailsFloorlvl, DetailsCarPlate,
            DetailsParkNo, DetailsBookTime, DetailsPayment, DetailsPrice, DetailsTransaction, DetailsDuration,
            TitleQrCode, TitleTransaction, TitlePrice, TitleDuration, TitleCancel, DetailsCancel;
    ImageView QRCode;
    ImageButton btnBackRecord;
    Button btnCancel, btnCheckOut;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_record);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        String bookID = getIntent().getStringExtra("bookingID");

        DetailsBookID = findViewById(R.id.detailsBookID);
        DetailsParkID = findViewById(R.id.detailsParkID);
        DetailsPlace = findViewById(R.id.detailsPlace);
        DetailsFloorlvl = findViewById(R.id.detailsFloorlvl);
        DetailsParkNo = findViewById(R.id.detailsParkNo);
        DetailsBookTime = findViewById(R.id.detailsBookTime);
        DetailsPayment = findViewById(R.id.detailsPayment);
        DetailsCarPlate = findViewById(R.id.detailsCarPlate);

        TitlePrice = findViewById(R.id.titlePrice);
        TitlePrice.setVisibility(View.GONE);
        DetailsPrice = findViewById(R.id.detailsPrice);
        DetailsPrice.setVisibility(View.GONE);

        TitleTransaction = findViewById(R.id.titleTransaction);
        TitleTransaction.setVisibility(View.GONE);
        DetailsTransaction = findViewById(R.id.detailsTransaction);
        DetailsTransaction.setVisibility(View.GONE);

        TitleQrCode = findViewById(R.id.titleQRCode);
        QRCode = findViewById(R.id.qrcode);

        TitleDuration = findViewById(R.id.durationTitle);
        TitleDuration.setVisibility(View.GONE);
        DetailsDuration = findViewById(R.id.detailsDuration);
        DetailsDuration.setVisibility(View.GONE);

        TitleCancel = findViewById(R.id.cancelTitle);
        TitleCancel.setVisibility(View.GONE);
        DetailsCancel = findViewById(R.id.detailsCancelTime);
        DetailsCancel.setVisibility(View.GONE);

        btnCancel = findViewById(R.id.details_cancel);
        btnCheckOut = findViewById(R.id.checkOutBtn);
        btnCheckOut.setVisibility(View.GONE);

        String userID = fAuth.getCurrentUser().getUid();
        assert bookID != null;

        btnBackRecord = findViewById(R.id.backRecord);
        btnBackRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customType(RecordDetails.this, "right-to-left");
                finish();
            }
        });

        DetailsBookID.setText(bookID);

        DocumentReference documentReference = fStore.collection("users").document(userID)
                .collection("Record").document(bookID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                assert documentSnapshot != null;
                DetailsParkID.setText(documentSnapshot.getString("parkingID"));
                DetailsPlace.setText(documentSnapshot.getString("place"));
                DetailsFloorlvl.setText(documentSnapshot.getString("floorlvl"));
                DetailsParkNo.setText(documentSnapshot.getString("parkingNo"));
                DetailsCarPlate.setText(documentSnapshot.getString("carPlate"));

                Date bookDate = documentSnapshot.getDate("bookingTime");
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                assert bookDate != null;
                String creationDate = dateFormat.format(bookDate);
                DetailsBookTime.setText(creationDate);
                Date current = Calendar.getInstance().getTime();
                long differentDate = current.getTime() - bookDate.getTime();

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;

                long elapsedHours = differentDate / hoursInMilli;
                differentDate = differentDate % hoursInMilli;

                long elapsedMinutes = differentDate / minutesInMilli;
                differentDate = differentDate % minutesInMilli;

                if (elapsedHours >= 1 || elapsedMinutes >= 30) {
                    btnCancel.setVisibility(View.GONE);
                }

                DetailsPayment.setText(documentSnapshot.getString("payment"));
                if ("PAID".equals(documentSnapshot.getString("payment"))) {
                    DetailsPayment.setTextColor(getResources().getColor(R.color.colorGreen));
                    TitlePrice.setVisibility(View.VISIBLE);
                    DetailsPrice.setVisibility(View.VISIBLE);
                    Double price = documentSnapshot.getDouble("price");
                    String price1 = String.format(Locale.getDefault(), "%.2f", price);
                    DetailsPrice.setText("RM" + price1);
                    TitleQrCode.setText("Exit QR Code");
                    QRCode.setImageResource(R.drawable.qr2);

                    TitleTransaction.setVisibility(View.VISIBLE);
                    DetailsTransaction.setText(documentSnapshot.getString("TransactionNo"));
                    DetailsTransaction.setVisibility(View.VISIBLE);

                    TitleDuration.setVisibility(View.VISIBLE);
                    DetailsDuration.setText(documentSnapshot.getString("parkingTime"));
                    DetailsDuration.setVisibility(View.VISIBLE);

                    btnCancel.setVisibility(View.GONE);

                } else if ("UNPAID".equals(documentSnapshot.getString("payment"))) {
                    DetailsPayment.setTextColor(getResources().getColor(R.color.colorRed));
                    btnCheckOut.setVisibility(View.VISIBLE);
                    TitleQrCode.setText("Enter QR Code");
                    QRCode.setImageResource(R.drawable.qr1);
                } else if ("Cancelled".equals(documentSnapshot.getString("payment"))) {
                    DetailsPayment.setTextColor(getResources().getColor(R.color.colorOrange));

                    TitleQrCode.setVisibility(View.GONE);
                    QRCode.setVisibility(View.GONE);

                    btnCancel.setVisibility(View.GONE);
                    btnCheckOut.setVisibility(View.GONE);

                    TitleCancel.setVisibility(View.VISIBLE);
                    DetailsCancel.setVisibility(View.VISIBLE);

                    Date cancelDate = documentSnapshot.getDate("cancelTime");
                    DateFormat dateFormat2 = SimpleDateFormat.getDateTimeInstance();
                    assert cancelDate != null;
                    String dateCancelled = dateFormat2.format(cancelDate);
                    DetailsCancel.setText(dateCancelled);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupViewCancel = LayoutInflater.from(RecordDetails.this).inflate(R.layout.pop_bookingcancel, null);
                final PopupWindow popupWindowCancel = new PopupWindow(popupViewCancel, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                Button closePop = popupViewCancel.findViewById(R.id.closePopUpCancel);
                Button ConfirmCancel = popupViewCancel.findViewById(R.id.confirmCancel);

                closePop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowCancel.dismiss();
                    }
                });

                ConfirmCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference documentReference4 = fStore.collection("users").document(userID)
                                .collection("Record").document(bookID);
                        documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document2 = task.getResult();
                                    assert document2 != null;
                                    if (document2.exists()) {
                                        String floor = document2.getString("floorlvl");
                                        String parkid = document2.getString("parkingID");
                                        if ("B1".equals(floor)) {
                                            assert parkid != null;
                                            Map<String, Object> parkingSlotStatus = new HashMap<>();
                                            parkingSlotStatus.put("Availability", "Available");
                                            parkingSlotStatus.put("User ID", FieldValue.delete());
                                            parkingSlotStatus.put("carPlate", FieldValue.delete());

                                            fStore.collection("parking").document("oneUtama")
                                                    .collection("ou_b1").document(parkid)
                                                    .set(parkingSlotStatus, SetOptions.merge());

                                            Map<String, Object> updateRecord = new HashMap<>();
                                            updateRecord.put("payment", "Cancelled");
                                            updateRecord.put("cancelTime", FieldValue.serverTimestamp());

                                            fStore.collection("users").document(userID).collection("Record")
                                                    .document(bookID).set(updateRecord, SetOptions.merge());
                                        } else if ("B2".equals(floor)) {
                                            assert parkid != null;
                                            Map<String, Object> parkingSlotStatus = new HashMap<>();
                                            parkingSlotStatus.put("Availability", "Available");
                                            parkingSlotStatus.put("User ID", FieldValue.delete());
                                            parkingSlotStatus.put("carPlate", FieldValue.delete());

                                            fStore.collection("parking").document("oneUtama")
                                                    .collection("ou_b2").document(parkid)
                                                    .set(parkingSlotStatus, SetOptions.merge());

                                            Map<String, Object> updateRecord = new HashMap<>();
                                            updateRecord.put("payment", "Cancelled");
                                            updateRecord.put("cancelTime", FieldValue.serverTimestamp());

                                            fStore.collection("users").document(userID).collection("Record")
                                                    .document(bookID).set(updateRecord, SetOptions.merge());
                                        }
                                    }
                                }
                            }
                        });

                        popupWindowCancel.dismiss();
                    }
                });
                popupWindowCancel.setFocusable(true);
                popupWindowCancel.showAsDropDown(popupViewCancel, 0, 0);

                dimBehind(popupWindowCancel);
            }

            public void dimBehind(PopupWindow popupWindow2) {
                View container = popupWindow2.getContentView().getRootView();
                Context context = popupWindow2.getContentView().getContext();
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.4f;
                wm.updateViewLayout(container, p);
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(RecordDetails.this).inflate(R.layout.pop_payment, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                TextView BookTime, ParkDuration, ParkPrice;
                BookTime = popupView.findViewById(R.id.bookingTime);
                ParkDuration = popupView.findViewById(R.id.parkDuration);
                ParkPrice = popupView.findViewById(R.id.parkPrice);
                Button btnClose = popupView.findViewById(R.id.closePopUp3);
                Button btnConfirm = popupView.findViewById(R.id.confirmPayment);
                ConstraintLayout DismissPop = popupView.findViewById(R.id.outsidePOPPayment);

                DismissPop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                DocumentReference documentReference1 = fStore.collection("users").document(userID)
                        .collection("Record").document(bookID);
                documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        Date bookDate = value.getDate("bookingTime");
                        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                        assert bookDate != null;
                        String creationDate = dateFormat.format(bookDate);
                        BookTime.setText(creationDate);

                        Date current = Calendar.getInstance().getTime();

                        long differentDate = current.getTime() - bookDate.getTime();

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        long elapsedHours = differentDate / hoursInMilli;
                        differentDate = differentDate % hoursInMilli;

                        long elapsedMinutes = differentDate / minutesInMilli;
                        differentDate = differentDate % minutesInMilli;

                        String b = String.valueOf(elapsedHours);
                        String d = String.valueOf(elapsedMinutes);

                        ParkDuration.setText(b + " Hour " + d + " Minute");

                        if (elapsedHours == 4 && elapsedMinutes <= 10) {
                            double price1 = 4 * 2;
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours <= 3 && elapsedHours >= 1 && elapsedMinutes >= 11) {
                            double price1 = (elapsedHours + 1) * 2;
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours <= 3 && elapsedHours >= 1 && elapsedMinutes <= 10) {
                            double price1 = (elapsedHours) * 2;
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours == 4 && elapsedMinutes >= 11) {
                            double price1 = ((4 * 2) + 1);
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours >= 5 && elapsedMinutes <= 10) {
                            double price1 = ((4 * 2) + (elapsedHours - 4));
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours >= 5 && elapsedMinutes >= 11) {
                            double price1 = ((4 * 2) + ((elapsedHours - 4) + 1));
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        } else if (elapsedHours == 0) {
                            double price1 = 2;
                            String totalPrice = String.format(Locale.getDefault(), "%.2f", price1);
                            ParkPrice.setText(totalPrice);
                        }
                        Double ans = Double.parseDouble((String) ParkPrice.getText());

                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DocumentReference documentReference = fStore.collection("users").document(userID).collection("E-Wallet").document("Cash");
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;
                                            if (document.exists()) {
                                                Double cashDB = document.getDouble("Money");

                                                if (Double.compare(ans, cashDB) <= 0) {
                                                    Double finalCash = cashDB - ans;

                                                    documentReference.update("Money", finalCash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            Toast.makeText(RecordDetails.this, "Payment Successful", Toast.LENGTH_SHORT).show();

                                                            Map<String, Object> payment = new HashMap<>();
                                                            payment.put("oldBalance", cashDB);
                                                            payment.put("paymentAmount", ans);
                                                            payment.put("newBalance", finalCash);
                                                            payment.put("recordRef", bookID);
                                                            payment.put("transactionMode", "Payment");
                                                            payment.put("transactionTime", FieldValue.serverTimestamp());

                                                            String paymentID = fStore.collection("users").document(userID).collection("E-Wallet")
                                                                    .document("Transaction").collection("TransactionRecord").document().getId();

                                                            fStore.collection("users").document(userID).collection("E-Wallet")
                                                                    .document("Transaction").collection("TransactionRecord")
                                                                    .document(paymentID).set(payment, SetOptions.merge());

                                                            String parkTime = elapsedHours + " Hours " + elapsedMinutes + " Minutes ";
                                                            Map<String, Object> updateRecord = new HashMap<>();
                                                            updateRecord.put("parkingTime", parkTime);
                                                            updateRecord.put("TransactionNo", paymentID);
                                                            updateRecord.put("price", ans);
                                                            updateRecord.put("payment", "PAID");
                                                            updateRecord.put("checkoutTime", FieldValue.serverTimestamp());

                                                            fStore.collection("users").document(userID).collection("Record")
                                                                    .document(bookID).set(updateRecord, SetOptions.merge());

                                                            DocumentReference documentReference4 = fStore.collection("users").document(userID)
                                                                    .collection("Record").document(bookID);
                                                            documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document2 = task.getResult();
                                                                        assert document2 != null;
                                                                        if (document2.exists()) {
                                                                            String floor = document2.getString("floorlvl");
                                                                            String parkid = document2.getString("parkingID");
                                                                            if ("B1".equals(floor)) {
                                                                                assert parkid != null;
                                                                                Map<String, Object> parkingSlotStatus = new HashMap<>();
                                                                                parkingSlotStatus.put("Availability", "Available");
                                                                                parkingSlotStatus.put("User ID", FieldValue.delete());
                                                                                parkingSlotStatus.put("carPlate", FieldValue.delete());

                                                                                fStore.collection("parking").document("oneUtama")
                                                                                        .collection("ou_b1").document(parkid)
                                                                                        .set(parkingSlotStatus, SetOptions.merge());
                                                                            } else if ("B2".equals(floor)) {
                                                                                assert parkid != null;
                                                                                Map<String, Object> parkingSlotStatus = new HashMap<>();
                                                                                parkingSlotStatus.put("Availability", "Available");
                                                                                parkingSlotStatus.put("User ID", FieldValue.delete());
                                                                                parkingSlotStatus.put("carPlate", FieldValue.delete());

                                                                                fStore.collection("parking").document("oneUtama")
                                                                                        .collection("ou_b2").document(parkid)
                                                                                        .set(parkingSlotStatus, SetOptions.merge());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                            btnCheckOut.setVisibility(View.GONE);
                                                            popupWindow.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(RecordDetails.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(RecordDetails.this, "Insufficient Cash. Please Top Up.", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        }
                                    }
                                });
                            }
                        });

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow.dismiss();
                            }
                        });
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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(RecordDetails.this,"right-to-left");
    }
}

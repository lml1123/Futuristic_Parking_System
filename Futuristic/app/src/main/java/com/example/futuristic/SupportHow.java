package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class SupportHow extends SupportPage{

    TextView BodySupportHow;
    ImageButton BtnBackHow;
    Button BtnTBrief, BtnTReservation, BtnTCheckOut, BtnTTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_support_how);

        BodySupportHow = findViewById(R.id.body_supportHow);
        BtnBackHow = findViewById(R.id.backSupportHow);
        BtnTBrief = findViewById(R.id.supportHowB);
        BtnTReservation = findViewById(R.id.supportHowR);
        BtnTCheckOut = findViewById(R.id.supportHowC);
        BtnTTopUp = findViewById(R.id.supportHowT);

        BodySupportHow.setText("Below part is the explaination for the Futuristic Parking System.");

        BtnBackHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportHow.this, SupportPage.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(SupportHow.this, "right-to-left");
            }
        });

        BtnTBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportHow.this, TutorialB.class));
                customType(SupportHow.this, "left-to-right");
            }
        });

        BtnTReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportHow.this, TutorialR.class));
                customType(SupportHow.this, "left-to-right");
            }
        });

        BtnTCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportHow.this, TutorialCO.class));
                customType(SupportHow.this, "left-to-right");
            }
        });

        BtnTTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportHow.this, TutorialTU.class));
                customType(SupportHow.this, "left-to-right");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SupportHow.this, SupportPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SupportHow.this, "right-to-left");
    }
}

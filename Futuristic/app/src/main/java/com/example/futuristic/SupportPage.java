package com.example.futuristic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codesgood.views.JustifiedTextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class SupportPage extends ProfilePage {

    TextView bodySupport;
    ImageButton btnBack;
    ImageView picSupport;
    Button btnPhone, btnEmail, btnHow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_support);

        bodySupport = findViewById(R.id.body_support);

        picSupport = findViewById(R.id.pic_support);
        btnBack = findViewById(R.id.backSupport);
        btnPhone = findViewById(R.id.supportPhone);
        btnEmail = findViewById(R.id.supportEmail);
        btnHow = findViewById(R.id.supportHow);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportPage.this, ProfilePage.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(SupportPage.this, "right-to-left");
            }
        });

        bodySupport.setText("Any inquiries please make a call or email us. " +
                "Our customer service representative will get to you as soon as possible." +
                "The best in you make possible. Thank you!");

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+60189199112"));
                startActivity(callIntent);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportPage.this, SupportEmail.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(SupportPage.this, "left-to-right");
            }
        });

        btnHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportPage.this, SupportHow.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                customType(SupportPage.this, "left-to-right");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SupportPage.this, ProfilePage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SupportPage.this, "right-to-left");
    }
}

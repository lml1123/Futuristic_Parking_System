package com.example.futuristic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codesgood.views.JustifiedTextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class AboutPage extends ProfilePage {

    TextView titleAbout;
    JustifiedTextView bodyAbout, body2About;
    ImageButton btnBack;
    ImageView picAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_aboutus);

        titleAbout = findViewById(R.id.title_aboutUs);
        bodyAbout = findViewById(R.id.body_aboutUs);
        body2About = findViewById(R.id.body2_aboutUs);

        picAbout = findViewById(R.id.pic_aboutUs);
        btnBack = findViewById(R.id.backAboutUs);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutPage.this, ProfilePage.class));
                customType(AboutPage.this, "right-to-left");
                finish();
            }
        });

        titleAbout.setText("Futuristic Parking System");

        bodyAbout.setText("A parking system that allows the users to make a reservation of the parking slots." +
                "This project was started from January 2021.");

        body2About.setText("This application is owned and created by Lee Men Long (SUKD1701108), " +
                "Bachelor of Information Technology Students, Final Year Project.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(AboutPage.this, "right-to-left");
        finish();
    }
}

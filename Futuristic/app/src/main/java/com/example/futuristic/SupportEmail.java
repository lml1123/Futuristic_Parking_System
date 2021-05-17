package com.example.futuristic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

import static maes.tech.intentanim.CustomIntent.customType;

public class SupportEmail extends SupportPage {

    ImageButton btnBack;
    TextInputLayout SubjectEmail, ContentEmail;
    Button BtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_email_support);

        btnBack = findViewById(R.id.backEmail);
        SubjectEmail = findViewById(R.id.subjectEmail);
        ContentEmail = findViewById(R.id.contentEmail);
        BtnSend = findViewById(R.id.emailSend);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportEmail.this, SupportPage.class));
                customType(SupportEmail.this, "right-to-left");
                finish();
            }
        });

        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String subject = SubjectEmail.getEditText().getText().toString();
                String content = ContentEmail.getEditText().getText().toString();

                String[] TO = {"lmlong1123@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");

                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(emailIntent,"Choose an email client"));

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SupportEmail.this, SupportPage.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(SupportEmail.this, "right-to-left");
    }
}

package com.example.futuristic;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codesgood.views.JustifiedTextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class TermsConditions extends ProfilePage {

    TextView titleTerm;
    JustifiedTextView bodyTerms, body2Terms;
    ImageButton btnBackTerms;
    ImageView picTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_terms);

        titleTerm = findViewById(R.id.title_terms);
        bodyTerms = findViewById(R.id.body_terms);
        body2Terms = findViewById(R.id.body2_terms);
        picTerms = findViewById(R.id.pic_terms);

        btnBackTerms = findViewById(R.id.backTerms);

        btnBackTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TermsConditions.this, ProfilePage.class));
                customType(TermsConditions.this, "right-to-left");
                finish();
            }
        });

        titleTerm.setText("TERMS OF USE");

        bodyTerms.setText("You will need to register for a futuristic account for you to use the application." +
                "When you register for an account we will ask you to provide your personal information including" +
                "a valid email address, a valid mobile phone number, fullname and a unique password. We will" +
                "request you to verify you phone number to finish the register progress.");


        body2Terms.setText("We may change our Service and policies, and we may need to make changes to these Terms so that" +
                "they accurately reflect out Service and policies. Unless otherwise required by law, we will notify you before" +
                "we make changes to these Terms and give you an opportunity to review them before they go into effect.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(TermsConditions.this, "right-to-left");
        finish();
    }
}

package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codesgood.views.JustifiedTextView;

import static maes.tech.intentanim.CustomIntent.customType;

public class NewsPage extends ProfilePage {

    TextView titleNews, timeNews;
    JustifiedTextView bodyNews, body2News;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_news);

        titleNews = findViewById(R.id.title_news);
        timeNews = findViewById(R.id.time_news);
        bodyNews = findViewById(R.id.body_news);
        body2News = findViewById(R.id.body2_news);

        btnBack = findViewById(R.id.backNews);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsPage.this, ProfilePage.class));
                customType(NewsPage.this, "right-to-left");
                finish();
            }
        });

        titleNews.setText("Futuristic Parking System");

        timeNews.setText("23-Mar-2021");

        bodyNews.setText("The application will be maintenance on 28 Mar 2021......");

        body2News.setText("The application was released into market on 21 Mar 2021......");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(NewsPage.this, "right-to-left");
        finish();
    }
}

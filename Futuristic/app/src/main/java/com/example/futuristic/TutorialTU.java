package com.example.futuristic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import static maes.tech.intentanim.CustomIntent.customType;

public class TutorialTU extends SupportPage {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    private TutorialTUAdapter sliderAdapter;
    private Button mNextBtn, mPreBtn, mSkipBtn;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_slide_skip);

        mSlideViewPager = (ViewPager) findViewById(R.id.tutorialSlideSkip);
        mDotLayout = (LinearLayout) findViewById(R.id.tutorialLinearSkip);

        mNextBtn = (Button) findViewById(R.id.tutorialNextSkip);
        mPreBtn = (Button) findViewById(R.id.tutorialPreviousSkip);
        mSkipBtn = (Button) findViewById(R.id.tutorialSkip);

        sliderAdapter = new TutorialTUAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGrey));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0) {
                mNextBtn.setEnabled(true);
                mPreBtn.setEnabled(false);
                mSkipBtn.setEnabled(true);
                mNextBtn.setVisibility(View.VISIBLE);
                mPreBtn.setVisibility(View.INVISIBLE);
            } else if (position == mDots.length - 1) {
                mNextBtn.setEnabled(false);
                mPreBtn.setEnabled(true);
                mSkipBtn.setEnabled(true);
                mNextBtn.setVisibility(View.INVISIBLE);
                mPreBtn.setVisibility(View.VISIBLE);
            } else {
                mNextBtn.setEnabled(true);
                mPreBtn.setEnabled(true);
                mSkipBtn.setEnabled(true);
                mNextBtn.setVisibility(View.VISIBLE);
                mPreBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TutorialTU.this, SupportHow.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        customType(TutorialTU.this, "right-to-left");
    }
}

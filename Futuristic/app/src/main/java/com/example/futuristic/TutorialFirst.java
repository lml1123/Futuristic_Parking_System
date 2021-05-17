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

public class TutorialFirst extends SupportPage {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    private TutorialFirstAdapter sliderAdapter;
    private Button mNextBtn, mPreBtn, mFinBtn;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_slide);

        mSlideViewPager = (ViewPager) findViewById(R.id.tutorialSlide);
        mDotLayout = (LinearLayout) findViewById(R.id.tutorialLinear);

        mNextBtn = (Button) findViewById(R.id.tutorialNext);
        mPreBtn = (Button) findViewById(R.id.tutorialPrevious);
        mFinBtn = (Button) findViewById(R.id.tutorialFinish);

        sliderAdapter = new TutorialFirstAdapter(this);

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

        mFinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialFirst.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[7];
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
                mFinBtn.setEnabled(false);
                mNextBtn.setVisibility(View.VISIBLE);
                mPreBtn.setVisibility(View.INVISIBLE);
                mFinBtn.setVisibility(View.GONE);
            } else if (position == mDots.length - 1) {
                mNextBtn.setEnabled(false);
                mPreBtn.setEnabled(true);
                mFinBtn.setEnabled(true);
                mNextBtn.setVisibility(View.GONE);
                mPreBtn.setVisibility(View.VISIBLE);
                mFinBtn.setVisibility(View.VISIBLE);
            } else {
                mNextBtn.setEnabled(true);
                mPreBtn.setEnabled(true);
                mFinBtn.setEnabled(false);
                mNextBtn.setVisibility(View.VISIBLE);
                mPreBtn.setVisibility(View.VISIBLE);
                mFinBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
    }
}

package com.example.futuristic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class TutorialBAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public TutorialBAdapter(Context context){
        this.context  = context;
    }

    public int[] slide_images = {
        R.drawable.tutorial1,
        R.drawable.tutorial2,
        R.drawable.tutorial3,
        R.drawable.tutorial4,
        R.drawable.tutorial5,
        R.drawable.tutorial6
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.tutorial_content, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        slideImageView.setImageResource(slide_images[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}

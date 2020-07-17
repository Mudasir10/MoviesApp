package com.mudasir.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mudasir.moviesapp.R;
import com.mudasir.moviesapp.activities.TrailorPlayer;
import com.mudasir.moviesapp.models.Slide;

import java.util.List;

public class SlidesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Slide> slides;

    public SlidesPagerAdapter(Context mContext, List<Slide> slides) {
        this.mContext = mContext;
        this.slides = slides;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view=inflater.inflate(R.layout.slide_item,null);

        ImageView slideimage=view.findViewById(R.id.slide_image);
        TextView slidetitle=view.findViewById(R.id.slide_title);
        FloatingActionButton floatingActionButton=view.findViewById(R.id.btnPlay);

        Glide.with(mContext).load(slides.get(position).getThumbnail()).into(slideimage);
        //slidetitle.setText(slides.get(position).getTitle());
        slidetitle.setVisibility(View.INVISIBLE);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,TrailorPlayer.class);
                intent.putExtra("video_url",slides.get(position).getStreamingLink());
                mContext.startActivity(intent);
            }
        });

        container.addView(view);
        return view;

    }

    @Override
    public int getCount() {
        return slides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

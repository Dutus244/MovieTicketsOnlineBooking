package com.example.movieticketsonlinebooking.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.movieticketsonlinebooking.R;
import com.example.movieticketsonlinebooking.activities.FilmInfoActivity;
import com.example.movieticketsonlinebooking.viewmodels.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class The_Slide_items_Pager_Adapter_HomePage extends PagerAdapter {

    private Context Mcontext;
    private List<Movie> theSlideItemsModelClassList;

    public The_Slide_items_Pager_Adapter_HomePage(Context Mcontext, List<Movie> theSlideItemsModelClassList) {
        this.Mcontext = Mcontext;
        this.theSlideItemsModelClassList = theSlideItemsModelClassList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.activity_home_page_the_items_layout, null);

        ImageView featured_image = sliderLayout.findViewById(R.id.activity_home_page_my_featured_image);
        TextView caption_title = sliderLayout.findViewById(R.id.activity_home_page_my_caption_title);

        Picasso.get().load(theSlideItemsModelClassList.get(position).getPoster_url()).into(featured_image);
        caption_title.setText(theSlideItemsModelClassList.get(position).getTitle());
        sliderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to launch the new activity
                Intent intent = new Intent(Mcontext, FilmInfoActivity.class);
                // You can pass any data to the new activity using the intent
                intent.putExtra("movie", theSlideItemsModelClassList.get(position));
                // Start the new activity
                Mcontext.startActivity(intent);
            }
        });

        container.addView(sliderLayout);

        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return theSlideItemsModelClassList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}

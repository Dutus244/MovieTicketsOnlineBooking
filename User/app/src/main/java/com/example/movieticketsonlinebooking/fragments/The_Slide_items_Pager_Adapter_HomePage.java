package com.example.movieticketsonlinebooking.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.movieticketsonlinebooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class The_Slide_items_Pager_Adapter_HomePage extends PagerAdapter {

    private Context Mcontext;
    private List<com.example.movieticketsonlinebooking.fragments.The_Slide_Items_Model_Class_HomePage> theSlideItemsModelClassList;

    public The_Slide_items_Pager_Adapter_HomePage(Context Mcontext, List<com.example.movieticketsonlinebooking.fragments.The_Slide_Items_Model_Class_HomePage> theSlideItemsModelClassList) {
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

        Picasso.get().load(theSlideItemsModelClassList.get(position).getFeatured_image()).into(featured_image);
        caption_title.setText(theSlideItemsModelClassList.get(position).getThe_caption_Title());
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

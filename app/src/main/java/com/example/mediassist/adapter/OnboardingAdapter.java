package com.example.mediassist.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.mediassist.R;
import com.example.mediassist.data.models.OnboardingItem;

import java.util.List;

public class OnboardingAdapter extends PagerAdapter {

    private Context context;
    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(Context context, List<OnboardingItem> onboardingItems) {
        this.context = context;
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.onboarding_screen_item, container, false);

        ImageView imageView = view.findViewById(R.id.onboardingImage);
        TextView titleTextView = view.findViewById(R.id.onboardingTitle);
        TextView descriptionTextView = view.findViewById(R.id.onboardingDescription);

        imageView.setImageResource(onboardingItems.get(position).getImageResource());
        titleTextView.setText(onboardingItems.get(position).getTitle());
        descriptionTextView.setText(onboardingItems.get(position).getDescription());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return onboardingItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
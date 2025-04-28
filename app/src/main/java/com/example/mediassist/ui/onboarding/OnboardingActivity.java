package com.example.mediassist.ui.onboarding;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.adapter.OnboardingAdapter;
import com.example.mediassist.data.models.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip, btnNext;
    private OnboardingAdapter adapter;
    private ImageView[] dots;
    private List<OnboardingItem> onboardingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make navigation bar transparent
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Make the content display behind navigation bar (immersive mode)
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);

        // Hide action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_onboarding);

        // Initialize views
        viewPager = findViewById(R.id.onboardingViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);

        // Prepare onboarding items
        setupOnboardingItems();

        // Set up the adapter
        adapter = new OnboardingAdapter(this, onboardingItems);
        viewPager.setAdapter(adapter);

        // Set up the indicator dots
        setupIndicatorDots();

        // Set the current dot
        setCurrentIndicatorDot(0);

        // Set page change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentIndicatorDot(position);

                // Update button text on last page
                if (position == onboardingItems.size() - 1) {
                    btnNext.setText("Commencer");
                } else {
                    btnNext.setText("Suivant");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Skip button click listener
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        // Next button click listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();

                if (currentPosition < onboardingItems.size() - 1) {
                    // Go to next page
                    viewPager.setCurrentItem(currentPosition + 1);
                } else {
                    // On last page, finish onboarding
                    finishOnboarding();
                }
            }
        });
    }

    private void setupOnboardingItems() {
        onboardingItems = new ArrayList<>();

        // First screen - Welcome
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding_0, // Replace with your actual drawable resource
                "Bienvenue sur MediAssist",
                "Votre assistant personnel pour la gestion de vos médicaments et rendez-vous médicaux."
        ));

        // Second screen - Medications
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding_1, // Replace with your actual drawable resource
                "Gestion de Médicaments",
                "Suivez facilement vos médicaments, avec des rappels et des informations détaillées."
        ));

        // Third screen - Appointments
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding_2, // Replace with your actual drawable resource
                "Gestion des Rendez-vous",
                "Organisez et ne manquez jamais vos rendez-vous médicaux grâce aux notifications."
        ));

        // Fourth screen - Prescriptions
        onboardingItems.add(new OnboardingItem(
                R.drawable.onboarding_3, // Replace with your actual drawable resource
                "Suivi d'Ordonnances",
                "Gardez une trace de toutes vos ordonnances et renouvellements en un seul endroit."
        ));
    }

    private void setupIndicatorDots() {
        dots = new ImageView[onboardingItems.size()];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[i], params);
        }
    }

    private void setCurrentIndicatorDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(i == position ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void finishOnboarding() {
        // Save that onboarding is completed
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstTimeLaunch", false);
        editor.apply();

        // Start main activity
        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        finish();
    }
}
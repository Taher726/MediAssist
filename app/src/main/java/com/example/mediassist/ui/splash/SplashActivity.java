package com.example.mediassist.ui.splash;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen duration in milliseconds
    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds

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

        setContentView(R.layout.activity_splash);

        // Hide action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handler to delay and then navigate
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkFirstTimeUser();
            }
        }, SPLASH_TIMEOUT);
    }

    private void checkFirstTimeUser() {
        // Check if it's first time launch
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        boolean isFirstTimeLaunch = prefs.getBoolean("isFirstTimeLaunch", true);

        Intent intent;

        if (isFirstTimeLaunch) {
            // First time user, show onboarding
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        } else {
            // Returning user, go directly to main screen
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);

        // Apply transition animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Close this activity
        finish();
    }
}
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
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.ui.auth.LoginActivity;
import com.example.mediassist.ui.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

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

        // Initialize database in background
        new Thread(() -> {
            // This ensures database is created and default user exists
            DatabaseHelper.getInstance(SplashActivity.this);

            runOnUiThread(() -> {
                // Handler to delay and then navigate
                new Handler().postDelayed(() -> {
                    checkFirstTimeUser();
                }, SPLASH_TIMEOUT);
            });
        }).start();
    }

    private void checkFirstTimeUser() {
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        boolean isFirstTimeLaunch = prefs.getBoolean("isFirstTimeLaunch", true);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        Intent intent;

        if (isFirstTimeLaunch) {
            // First time: show Onboarding
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            // Mark that we've launched at least once
            prefs.edit().putBoolean("isFirstTimeLaunch", false).apply();
        } else {
            if (isLoggedIn) {
                // Already logged in, go to Home
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Not logged in, go to Login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
        }

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
package com.womensafety.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.womensafety.app.R;
import com.womensafety.app.utils.SessionManager;
import com.womensafety.app.utils.ShakeDetector;

public class HomeActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener {

    private SessionManager sessionManager;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(sessionManager.getName());

        MaterialButton btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v ->
                startActivity(new Intent(this, SOSActivity.class)));

        CardView cardContacts = findViewById(R.id.cardContacts);
        CardView cardHistory = findViewById(R.id.cardHistory);
        CardView cardProfile = findViewById(R.id.cardProfile);
        CardView cardSettings = findViewById(R.id.cardSettings);

        cardContacts.setOnClickListener(v ->
                startActivity(new Intent(this, TrustedContactsActivity.class)));
        cardHistory.setOnClickListener(v ->
                startActivity(new Intent(this, SOSHistoryActivity.class)));
        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        cardSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isShakeSOSEnabled()) {
            shakeDetector.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    @Override
    public void onShake() {
        Intent intent = new Intent(this, SOSActivity.class);
        intent.putExtra("from_shake", true);
        startActivity(intent);
    }
}

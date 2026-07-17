package com.womensafety.app.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.womensafety.app.R;
import com.womensafety.app.api.RetrofitClient;
import com.womensafety.app.models.ApiResponse;
import com.womensafety.app.models.Contact;
import com.womensafety.app.models.SOS;
import com.womensafety.app.utils.LocationHelper;
import com.womensafety.app.utils.SessionManager;
import com.womensafety.app.utils.SmsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SOSActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
    };

    private TextView tvCountdown, tvStatus, tvLocation;
    private MaterialButton btnTriggerSOS, btnCancel;
    private ProgressBar progressBar;
    private View pulseRing;
    private SessionManager sessionManager;
    private LocationHelper locationHelper;
    private CountDownTimer countDownTimer;
    private boolean sosTriggered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        sessionManager = new SessionManager(this);
        locationHelper = new LocationHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvCountdown = findViewById(R.id.tvCountdown);
        tvStatus = findViewById(R.id.tvStatus);
        tvLocation = findViewById(R.id.tvLocation);
        btnTriggerSOS = findViewById(R.id.btnTriggerSOS);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        pulseRing = findViewById(R.id.pulseRing);

        startPulseAnimation();

        btnTriggerSOS.setOnClickListener(v -> startCountdown());
        btnCancel.setOnClickListener(v -> cancelCountdown());

        if (getIntent().getBooleanExtra("from_shake", false)) {
            startCountdown();
        }

        checkPermissions();
        updateLocationDisplay();
    }

    private void startPulseAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(pulseRing, "scaleX", 1f, 1.15f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(pulseRing, "scaleY", 1f, 1.15f, 1f);
        scaleX.setDuration(1500);
        scaleY.setDuration(1500);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }

    private void startCountdown() {
        if (sosTriggered) return;

        btnTriggerSOS.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        tvStatus.setText("SOS will trigger in...");

        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                triggerSOS();
            }
        }.start();
    }

    private void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        tvCountdown.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnTriggerSOS.setVisibility(View.VISIBLE);
        tvStatus.setText(getString(R.string.sos_description));
    }

    private void triggerSOS() {
        sosTriggered = true;
        tvCountdown.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        tvStatus.setText(getString(R.string.sos_triggered));
        progressBar.setVisibility(View.VISIBLE);

        locationHelper.getCurrentLocation(new LocationHelper.LocationCallbackListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                tvLocation.setText(String.format("Lat: %.4f, Lng: %.4f", latitude, longitude));
                saveSOSToServer(latitude, longitude);
                fetchContactsAndSendSMS(latitude, longitude);
            }

            @Override
            public void onLocationError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SOSActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSOSToServer(double lat, double lng) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", sessionManager.getUserId());
        body.put("latitude", lat);
        body.put("longitude", lng);

        RetrofitClient.getInstance().getApiService().triggerSOS(body)
                .enqueue(new Callback<ApiResponse<SOS>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SOS>> call, Response<ApiResponse<SOS>> response) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<SOS>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void fetchContactsAndSendSMS(double lat, double lng) {
        RetrofitClient.getInstance().getApiService()
                .getContacts(sessionManager.getUserId())
                .enqueue(new Callback<ApiResponse<List<Contact>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Contact>>> call,
                                           Response<ApiResponse<List<Contact>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<Contact> contacts = response.body().getData();
                            if (contacts != null && !contacts.isEmpty()) {
                                SmsHelper.sendSOSToContacts(SOSActivity.this, contacts, lat, lng,
                                        sessionManager.getName());
                                SmsHelper.makeEmergencyCall(SOSActivity.this, contacts.get(0).getPhoneNumber());
                                tvStatus.setText(getString(R.string.sending_alerts));
                            } else {
                                Toast.makeText(SOSActivity.this, "No trusted contacts found",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Contact>>> call, Throwable t) {
                        Toast.makeText(SOSActivity.this, "Failed to fetch contacts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateLocationDisplay() {
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallbackListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                tvLocation.setText(String.format("Current: %.4f, %.4f", latitude, longitude));
            }

            @Override
            public void onLocationError(String error) {
                tvLocation.setText("Location unavailable");
            }
        });
    }

    private void checkPermissions() {
        boolean allGranted = true;
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        locationHelper.stopUpdates();
    }
}

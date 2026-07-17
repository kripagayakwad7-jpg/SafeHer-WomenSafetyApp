package com.womensafety.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.womensafety.app.R;
import com.womensafety.app.api.RetrofitClient;
import com.womensafety.app.models.ApiResponse;
import com.womensafety.app.models.User;
import com.womensafety.app.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone;
    private TextInputEditText etName, etEmail, etPhone;
    private MaterialButton btnSave, btnBack;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        RetrofitClient.getInstance().getApiService()
                .getProfile(sessionManager.getUserId())
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            User user = response.body().getData();
                            tvName.setText(user.getName());
                            tvEmail.setText(user.getEmail());
                            tvPhone.setText(user.getPhone());
                            etName.setText(user.getName());
                            etEmail.setText(user.getEmail());
                            etPhone.setText(user.getPhone());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        tvName.setText(sessionManager.getName());
                        tvEmail.setText(sessionManager.getEmail());
                        tvPhone.setText(sessionManager.getPhone());
                        etName.setText(sessionManager.getName());
                        etEmail.setText(sessionManager.getEmail());
                        etPhone.setText(sessionManager.getPhone());
                    }
                });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("phone", phone);

        RetrofitClient.getInstance().getApiService()
                .updateProfile(sessionManager.getUserId(), body)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            User user = response.body().getData();
                            sessionManager.saveSession(user.getUserId(), sessionManager.getToken(),
                                    user.getName(), user.getEmail(), user.getPhone());
                            tvName.setText(user.getName());
                            tvEmail.setText(user.getEmail());
                            tvPhone.setText(user.getPhone());
                            Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : "Update failed";
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

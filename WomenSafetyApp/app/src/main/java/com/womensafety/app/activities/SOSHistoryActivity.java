package com.womensafety.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.womensafety.app.R;
import com.womensafety.app.adapters.SOSHistoryAdapter;
import com.womensafety.app.api.RetrofitClient;
import com.womensafety.app.models.ApiResponse;
import com.womensafety.app.models.SOS;
import com.womensafety.app.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SOSHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private SOSHistoryAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_history);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvHistory);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new SOSHistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        RetrofitClient.getInstance().getApiService()
                .getSOSHistory(sessionManager.getUserId())
                .enqueue(new Callback<ApiResponse<List<SOS>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<SOS>>> call,
                                           Response<ApiResponse<List<SOS>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<SOS> history = response.body().getData();
                            adapter.setHistory(history);
                            tvEmpty.setVisibility(history == null || history.isEmpty()
                                    ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<SOS>>> call, Throwable t) {
                        Toast.makeText(SOSHistoryActivity.this, "Failed to load history",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

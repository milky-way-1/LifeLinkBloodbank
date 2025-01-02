package com.example.lifelinkbloodbank;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lifelinkbloodbank.api.ApiService;
import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.BloodRequest;
import com.example.lifelinkbloodbank.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodBankDashboard extends AppCompatActivity {

    private RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private BloodRequestAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    private static final int POLLING_INTERVAL = 10000; // 10 seconds
    private Handler pollingHandler;
    private boolean isPollingActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_dashboard);

        setupServices();
        setupViews();
        setupPolling();
        loadRequests();
    }

    private void setupServices() {
        apiService = RetrofitClient.getInstance().getApiService();
        sessionManager = new SessionManager(this);
    }

    private void setupViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodRequestAdapter();
        requestsRecyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> {
            loadRequests();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void setupPolling() {
        pollingHandler = new Handler(Looper.getMainLooper());
        startPolling();
    }

    private void startPolling() {
        isPollingActive = true;
        pollingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPollingActive) {
                    loadRequests();
                    pollingHandler.postDelayed(this, POLLING_INTERVAL);
                }
            }
        }, POLLING_INTERVAL);
    }

    private void stopPolling() {
        isPollingActive = false;
        if (pollingHandler != null) {
            pollingHandler.removeCallbacksAndMessages(null);
        }
    }

    private void loadRequests() {
        String token = "Bearer " + sessionManager.getToken();
        String bloodBankId = sessionManager.getUserId();

        if (token == null || bloodBankId == null) {
            showError("Authentication error");
            return;
        }

        apiService.getBloodRequests(token, bloodBankId)
                .enqueue(new Callback<List<BloodRequest>>() {
                    @Override
                    public void onResponse(Call<List<BloodRequest>> call,
                                           Response<List<BloodRequest>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateRequestsList(response.body());
                        } else {
                            showError("Failed to load requests");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BloodRequest>> call, Throwable t) {
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void updateRequestsList(List<BloodRequest> requests) {
        runOnUiThread(() -> {
            if (requests.isEmpty()) {
                // Show empty state
                // findViewById(R.id.emptyState).setVisibility(View.VISIBLE);
                requestsRecyclerView.setVisibility(View.GONE);
            } else {
                // findViewById(R.id.emptyState).setVisibility(View.GONE);
                requestsRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(requests);
            }
        });
    }

    private void showError(String message) {
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling();
        loadRequests(); // Load immediately when activity resumes
    }

    @Override
    protected void onPause() {
        stopPolling();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopPolling();
        super.onDestroy();
    }
}
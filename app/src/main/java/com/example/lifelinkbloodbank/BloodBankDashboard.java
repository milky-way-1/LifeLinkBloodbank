package com.example.lifelinkbloodbank;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lifelinkbloodbank.api.RetrofitClient;
import com.example.lifelinkbloodbank.model.BloodRequest;
import com.example.lifelinkbloodbank.model.BloodRequestUI;
import com.example.lifelinkbloodbank.model.Hospital;
import com.example.lifelinkbloodbank.model.RequestStatusUpdate;
import com.example.lifelinkbloodbank.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodBankDashboard extends AppCompatActivity implements BloodRequestAdapter.OnRequestActionListener {

    private RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private View emptyStateView;
    private View loadingView;
    private BloodRequestAdapter adapter;
    private SessionManager sessionManager;
    private final Map<String, Hospital> hospitalCache = new HashMap<>();

    private static final int POLLING_INTERVAL = 10000; // 10 seconds
    private Handler pollingHandler;
    private boolean isPollingActive = false;
    private static final String TAG = "BloodBankDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_dashboard);

        setupViews();
        setupServices();
        setupPolling();
        loadRequests();
    }

    private void setupViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        emptyStateView = findViewById(R.id.emptyState);
        loadingView = findViewById(R.id.loadingView);

        adapter = new BloodRequestAdapter(this);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsRecyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> {
            loadRequests();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void setupServices() {
        sessionManager = new SessionManager(this);
    }

    private void setupPolling() {
        pollingHandler = new Handler(Looper.getMainLooper());
        startPolling();
    }

    private void loadRequests() {
        showLoading(true);
        String token = "Bearer " + sessionManager.getToken();
        String bloodBankId = sessionManager.getUserId();

        if (token == null || bloodBankId == null) {
            showError("Authentication error");
            showLoading(false);
            return;
        }

        RetrofitClient.getInstance()
                .getApiService()
                .getBloodRequests(token, bloodBankId)
                .enqueue(new Callback<List<BloodRequest>>() {
                    @Override
                    public void onResponse(Call<List<BloodRequest>> call,
                                           Response<List<BloodRequest>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            processBloodRequests(response.body());
                        } else {
                            showError("Failed to load requests");
                            showLoading(false);
                            showEmptyState(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BloodRequest>> call, Throwable t) {
                        Log.e(TAG, "Network error", t);
                        showError("Network error: " + t.getMessage());
                        showLoading(false);
                        showEmptyState(true);
                    }
                });
    }

    private void processBloodRequests(List<BloodRequest> requests) {
        if (requests.isEmpty()) {
            showEmptyState(true);
            showLoading(false);
            return;
        }

        List<BloodRequestUI> uiRequests = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(requests.size());

        for (BloodRequest request : requests) {
            if (hospitalCache.containsKey(request.getHospitalId())) {
                Hospital hospital = hospitalCache.get(request.getHospitalId());
                uiRequests.add(createUIRequest(request, hospital));
                if (pendingRequests.decrementAndGet() == 0) {
                    updateAdapter(uiRequests);
                }
            } else {
                fetchHospitalDetails(request, hospital -> {
                    hospitalCache.put(hospital.getId(), hospital);
                    uiRequests.add(createUIRequest(request, hospital));
                    if (pendingRequests.decrementAndGet() == 0) {
                        updateAdapter(uiRequests);
                    }
                });
            }
        }
    }

    private void fetchHospitalDetails(BloodRequest request, OnHospitalFetchedListener listener) {
        String token = "Bearer " + sessionManager.getToken();

        RetrofitClient.getInstance()
                .getApiService()
                .getHospitalDetails(token, request.getHospitalId())
                .enqueue(new Callback<Hospital>() {
                    @Override
                    public void onResponse(Call<Hospital> call, Response<Hospital> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listener.onHospitalFetched(response.body());
                        } else {
                            Log.e(TAG, "Failed to fetch hospital details: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Hospital> call, Throwable t) {
                        Log.e(TAG, "Network error fetching hospital details", t);
                    }
                });
    }

    private BloodRequestUI createUIRequest(BloodRequest request, Hospital hospital) {
        BloodRequestUI uiRequest = new BloodRequestUI(request);
        uiRequest.setHospitalName(hospital.getHospitalName());
        uiRequest.setAddress(hospital.getFullAddress());
        uiRequest.setContactNumber(hospital.getPhoneNumber());
        return uiRequest;
    }

    private void updateAdapter(List<BloodRequestUI> requests) {
        runOnUiThread(() -> {
            showEmptyState(false);
            showLoading(false);
            adapter.submitList(requests);
        });
    }

    @Override
    public void onAcceptRequest(BloodRequestUI request) {
        updateRequestStatus(request, "ACCEPTED");
    }

    @Override
    public void onRejectRequest(BloodRequestUI request) {
        updateRequestStatus(request, "REJECTED");
    }

    private void updateRequestStatus(BloodRequestUI request, String newStatus) {
        String token = "Bearer " + sessionManager.getToken();
        RequestStatusUpdate statusUpdate = new RequestStatusUpdate(newStatus);

        RetrofitClient.getInstance()
                .getApiService()
                .updateRequestStatus(token, request.getId(), statusUpdate)
                .enqueue(new Callback<BloodRequest>() {
                    @Override
                    public void onResponse(Call<BloodRequest> call, Response<BloodRequest> response) {
                        if (response.isSuccessful()) {
                            showSuccess("Request " + newStatus.toLowerCase());
                            loadRequests(); // Reload the list
                        } else {
                            if (response.code() == 404) {
                                showError("Request not found");
                            } else if (response.code() == 400) {
                                showError("Invalid status update");
                            } else {
                                showError("Failed to update request");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BloodRequest> call, Throwable t) {
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefresh.setEnabled(!show);
    }

    private void showEmptyState(boolean show) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        requestsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }

    private void showSuccess(String message) {
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }

    private void startPolling() {
        if (!isPollingActive) {
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
    }

    private void stopPolling() {
        isPollingActive = false;
        if (pollingHandler != null) {
            pollingHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling();
        loadRequests();
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

    interface OnHospitalFetchedListener {
        void onHospitalFetched(Hospital hospital);
    }
}
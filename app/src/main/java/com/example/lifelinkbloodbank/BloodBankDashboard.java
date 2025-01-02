package com.example.lifelinkbloodbank;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lifelinkbloodbank.model.BloodRequest;

import java.util.List;

public class BloodBankDashboard extends AppCompatActivity {

    private RecyclerView requestsRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private BloodRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_dashboard);

        setupViews();
        loadRequests();
    }

    private void setupViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodRequestAdapter();
        requestsRecyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadRequests);
    }

    private void loadRequests() {
        // TODO: Load requests from your backend
//        List<BloodRequest> requests = getDummyRequests();
//        adapter.submitList(requests);
//        swipeRefresh.setRefreshing(false);
    }
}
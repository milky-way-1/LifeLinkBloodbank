package com.example.lifelinkbloodbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifelinkbloodbank.model.BloodRequest;

public class BloodRequestAdapter extends ListAdapter<BloodRequest, BloodRequestAdapter.ViewHolder> {

    protected BloodRequestAdapter() {
        super(new DiffUtil.ItemCallback<BloodRequest>() {
            @Override
            public boolean areItemsTheSame(@NonNull BloodRequest oldItem,
                                           @NonNull BloodRequest newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull BloodRequest oldItem,
                                              @NonNull BloodRequest newItem) {
                return oldItem.getStatus().equals(newItem.getStatus());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodRequest request = getItem(position);
        holder.bind(request);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hospitalName;
        private final TextView bloodType;
        private final TextView address;
        private final TextView phoneNumber;
        private final Button acceptButton;

        ViewHolder(View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            bloodType = itemView.findViewById(R.id.bloodType);
            address = itemView.findViewById(R.id.address);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }

        void bind(BloodRequest request) {
            hospitalName.setText(request.getHospitalName());
            bloodType.setText(request.getBloodType());
            address.setText(request.getAddress());
            phoneNumber.setText(request.getContactNumber());

            acceptButton.setOnClickListener(v -> {
                // Handle accept click
                // You might want to add a callback here
            });
        }
    }
}
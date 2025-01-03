package com.example.lifelinkbloodbank;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifelinkbloodbank.R;
import com.example.lifelinkbloodbank.model.BloodRequestUI;
import com.google.android.material.button.MaterialButton;

public class BloodRequestAdapter extends ListAdapter<BloodRequestUI, BloodRequestAdapter.ViewHolder> {

    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAcceptRequest(BloodRequestUI request);
        void onRejectRequest(BloodRequestUI request);
    }

    public BloodRequestAdapter(OnRequestActionListener listener) {
        super(new DiffUtil.ItemCallback<BloodRequestUI>() {
            @Override
            public boolean areItemsTheSame(@NonNull BloodRequestUI oldItem,
                                           @NonNull BloodRequestUI newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull BloodRequestUI oldItem,
                                              @NonNull BloodRequestUI newItem) {
                return oldItem.getStatus().equals(newItem.getStatus()) &&
                        oldItem.getHospitalName().equals(newItem.getHospitalName());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_request, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hospitalName;
        private final TextView bloodType;
        private final TextView address;
        private final TextView phoneNumber;
        private final MaterialButton acceptButton;
        private final MaterialButton rejectButton;

        ViewHolder(View itemView, OnRequestActionListener listener) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            bloodType = itemView.findViewById(R.id.bloodType);
            address = itemView.findViewById(R.id.address);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);

            // Set up button click listeners
            acceptButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAcceptRequest(getItem(position));
                }
            });

            rejectButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRejectRequest(getItem(position));
                }
            });
        }

        void bind(BloodRequestUI request) {
            // Set text fields
            hospitalName.setText(request.getHospitalName());
            bloodType.setText(request.getBloodTypeDisplay());
            address.setText(request.getAddress());
            phoneNumber.setText(request.getContactNumber());

            // Handle button visibility based on status
            boolean isPending = request.isPending();
            acceptButton.setVisibility(isPending ? View.VISIBLE : View.GONE);
            rejectButton.setVisibility(isPending ? View.VISIBLE : View.GONE);

            // Style blood type background
            bloodType.setBackgroundResource(R.drawable.bg_blood_type);

            // Set icons for address and phone
            address.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_location, 0, 0, 0);
            phoneNumber.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_phone, 0, 0, 0);

            // Style buttons
            acceptButton.setIcon(ContextCompat.getDrawable(
                    itemView.getContext(), R.drawable.ic_accept));
            rejectButton.setIcon(ContextCompat.getDrawable(
                    itemView.getContext(), R.drawable.ic_reject));

            // Optional: Set button colors
            acceptButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), R.color.green)));
            rejectButton.setStrokeColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), R.color.red_500)));
            rejectButton.setTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), R.color.red_500)));
        }

        private BloodRequestUI getItem(int position) {
            return ((BloodRequestAdapter) getBindingAdapter()).getCurrentList().get(position);
        }
    }
}
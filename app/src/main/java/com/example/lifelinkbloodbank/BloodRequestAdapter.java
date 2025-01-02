package com.example.lifelinkbloodbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            public boolean areItemsTheSame(@NonNull BloodRequest oldItem, @NonNull BloodRequest newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull BloodRequest oldItem, @NonNull BloodRequest newItem) {
                return oldItem.getHospitalName().equals(newItem.getHospitalName()) &&
                        oldItem.getPatientName().equals(newItem.getPatientName()) &&
                        oldItem.getBloodType().equals(newItem.getBloodType()) &&
                        oldItem.getVolume() == newItem.getVolume() &&
                        oldItem.getStatus().equals(newItem.getStatus());
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
        private final TextView patientName;
        private final TextView bloodType;
        private final TextView volume;
        private final TextView contactNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            patientName = itemView.findViewById(R.id.patientName);
            bloodType = itemView.findViewById(R.id.bloodType);
            volume = itemView.findViewById(R.id.volume);
            contactNumber = itemView.findViewById(R.id.contactNumber);
        }

        public void bind(BloodRequest request) {
            hospitalName.setText(request.getHospitalName());
            patientName.setText(request.getPatientName());
            bloodType.setText(request.getBloodType());
            volume.setText(request.getVolume() + " units");
            contactNumber.setText(request.getContactNumber());
        }
    }
}